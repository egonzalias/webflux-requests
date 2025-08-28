package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoanTypeRepository;
import co.com.crediya.model.loanrequest.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class LoanRequestUseCase {

    private final LoanRequestRepository repository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserRepository userRepository;

    public Mono<Void> loanRequest(LoanRequest loanRequest){
        loanRequest.setCreatedAt(LocalDateTime.now());

        Mono<LoanStatus> statusMono = loanStatusRepository.findStatusByCode("PEND")
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El estado 'PEND' es incorrecto o no existe en la base de datos."))));

        Mono<LoanType> loanTypeMono = loanTypeRepository.findByCode(loanRequest.getLoanTypeCode().getName())
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El tipo de préstamo '"+loanRequest.getLoanTypeCode().getName()+"' es incorrecto o no existe en la base de datos."))));

        /*Mono<User> userMono = userRepository.findByDocumentNumber(loanRequest.getDocumentNumber())
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El Usuario debe estar registrado previamente para poder crear la solicitud de prestamo"))));*/

        return Mono.zip(statusMono, loanTypeMono/*, userMono*/)
                .flatMap(tuple -> {
                    LoanStatus status = tuple.getT1();
                    LoanType loanType = tuple.getT2();

                    loanRequest.setLoanStatus(status);
                    loanRequest.setLoanTypeCode(loanType);

                    //return repository.loanRequest(loanRequest);
                    //see to implement to handle better the exception
                    return repository.loanRequest(loanRequest).onErrorMap( throwable -> {
                        if(isForeignKeyViolation(throwable)){
                            return new ValidationException(List.of("El Usuario debe estar registrado previamente para poder crear la solicitud de prestamo."));
                        }
                        return throwable;
                    });
                });
    }

    private boolean isForeignKeyViolation(Throwable throwable) {
        //if (throwable instanceof R2dbcDataIntegrityViolationException) {
        String msg = throwable.getMessage();
        return msg != null && msg.contains("violates foreign key constraint \"fk_user\"");
    }
}
