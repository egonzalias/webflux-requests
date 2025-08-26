package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.r2dbc.repository.LoanStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanRequestUseCaseTest {

    @Mock
    private LoanRequestRepository repository;

    private LoanRequest loanRequest;
    private LoanRequestUseCase loanRequestUseCase;
    private LoanStatusService loanStatusService;

    /*@BeforeEach
    void setup(){
        loanRequest = new LoanRequest("112233", BigDecimal.valueOf(1400000), 6, LocalDate.of(1995, 5, 20),"123 Main St","+1234567890","john2.doe@example.com", );
        loanRequestUseCase = new LoanRequestUseCase(repository, loanStatusService);
    }

    @Test
    void shouldRegisterUser_whenEmailNotExists() {
        when(repository.findByEmail(loanRequest.getEmail())).thenReturn(Mono.just(false));
        when(repository.loanRequest(loanRequest)).thenReturn(Mono.empty());
        StepVerifier.create(loanRequestUseCase.registerUser(loanRequest)).verifyComplete();
        verify(repository).loanRequest(loanRequest);
    }

    @Test
    void shouldThrowError_whenEmailAlreadyExists() {
        when(repository.findByEmail(loanRequest.getEmail())).thenReturn(Mono.just(true));
        StepVerifier.create(loanRequestUseCase.registerUser(loanRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                ((ValidationException) throwable).getErrors() != null &&
                                ((ValidationException) throwable).getErrors().contains("El correo electrónico ya está registrado")
                )
                .verify();

        verify(repository, never()).loanRequest(any());
    }*/
}
