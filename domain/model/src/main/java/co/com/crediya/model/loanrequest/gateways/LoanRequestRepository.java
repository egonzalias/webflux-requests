package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.ActiveLoan;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanRequestRepository {
    Mono<LoanRequest> loanRequest(LoanRequest loanRequest);
    Flux<LoanRequest> findByStatus(Long codeStatus, int page, int offset);
    Flux<LoanRequestSummary> findLoanRequestsByStatusIn(List<Long> statusIds, int size, int offset);
    Mono<Long> countLoanRequestByStatusIn(List<Long> statusIds);
    Mono<LoanRequest> findLoanById(Long id);
    Mono<Void> updateloanRequest(Long id, Long statusId);
    Mono<LoanRequestSummary> findLoanRequestsById(Long id);
    Flux<ActiveLoan> findLoansByUserAndStatus(String documentNumber, String statusCode);
}
