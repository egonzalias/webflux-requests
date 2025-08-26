package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
    Mono<UserEntity> findByDocumentNumber(String documentNumber);
}
