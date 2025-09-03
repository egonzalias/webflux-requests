package co.com.crediya.usecase.user;

import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetLoanRequestUseCase {

    private final LoanRequestRepository repository;
    private final LoggerService logger;

    public Flux<LoanRequest> getLoanRequestsByStatus(int codeStatus, int page, int size){
        int offset = page * size;
        return repository.findByStatus(codeStatus, size, offset);
    }

}
