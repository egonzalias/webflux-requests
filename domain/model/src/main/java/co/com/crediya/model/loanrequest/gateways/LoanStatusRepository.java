package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.LoanStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanStatusRepository {
    Mono<LoanStatus> findStatusByCode(String code);
    Flux<LoanStatus> findStatusByCodes(List<String> codes);
}
