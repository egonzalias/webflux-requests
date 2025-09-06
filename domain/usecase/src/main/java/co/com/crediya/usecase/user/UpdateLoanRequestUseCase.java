package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequestUpdateStatus;
import co.com.crediya.model.loanrequest.gateways.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.List;

@RequiredArgsConstructor
public class UpdateLoanRequestUseCase {

    private final LoanRequestRepository repository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoggerService logger;
    private final SqsService sqsService;

    public Mono<Void> updateLoanStatus(LoanRequestUpdateStatus loanRequestUpdateStatus, String queueName){
        String statusCode = loanRequestUpdateStatus.getStatus();
        Long id = loanRequestUpdateStatus.getId();

        return loanStatusRepository.findStatusByCode(statusCode)
                .doOnNext(status -> logger.debug("Loan status found: {}", status))
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El estado '"+statusCode+"' es incorrecto o no existe en la base de datos."))))
                .flatMap(loanStatus -> {
                    return repository.findLoanById(id)
                            .switchIfEmpty(Mono.error(new ValidationException(List.of("La solicitud de prestamo con ID: "+id+" no existe en la base de datos."))))
                            .flatMap(loanRequest -> {
                                Long previousStatusId = loanRequest.getLoanStatus().getId();
                                return repository.updateloanRequest(id, loanStatus.getId())
                                        .then(sqsService.sendMessage("Hola EGR", queueName)
                                                .onErrorResume(error -> {
                                                    logger.error("Error enviando mensaje a SQS, realizando rollback.", error);
                                                    return repository.updateloanRequest(id, previousStatusId)
                                                            .doOnSuccess(avoid ->logger.info("La solicitud de prestamo con ID: {} fue actualizada a su estado original.", id))
                                                            .then(Mono.error(new ValidationException(List.of("Error enviando mensaje a SQS, realizando rollback."))));
                                                }));
                            });
                });
    }


}
