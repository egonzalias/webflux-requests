package co.com.crediya.usecase.user;


import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.enums.LoanStatusEnum;
import co.com.crediya.model.loanrequest.enums.LoanTypeEnum;
import co.com.crediya.model.loanrequest.gateways.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.sound.midi.SysexMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Mock
    private SqsService sqsService;

    private LoanType loanType;
    private LoanStatus loanStatus;
    private LoanRequest loanRequest;
    private CreateLoanRequestUseCase createLoanRequestUseCase;
    private final String queueName = "test-queue-name";



    @BeforeEach
    void setup(){
        loanStatus = new LoanStatus(1L, LoanStatusEnum.PEND.name(), "Pendiente de revision");
        loanType = new LoanType(1L,
                LoanTypeEnum.LIBRE.name(),
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

        createLoanRequestUseCase = new CreateLoanRequestUseCase(repository, loanStatusRepository, loanTypeRepository, sqsService, loggerService);
    }

    @Test
    void shouldRequestLoanSuccessfully() {
        when(loanStatusRepository.findStatusByCode(LoanStatusEnum.PEND.name())).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode(LoanTypeEnum.LIBRE.name())).thenReturn(Mono.just(loanType));
        when(repository.loanRequest(loanRequest)).thenReturn(Mono.just(loanRequest));

        createLoanRequestUseCase.loanRequest(loanRequest, queueName).as(StepVerifier::create).verifyComplete();

        verify(loanStatusRepository).findStatusByCode(LoanStatusEnum.PEND.name());
        verify(loanTypeRepository).findByCode(loanRequest.getLoanTypeCode().getName());
        verify(repository).loanRequest(loanRequest);
    }

    @Test
    void shouldFailWhenLoanStatusPendIsInvalid() {
        when(loanStatusRepository.findStatusByCode(LoanStatusEnum.PEND.name())).thenReturn(Mono.empty());
        when(loanTypeRepository.findByCode(LoanTypeEnum.LIBRE.name())).thenReturn(Mono.just(loanType));

        createLoanRequestUseCase.loanRequest(loanRequest, queueName).as(StepVerifier::create)
                        .expectErrorSatisfies(error ->{
                            ValidationException ve = (ValidationException) error;
                            Assertions.assertInstanceOf(ValidationException.class, error);
                            assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El estado 'PEND' es incorrecto o no existe en la base de datos")));
                        }).verify();
    }

    @Test
    void shouldSendToSqsIfAutomaticValidationIsTrue() {
        LoanType autoLoanType = new LoanType(
                2L, LoanTypeEnum.VEHIC.name(),
                "Vehiculo",
                new BigDecimal(1000),
                new BigDecimal(5000),
                new BigDecimal("0.05"),
                true
        );
        loanRequest.setLoanTypeCode(autoLoanType);

        when(loanStatusRepository.findStatusByCode(LoanStatusEnum.PEND.name())).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode(LoanTypeEnum.VEHIC.name())).thenReturn(Mono.just(autoLoanType));
        when(repository.loanRequest(any())).thenReturn(Mono.just(loanRequest));
        when(repository.findLoansByUserAndStatus(anyString(), anyString())).thenReturn(Flux.empty());
        when(sqsService.sendMessage(any(), any())).thenReturn(Mono.empty());

        createLoanRequestUseCase.loanRequest(loanRequest, "test-queue")
                .as(StepVerifier::create)
                .verifyComplete();

        verify(sqsService).sendMessage(any(), eq("test-queue"));
    }

    @Test
    void shouldNotSendToSqsWhenAutomaticValidationIsFalse() {
        loanRequest.setLoanTypeCode(loanType);

        lenient().when(loanStatusRepository.findStatusByCode(LoanStatusEnum.PEND.name())).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode(LoanTypeEnum.LIBRE.name())).thenReturn(Mono.just(loanType));
        lenient().when(repository.findLoansByUserAndStatus(anyString(), anyString())).thenReturn(Flux.empty());
        when(repository.loanRequest(any())).thenReturn(Mono.just(loanRequest));

        createLoanRequestUseCase.loanRequest(loanRequest, queueName)
                .as(StepVerifier::create)
                .verifyComplete();

        verify(sqsService, never()).sendMessage(any(), anyString());
    }


    @Test
    void shouldFailWhenLoanTypeIsInvalid() {
        when(loanStatusRepository.findStatusByCode("PEND")).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode("LIBRE")).thenReturn(Mono.empty());

        createLoanRequestUseCase.loanRequest(loanRequest, queueName).as(StepVerifier::create)
                .expectErrorSatisfies(error ->{
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertInstanceOf(ValidationException.class, error);
                    assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El tipo de préstamo")));
                    assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("es incorrecto o no existe en la base de datos")));
                }).verify();
    }

    @Test
    void shouldHandleForeignKeyViolationException() {
        when(loanStatusRepository.findStatusByCode(LoanStatusEnum.PEND.name())).thenReturn(Mono.just(loanStatus));
        when(loanTypeRepository.findByCode(LoanTypeEnum.LIBRE.name())).thenReturn(Mono.just(loanType));
        lenient().when(repository.findLoansByUserAndStatus(anyString(), anyString())).thenReturn(Flux.empty());

        when(repository.loanRequest(any())).thenReturn(
                Mono.error(new RuntimeException("violates foreign key constraint \"fk_user\""))
        );

        StepVerifier.create(createLoanRequestUseCase.loanRequest(loanRequest, queueName))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);

                    ValidationException ve = (ValidationException) error;
                    assertTrue(ve.getErrors().stream().anyMatch( msg -> msg.contains("El Usuario debe estar registrado previamente para poder crear la solicitud de prestamo")));
                })
                .verify();
    }



}
