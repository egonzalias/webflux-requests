package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanRequestRepository {
    Mono<Void> loanRequest(LoanRequest loanRequest);
    Flux<LoanRequest> findByStatus(Long codeStatus, int page, int offset);
    Flux<LoanRequestSummary> findLoanRequestsByStatusIn(List<Long> statusIds, int size, int offset);
    Mono<Long> countLoanRequestByStatusIn(List<Long> statusIds);
}
