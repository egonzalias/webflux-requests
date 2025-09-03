package co.com.crediya.r2dbc.cache;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.r2dbc.mapper.LoanStatusMapper;
import co.com.crediya.r2dbc.repository.LoanStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoanStatusCache {

    private final LoanStatusRepository repository;
    // Use ConcurrentHashMap for thread-safe access in concurrent environments (e.g., reactive applications)
    private final Map<String, LoanStatus > cache = new ConcurrentHashMap<>();
    private final LoanStatusMapper mapper;

    @PostConstruct
    public void preloadCache() {
        repository.findAll()
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(status.getCode(), status))
                .subscribe();
    }

    public Mono<LoanStatus> findByCode(String code) {
        LoanStatus cached = cache.get(code);
        if (cached != null) return Mono.just(cached);
        return repository.findByCode(code)
                .map(mapper::toModel)
                .doOnNext(status -> cache.put(code, status));
    }

    public Flux<LoanStatus> findByCodes(List<String> codes) {
        return Flux.fromIterable(codes)
                .flatMap(this::findByCode);
    }
}
