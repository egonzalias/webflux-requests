package co.com.crediya.r2dbc.service;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.r2dbc.cache.LoanStatusCache;
import co.com.crediya.r2dbc.cache.LoanTypeCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoanTypeService {
    private final LoanTypeCache cache;

    public Mono<LoanType> getStatusByCode(String code) {
        return cache.findByCode(code);
    }
}
