package co.com.crediya.r2dbc.service;

import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.User;
import co.com.crediya.r2dbc.cache.LoanTypeCache;
import co.com.crediya.r2dbc.cache.UserCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    /*private final UserCache cache;

    public Mono<User> findByDocumentNumber(String code) {
        return cache.findByDocumentNumber(code);
    }*/
}
