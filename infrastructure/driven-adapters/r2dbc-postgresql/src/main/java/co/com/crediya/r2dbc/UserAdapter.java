package co.com.crediya.r2dbc;

import co.com.crediya.model.loanrequest.User;
import co.com.crediya.model.loanrequest.gateways.UserRepository;
import co.com.crediya.r2dbc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private final UserService userService;

    @Override
    public Mono<User> findByDocumentNumber(String documentNumber) {
        //return userService.findByDocumentNumber(documentNumber);
        return null;
    }
}
