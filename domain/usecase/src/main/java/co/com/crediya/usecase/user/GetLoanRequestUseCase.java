package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetLoanRequestUseCase {

    private final LoanStatusRepository loanStatusRepository;
    private final LoanRequestRepository repository;
    private final LoggerService logger;

    /*public Flux<LoanRequest> getLoanRequestsByStatus(String codeStatus, int page, int size){
        int offset = page * size;

        return loanStatusRepository.findStatusByCode(codeStatus)
                //.doOnNext(status -> logger.debug("Loan status found: {}", status))
                .switchIfEmpty(Mono.error(new ValidationException(List.of("El estado 'PEND' es incorrecto o no existe en la base de datos."))))
                        .flatMap(status -> repository.findByStatus(,size, offset )));

        //return repository.findByStatus(codeStatus, size, offset);
    }*/

    public Flux<LoanRequest> getLoanRequestsByStatus(String codeStatus, int page, int size) {
        int offset = page * size;

        return loanStatusRepository.findStatusByCode(codeStatus)
                .switchIfEmpty(Mono.error(new ValidationException(
                        List.of("El estado '" + codeStatus + "' es incorrecto o no existe en la base de datos.")
                )))
                .flatMapMany(status ->
                        repository.findByStatus(status.getId(), size, offset)
                );
    }


}
