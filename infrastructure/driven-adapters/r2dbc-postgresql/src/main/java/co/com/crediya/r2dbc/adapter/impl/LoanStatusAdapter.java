package co.com.crediya.r2dbc.adapter.impl;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.r2dbc.service.LoanStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class LoanStatusAdapter implements LoanStatusRepository {

    private final LoanStatusService loanStatusService;

    @Override
    public Mono<LoanStatus> findStatusByCode(String code) {
        return loanStatusService.getStatusByCode(code);
    }


}
