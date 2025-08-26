package co.com.crediya.r2dbc.repository;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.r2dbc.entity.LoanStatusEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LoanStatusRepository extends ReactiveCrudRepository<LoanStatusEntity, Long> {
    Mono<LoanStatusEntity> findByCode(String code);
}
