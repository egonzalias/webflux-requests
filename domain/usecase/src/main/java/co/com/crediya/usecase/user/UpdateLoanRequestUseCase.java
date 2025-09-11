package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequestUpdateStatus;
import co.com.crediya.model.loanrequest.MessageBody;
import co.com.crediya.model.loanrequest.enums.LoanStatusEnum;
import co.com.crediya.model.loanrequest.gateways.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.List;

@RequiredArgsConstructor
public class UpdateLoanRequestUseCase {

    private final LoanRequestRepository repository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoggerService logger;
    private final SqsService sqsService;

    public Mono<Void> updateLoanStatus(LoanRequestUpdateStatus loanRequestUpdateStatus, String queueName) {
        String statusCode = loanRequestUpdateStatus.getStatus();
        Long id = loanRequestUpdateStatus.getId();

        return loanStatusRepository.findStatusByCode(statusCode)
                .doOnNext(status -> logger.debug("Loan status found: {}", status))
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El estado '" + statusCode + "' es incorrecto o no existe en la base de datos."))))
                .flatMap(loanStatus -> {
                    String newStatusDescription = loanStatus.getDescription();

                    return repository.findLoanRequestsById(id)
                            .switchIfEmpty(Mono.error(new ValidationException(List.of("La solicitud de préstamo con ID: " + id + " no existe en la base de datos."))))
                            .flatMap(loanRequest -> {
                                Long previousStatusId = loanRequest.getLoanStatus().getId();

                                Mono<Void> updateMono = repository.updateloanRequest(id, loanStatus.getId());

                                if (LoanStatusEnum.APROB.name().equals(statusCode) || LoanStatusEnum.RECH.name().equals(statusCode)) {
                                    logger.info("Loan status updated to '{}', preparing to send message to {}.", statusCode, queueName);

                                    return updateMono.then(
                                            sqsService.sendMessage(
                                                    new MessageBody(
                                                            String.valueOf(id),
                                                            newStatusDescription,
                                                            loanRequest.getEmail(),
                                                            loanRequest.getFirstName() + " " + loanRequest.getLastName()
                                                    ),
                                                    queueName
                                            ).doOnSuccess(ignored ->
                                                    logger.info("Message for loan ID {} was successfully sent to queue {}", id, queueName)
                                            ).onErrorResume(error -> {
                                                logger.error("Error sending message to SQS. Performing rollback...", error);
                                                return repository.updateloanRequest(id, previousStatusId)
                                                        .doOnSuccess(avoid -> logger.info("Rolled back status update for loan ID {}", id))
                                                        .then(Mono.error(new ValidationException(List.of("Error enviando mensaje a SQS, se realizó rollback."))));
                                            })
                                    );
                                }else{
                                    logger.info("Loan status updated but message was NOT sent to the queue because status is '{}'", statusCode);
                                    return updateMono;
                                }
                            });
                });
    }



}
