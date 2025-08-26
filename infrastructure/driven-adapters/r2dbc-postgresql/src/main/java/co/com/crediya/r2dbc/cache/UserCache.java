package co.com.crediya.r2dbc.cache;

import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.User;
import co.com.crediya.r2dbc.mapper.LoanTypeMapper;
import co.com.crediya.r2dbc.mapper.UserMapper;
import co.com.crediya.r2dbc.repository.LoanTypeRepository;
import co.com.crediya.r2dbc.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class UserCache {

    /*private final UserRepository repository;
    // Use ConcurrentHashMap for thread-safe access in concurrent environments (e.g., reactive applications)
    private final Map<String, User> cache = new ConcurrentHashMap<>();
    private final UserMapper mapper;

    @PostConstruct
    public void preloadCache() {
        repository.findAll()
                .map(mapper::toModel)
                .doOnNext(user -> cache.put(user.getDocument_number(), user))
                .subscribe();
    }

    public Mono<User> findByDocumentNumber(String documentNumber) {
        User cached = cache.get(documentNumber);
        if (cached != null) return Mono.just(cached);
        return repository.findByDocumentNumber(documentNumber)
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(documentNumber, status));
    }*/
}
