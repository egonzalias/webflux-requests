package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LoanTypeRepository extends ReactiveCrudRepository<LoanTypeEntity, Long> {
    Mono<LoanTypeEntity> findByName(String code);
}
