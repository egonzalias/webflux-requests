package co.com.crediya.r2dbc.adapter;

import co.com.crediya.r2dbc.entity.LoanRequestEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface RequestReactiveRepository extends ReactiveCrudRepository<LoanRequestEntity, String>, ReactiveQueryByExampleExecutor<LoanRequestEntity> {

}
