package co.com.crediya.r2dbc.adapter.impl;

import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.gateways.LoanTypeRepository;
import co.com.crediya.r2dbc.service.LoanTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class LoanTypeAdapter implements LoanTypeRepository {

    private final LoanTypeService loanTypeService;


    @Override
    public Mono<LoanType> findByCode(String code) {
        return loanTypeService.getStatusByCode(code);
    }
}
