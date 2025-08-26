package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Void> registerUser(User user);
    Mono<Boolean> findByEmail(String email);
    String getTest();
}
