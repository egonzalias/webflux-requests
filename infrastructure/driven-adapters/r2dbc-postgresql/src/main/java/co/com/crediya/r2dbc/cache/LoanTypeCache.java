package co.com.crediya.r2dbc.cache;

import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.r2dbc.mapper.LoanTypeMapper;
import co.com.crediya.r2dbc.repository.LoanTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoanTypeCache {

    private final LoanTypeRepository repository;
    // Use ConcurrentHashMap for thread-safe access in concurrent environments (e.g., reactive applications)
    private final Map<String, LoanType > cache = new ConcurrentHashMap<>();
    private final LoanTypeMapper mapper;

    @PostConstruct
    public void preloadCache() {
        repository.findAll()
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(status.getName(), status))
                .subscribe();
    }

    public Mono<LoanType> findByName(String code) {
        LoanType cached = cache.get(code);
        if (cached != null) return Mono.just(cached);
        return repository.findByName(code)
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(code, status));
    }
}
