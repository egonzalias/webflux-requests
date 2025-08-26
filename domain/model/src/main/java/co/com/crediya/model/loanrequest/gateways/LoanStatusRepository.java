package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.LoanStatus;
import reactor.core.publisher.Mono;

public interface LoanStatusRepository {
    Mono<LoanStatus> findStatusByCode(String code);
}
