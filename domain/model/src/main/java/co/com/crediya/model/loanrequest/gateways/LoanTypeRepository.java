package co.com.crediya.model.loanrequest.gateways;



import co.com.crediya.model.loanrequest.LoanType;
import reactor.core.publisher.Mono;


public interface LoanTypeRepository {
    Mono<LoanType> findByCode(String code);
}
