package co.com.crediya.r2dbc;

import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.r2dbc.entity.LoanRequestEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanRequest, LoanRequestEntity, String, MyReactiveRepository
> implements LoanRequestRepository {

    private final TransactionalOperator transactionalOperator;
    private final ObjectMapper mapper;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanRequest.class/* change for domain model */));
        this.transactionalOperator = transactionalOperator;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> loanRequest(LoanRequest loanRequest) {
        LoanRequestEntity entity = mapper.map(loanRequest, LoanRequestEntity.class);
        entity.setStatus(loanRequest.getLoanStatus().getId());
        entity.setLoanType(loanRequest.getLoanTypeCode().getId());
        entity.setDocumentNumber(loanRequest.getDocumentNumber());
        return transactionalOperator.execute(tx -> repository.save(entity).then()).then();
    }

}
