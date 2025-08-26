package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository repository;

    public Mono<Void> registerUser(User user){
        return  repository.findByEmail(user.getEmail()).flatMap(exists ->{
            if(exists){
                throw new ValidationException(List.of("El correo electrónico ya está registrado"));
            } else {
                return repository.registerUser(user).then();
            }
        });


    }

    public String test(){
        return "Hello!!! GONZA!!!";
    }
}
