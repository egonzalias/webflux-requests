package co.com.crediya.r2dbc.adapter.impl;

import co.com.crediya.model.loanrequest.ActiveLoan;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.r2dbc.adapter.RequestReactiveRepository;
import co.com.crediya.r2dbc.entity.LoanRequestEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.LoanRequestMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanRequest, LoanRequestEntity, String, RequestReactiveRepository
> implements LoanRequestRepository {

    private final TransactionalOperator transactionalOperator;
    private final ObjectMapper mapper;
    private final LoanRequestMapper loanRequestMapper;

    public MyReactiveRepositoryAdapter(RequestReactiveRepository repository, ObjectMapper mapper,
                                       TransactionalOperator transactionalOperator, LoanRequestMapper loanRequestMapper) {
        super(repository, mapper, d -> mapper.map(d, LoanRequest.class/* change for domain model */));
        this.transactionalOperator = transactionalOperator;
        this.mapper = mapper;
        this.loanRequestMapper = loanRequestMapper;
    }

    @Override
    public Mono<LoanRequest> loanRequest(LoanRequest loanRequest) {
        LoanRequestEntity entity = mapper.map(loanRequest, LoanRequestEntity.class);
        entity.setStatus(loanRequest.getLoanStatus().getId());
        entity.setLoanType(loanRequest.getLoanTypeCode().getId());
        entity.setDocumentNumber(loanRequest.getDocumentNumber());
        return transactionalOperator.execute(tx ->
                repository.save(entity)
                        .map(savedEntity -> mapper.map(savedEntity, LoanRequest.class))
                )
                .single();
    }

    @Override
    public Flux<LoanRequest> findByStatus(Long codeStatus, int size, int offset) {
        //Pageable pageable = PageRequest.of(page, offset);
        return repository.findByStatus(codeStatus, size, offset)// debug antes del map
                .map(loanRequestMapper::toDomain);
    }

    @Override
    public Flux<LoanRequestSummary> findLoanRequestsByStatusIn(List<Long> statusIds, int size, int offset) {
        return repository.findLoanRequestsByStatuses(statusIds, size, offset)
                .doOnNext(entity -> System.out.println("Found EGR: " + entity)) // debug antes del map
                .map(loanRequestMapper::toDomainExtend);
    }

    @Override
    public Mono<Long> countLoanRequestByStatusIn(List<Long> statusIds) {
        return repository.countLoanRequestsByStatusIn(statusIds).defaultIfEmpty(0L);
    }

    @Override
    public Mono<LoanRequest> findLoanById(Long id) {
        return repository.findById(String.valueOf(id))
                .map(loanRequestMapper::toDomain);
    }

    @Override
    public Mono<Void> updateloanRequest(Long id, Long statusId) {
        return transactionalOperator.execute(tx -> repository.updateStatusById(id, statusId).then()).then();
    }

    @Override
    public Mono<LoanRequestSummary> findLoanRequestsById(Long id) {
        return repository.findLoanRequestsById(id)
                .doOnNext(entity -> System.out.println("Found EGR: " + entity)) // debug antes del map
                .map(loanRequestMapper::toDomainExtend);
    }

    @Override
    public Flux<ActiveLoan> findLoansByUserAndStatus(String documentNumber, String statusCode) {
        return repository.findLoansByUserAndStatus(documentNumber, statusCode)
                .doOnNext(data -> System.out.println(data));
    }

}
