package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.*;
import co.com.crediya.model.loanrequest.enums.LoanStatusEnum;
import co.com.crediya.model.loanrequest.gateways.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CreateLoanRequestUseCase {

    private final LoanRequestRepository repository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final SqsService sqsService;
    private final LoggerService logger;

    public Mono<Void> loanRequest(LoanRequest loanRequest, String queueName){
        loanRequest.setCreatedAt(LocalDateTime.now());

        //Executes both Monos in parallel and combines their results when complete
        return Mono.zip(getLoanStatus(), getLoanType(loanRequest))
                .flatMap(tuple -> {
                    LoanStatus status = tuple.getT1();
                    LoanType loanType = tuple.getT2();

                    loanRequest.setLoanStatus(status);
                    loanRequest.setLoanTypeCode(loanType);

                    logger.info("Saving loan request for user: {} with type: {}",
                            loanRequest.getDocumentNumber(),
                            loanRequest.getLoanTypeCode().getName());

                    return repository.loanRequest(loanRequest)
                            .flatMap(savedLoan -> {
                                logger.info("Loan request was saved with ID {}", savedLoan.getId());
                                logger.info("The automatic validation is {} ", loanType.isAutomaticValidation());
                                if (!loanType.isAutomaticValidation()) {
                                    logger.info("The request wasn't sent to the queue because the loan type automatic validation is false");
                                    return Mono.empty();
                                } else{
                                    String generatedId = String.valueOf(savedLoan.getId());
                                    return repository.findLoansByUserAndStatus(loanRequest.getDocumentNumber(), LoanStatusEnum.APROB.name())
                                            .collectList()
                                            .flatMap(activeLoans -> {
                                                logger.info("There are {} active loans for the user {}", activeLoans.size(), loanRequest.getDocumentNumber());
                                                return sqsService.sendMessage(
                                                        new CalculationRequest(
                                                                loanRequest.getDocumentNumber(),
                                                                generatedId,
                                                                loanRequest.getAmount(),
                                                                BigDecimal.valueOf(8000000),
                                                                loanRequest.getTermInMonths(),
                                                                loanType.getInterestRate(),
                                                                activeLoans
                                                        ),
                                                        queueName);

                                            }).doOnSuccess( ignored ->
                                                    logger.info("The Loan request with ID {} for the user {} was sent to the {} ", generatedId, loanRequest.getDocumentNumber(), queueName));
                                }
                            })
                            .onErrorMap( throwable -> {
                                if(isForeignKeyViolation(throwable)){
                                    return new ValidationException(List.of("El Usuario debe estar registrado previamente para poder crear la solicitud de prestamo."));
                                }
                                logger.error("Unexpected error saving loan request", throwable);
                                return throwable;
                            }).then();
                });
    }

    private Mono<LoanStatus> getLoanStatus(){
        return loanStatusRepository.findStatusByCode(LoanStatusEnum.PEND.name())
                .doOnNext(status -> logger.debug("Loan status found: {}", status))
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El estado 'PEND' es incorrecto o no existe en la base de datos."))));
    }

    private Mono<LoanType> getLoanType(LoanRequest loanRequest){
        return loanTypeRepository.findByCode(loanRequest.getLoanTypeCode().getName())
                .doOnNext(type -> logger.debug("Loan type found: {}", type))
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El tipo de préstamo '"+loanRequest.getLoanTypeCode().getName()+"' es incorrecto o no existe en la base de datos."))));
    }

    private boolean isForeignKeyViolation(Throwable throwable) {
        //if (throwable instanceof R2dbcDataIntegrityViolationException) {
        String msg = throwable.getMessage();
        return msg != null && msg.contains("violates foreign key constraint \"fk_user\"");
    }
}
