package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.LoanRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanRequestRepository {
    Mono<Void> loanRequest(LoanRequest loanRequest);
    Flux<LoanRequest> findByStatus(Long codeStatus, int page, int offset);
}
