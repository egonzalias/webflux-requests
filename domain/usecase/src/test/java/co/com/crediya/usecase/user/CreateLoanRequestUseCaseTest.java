package co.com.crediya.usecase.user;


import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoanTypeRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateLoanRequestUseCaseTest {

    @Mock
    private LoanRequestRepository repository;
    @Mock
    private LoanStatusRepository loanStatusRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    LoggerService loggerService;

    private LoanType loanType;
    private LoanStatus loanStatus;
    private LoanRequest loanRequest;
    private CreateLoanRequestUseCase createLoanRequestUseCase;



    @BeforeEach
    void setup(){
        loanStatus = new LoanStatus(1L, "PEND", "Pendiente de revision");
        loanType = new LoanType(1L,
                "LIBRE",
                "Prestamos personal para libre inversion",
                new BigDecimal(1000),
                new BigDecimal(5000),
                new BigDecimal("0.05"),
                false);
        loanRequest = new LoanRequest(1L,
                "112233",
                BigDecimal.valueOf(1400000),
                6,
                loanType,
                loanStatus,
                LocalDate.of(1995, 5, 20).atTime(LocalTime.now()));

        createLoanRequestUseCase = new CreateLoanRequestUseCase(repository, loanStatusRepository, loanTypeRepository, loggerService);
    }

    @Test
    void shouldRequestLoanSuccessfully() {
        when(loanStatusRepository.findStatusByCode("PEND")).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode("LIBRE")).thenReturn(Mono.just(loanType));
        when(repository.loanRequest(loanRequest)).thenReturn(Mono.empty());

        createLoanRequestUseCase.loanRequest(loanRequest).as(StepVerifier::create).verifyComplete();

        verify(loanStatusRepository).findStatusByCode("PEND");
        verify(loanTypeRepository).findByCode(loanRequest.getLoanTypeCode().getName());
        verify(repository).loanRequest(loanRequest);
    }

    @Test
    void shouldFailWhenLoanStatusPendIsInvalid() {
        when(loanStatusRepository.findStatusByCode("PEND")).thenReturn(Mono.empty());
        when(loanTypeRepository.findByCode("LIBRE")).thenReturn(Mono.just(loanType));

        createLoanRequestUseCase.loanRequest(loanRequest).as(StepVerifier::create)
                        .expectErrorSatisfies(error ->{
                            ValidationException ve = (ValidationException) error;
                            Assertions.assertInstanceOf(ValidationException.class, error);
                            Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El estado 'PEND' es incorrecto o no existe en la base de datos")));
                        }).verify();
    }

    @Test
    void shouldFailWhenLoanTypeIsInvalid() {
        when(loanStatusRepository.findStatusByCode("PEND")).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode("LIBRE")).thenReturn(Mono.empty());

        createLoanRequestUseCase.loanRequest(loanRequest).as(StepVerifier::create)
                .expectErrorSatisfies(error ->{
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertInstanceOf(ValidationException.class, error);
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El tipo de préstamo")));
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("es incorrecto o no existe en la base de datos")));
                }).verify();
    }

}
