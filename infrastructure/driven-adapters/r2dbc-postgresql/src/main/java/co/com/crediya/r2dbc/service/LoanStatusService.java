package co.com.crediya.r2dbc.service;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.r2dbc.cache.LoanStatusCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoanStatusService {
    private final LoanStatusCache cache;

    public Mono<LoanStatus> getStatusByCode(String code) {
        return cache.findByCode(code);
    }
}
