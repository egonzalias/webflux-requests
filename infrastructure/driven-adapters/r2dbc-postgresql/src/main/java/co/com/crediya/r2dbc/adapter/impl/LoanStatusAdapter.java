package co.com.crediya.r2dbc.adapter.impl;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.r2dbc.service.LoanStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoanStatusAdapter implements LoanStatusRepository {

    private final LoanStatusService loanStatusService;

    @Override
    public Mono<LoanStatus> findStatusByCode(String code) {
        return loanStatusService.getStatusByCode(code);
    }

    @Override
    public Flux<LoanStatus> findStatusByCodes(List<String> codes) {
        return loanStatusService.getStatusesByCodes(codes);
    }

}
