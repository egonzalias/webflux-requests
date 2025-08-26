package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {
    @Query("SELECT * from 'users'")
    String getTesting();
    @Query("SELECT EXISTS (SELECT 1 FROM users WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);
}
