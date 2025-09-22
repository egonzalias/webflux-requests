package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.*;
import co.com.crediya.model.loanrequest.enums.LoanStatusEnum;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import co.com.crediya.model.loanrequest.gateways.SqsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateLoanRequestUseCaseTest {


    @Mock
    private LoanStatusRepository loanStatusRepository;
    @Mock
    private LoanRequestRepository repository;
    @Mock
    private SqsService sqsService;
    @Mock
    LoggerService loggerService;

    private UpdateLoanRequestUseCase updateLoanRequestUseCase;


    private final Long loanRequestId = 1L;
    private final String statusCode = LoanStatusEnum.APROB.name();
    private final String queueName = "test-queue";
    private final String queueLoanApprovedReports = "test-queue-approved-loans";

    private LoanRequestUpdateStatus loanRequestUpdateStatus;

    @BeforeEach
    void setUp() {
        loanRequestUpdateStatus = new LoanRequestUpdateStatus();
        loanRequestUpdateStatus.setId(loanRequestId);
        loanRequestUpdateStatus.setStatus(statusCode);

        updateLoanRequestUseCase = new UpdateLoanRequestUseCase(repository, loanStatusRepository, loggerService, sqsService);
    }

    @Test
    void shouldUpdateLoanStatusSuccessfully() {
        LoanStatus newStatus = new LoanStatus(2L, statusCode, "APROBADA");
        LoanStatus previousStatus = new LoanStatus(1L, "PENDING", "Pendiente de revision");

        LoanRequestSummary loanRequestSummary = new LoanRequestSummary();
        loanRequestSummary.setId(loanRequestId);
        loanRequestSummary.setLoanStatus(previousStatus);
        loanRequestSummary.setLastName("Perez");
        loanRequestSummary.setFirstName("Andres");
        loanRequestSummary.setEmail("andrespereza@example.com");

        when(loanStatusRepository.findStatusByCode(statusCode)).thenReturn(Mono.just(newStatus));
        when(repository.findLoanRequestsById(loanRequestId)).thenReturn(Mono.just(loanRequestSummary));
        when(repository.updateloanRequest(loanRequestId, newStatus.getId())).thenReturn(Mono.empty());
        when(sqsService.sendMessage(any(MessageBody.class), eq(queueName))).thenReturn(Mono.empty());
        when(sqsService.sendMessage(any(), eq(queueLoanApprovedReports))).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueName, queueLoanApprovedReports))
                .verifyComplete();

        verify(repository).updateloanRequest(loanRequestId, newStatus.getId());
        verify(sqsService).sendMessage(any(MessageBody.class), eq(queueName));
        verify(sqsService).sendMessage(any(MessageBody.class), eq(queueLoanApprovedReports));
    }

    @Test
    void shouldThrowExceptionWhenStatusCodeNotFound() {
        when(loanStatusRepository.findStatusByCode(statusCode)).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueName, queueLoanApprovedReports))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch(msg -> msg.contains("El estado")));
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch(msg -> msg.contains("es incorrecto")));
                })
                .verify();

        verifyNoInteractions(repository);
        verifyNoInteractions(sqsService);
    }

    @Test
    void shouldThrowExceptionWhenLoanRequestNotFound() {
        LoanStatus newStatus = new LoanStatus(2L, statusCode, "Aprobado");

        when(loanStatusRepository.findStatusByCode(statusCode)).thenReturn(Mono.just(newStatus));
        when(repository.findLoanRequestsById(loanRequestId)).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueName, queueLoanApprovedReports))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch(msg -> msg.contains("La solicitud de prestamo con ID")));
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch(msg -> msg.contains("no existe en la base de datos")));
                })
                .verify();

        verify(repository, never()).updateloanRequest(any(), any());
        verifyNoInteractions(sqsService);
    }

    @Test
    void shouldRollbackWhenSqsFails() {
        LoanStatus newStatus = new LoanStatus(2L, statusCode, "Aprobado");
        LoanStatus previousStatus = new LoanStatus(1L, "PEND", "Pendiente de revision");

        LoanRequestSummary loanRequestSummary = new LoanRequestSummary();
        loanRequestSummary.setId(loanRequestId);
        loanRequestSummary.setEmail("test@email.com");
        loanRequestSummary.setFirstName("John");
        loanRequestSummary.setLastName("Doe");
        loanRequestSummary.setLoanStatus(previousStatus);

        when(loanStatusRepository.findStatusByCode(statusCode)).thenReturn(Mono.just(newStatus));
        when(repository.findLoanRequestsById(loanRequestId)).thenReturn(Mono.just(loanRequestSummary));
        when(repository.updateloanRequest(loanRequestId, newStatus.getId())).thenReturn(Mono.empty());
        when(sqsService.sendMessage(any(), eq(queueName))).thenReturn(Mono.error(new RuntimeException("SQS error")));
        when(repository.updateloanRequest(loanRequestId, previousStatus.getId())).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueName, queueLoanApprovedReports))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);
                    ValidationException ve = (ValidationException) error;
                    Assertions.assertTrue(ve.getErrors().stream().anyMatch(msg -> msg.contains("Error enviando mensaje a SQS, reealizando rollback")));
                })
                .verify();

        verify(repository).updateloanRequest(loanRequestId, previousStatus.getId());
    }

    @Test
    void shouldSendCorrectMessageToSqs() {
        LoanStatus newStatus = new LoanStatus(2L, statusCode, "Aprobado");
        LoanStatus previousStatus = new LoanStatus(1L, "PEND", "Pendiente de revision");

        LoanRequestSummary loanRequestSummary = new LoanRequestSummary();
        loanRequestSummary.setId(loanRequestId);
        loanRequestSummary.setEmail("user@test.com");
        loanRequestSummary.setFirstName("Alice");
        loanRequestSummary.setLastName("Smith");
        loanRequestSummary.setLoanStatus(previousStatus);

        ArgumentCaptor<MessageBody> messageCaptor = ArgumentCaptor.forClass(MessageBody.class);

        when(loanStatusRepository.findStatusByCode(statusCode)).thenReturn(Mono.just(newStatus));
        when(repository.findLoanRequestsById(loanRequestId)).thenReturn(Mono.just(loanRequestSummary));
        when(repository.updateloanRequest(any(), any())).thenReturn(Mono.empty());
        when(sqsService.sendMessage(any(), eq(queueName))).thenReturn(Mono.empty());
        when(sqsService.sendMessage(any(), eq(queueLoanApprovedReports))).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueName, queueLoanApprovedReports))
                .verifyComplete();

        verify(sqsService).sendMessage(messageCaptor.capture(), eq(queueName));

        MessageBody sentMessage = messageCaptor.getValue();
        assertEquals("1", sentMessage.getIdLoanRequest());
        assertEquals("Aprobado", sentMessage.getStatus());
        assertEquals("user@test.com", sentMessage.getEmail());
        assertEquals("Alice Smith", sentMessage.getFullName());
    }

    @Test
    void shouldNotSendToSqsWhenStatusIsNeitherAprobNorRech() {
        LoanStatus newStatus = new LoanStatus(3L, LoanStatusEnum.CAN.name(), "En revisión");
        LoanStatus previousStatus = new LoanStatus(1L, LoanStatusEnum.PEND.name(), "Pendiente");

        LoanRequestSummary loanRequestSummary = new LoanRequestSummary();
        loanRequestSummary.setId(loanRequestId);
        loanRequestSummary.setLoanStatus(previousStatus);
        loanRequestSummary.setEmail("test@test.com");
        loanRequestSummary.setFirstName("Maria");
        loanRequestSummary.setLastName("Lopez");

        loanRequestUpdateStatus.setStatus(LoanStatusEnum.CAN.name());

        when(loanStatusRepository.findStatusByCode(LoanStatusEnum.CAN.name())).thenReturn(Mono.just(newStatus));
        when(repository.findLoanRequestsById(loanRequestId)).thenReturn(Mono.just(loanRequestSummary));
        when(repository.updateloanRequest(loanRequestId, newStatus.getId())).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueName, queueLoanApprovedReports))
                .verifyComplete();

        verify(repository).updateloanRequest(loanRequestId, newStatus.getId());
        verifyNoInteractions(sqsService);

    }
}