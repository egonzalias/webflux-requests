package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.User;
import reactor.core.publisher.Mono;


public interface UserRepository  {
    Mono<User> findByDocumentNumber(String documentNumber);
}
