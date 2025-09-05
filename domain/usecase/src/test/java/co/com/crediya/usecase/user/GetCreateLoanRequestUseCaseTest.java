package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GetCreateLoanRequestUseCaseTest {

    @Mock
    private LoanRequestRepository repository;
    @Mock
    private LoanStatusRepository loanStatusRepository;
    @Mock
    LoggerService loggerService;

    private LoanType loanType;
    private LoanStatus loanStatus;
    private LoanRequest loanRequest;
    private GetLoanRequestUseCase getLoanRequestUseCase;
    private int page;
    private int size;
    private List<LoanStatus> foundStatuses;
    private LoanRequestSummary summary1;
    private LoanRequestSummary summary2;

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

        foundStatuses = List.of(
                new LoanStatus(1L, "PEND", "Pendiente de revision"),
                new LoanStatus(2L, "APROB", "Solicitud aprobada"),
                new LoanStatus(3L, "RECH", "Solicitud rechazada")
        );

        summary1 = new LoanRequestSummary();
        summary1.setId(1L);
        summary1.setDocumentNumber("1234567890");
        summary1.setAmount(new BigDecimal("10000.00"));
        summary1.setTermInMonths(12);
        summary1.setLoanTypeCode(loanType);
        summary1.setLoanStatus(loanStatus);
        summary1.setCreatedAt(LocalDateTime.of(2025, 9, 3, 10, 30));
        summary1.setApproved_monthly_debt(new BigDecimal("850.00"));

        summary2 = new LoanRequestSummary();
        summary2.setId(2L);
        summary2.setDocumentNumber("0987654321");
        summary2.setAmount(new BigDecimal("25000.00"));
        summary2.setTermInMonths(24);
        summary2.setLoanTypeCode(loanType);
        summary2.setLoanStatus(loanStatus);
        summary2.setCreatedAt(LocalDateTime.of(2025, 9, 1, 15, 45));
        summary2.setApproved_monthly_debt(new BigDecimal("1100.00"));

        getLoanRequestUseCase = new GetLoanRequestUseCase(loanStatusRepository, repository, loggerService);
        page = 0;
        size = 10;
    }

    @Test
    void shouldReturnLoanRequestsWhenStatusesAreValid() {
        List<String> statusCodes = List.of("APROBADO", "PENDIENTE");
        List<LoanRequestSummary> expectedLoans = List.of(summary1, summary2);

        when(loanStatusRepository.findStatusByCodes(statusCodes))
                .thenReturn(Flux.fromIterable(foundStatuses));

        when(repository.findLoanRequestsByStatusIn(List.of(1L, 2L, 3L), 10, 0))
                .thenReturn(Flux.fromIterable(expectedLoans));

        when(repository.countLoanRequestByStatusIn(List.of(1L, 2L, 3L)))
                .thenReturn(Mono.just(2L));

        StepVerifier.create(getLoanRequestUseCase.getLoanRequestsByStatus(statusCodes, 0, 10))
                .assertNext(page -> {
                    assertEquals(2, page.getContent().size());
                    assertEquals(summary1, page.getContent().get(0));
                    assertEquals(summary2, page.getContent().get(1));
                    assertEquals(0, page.getPage());
                    assertEquals(10, page.getSize());
                    assertEquals(2, page.getTotalElements());
                })
                .verifyComplete();

        verify(loanStatusRepository).findStatusByCodes(statusCodes);
        verify(repository).findLoanRequestsByStatusIn(List.of(1L, 2L, 3L), 10, 0);
    }

    @Test
    void shouldThrowValidationExceptionWhenNoValidStatuses() {
        List<String> invalidCodes = List.of("INVALIDO", "DESCONOCIDO");

        when(loanStatusRepository.findStatusByCodes(invalidCodes))
                .thenReturn(Flux.empty());

        StepVerifier.create(getLoanRequestUseCase.getLoanRequestsByStatus(invalidCodes, 0, 10))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ValidationException);
                    ValidationException ex = (ValidationException) error;
                    assertEquals(List.of("Ninguno de los estados proporcionados es válido."), ex.getErrors());
                })
                .verify();

        verify(loanStatusRepository).findStatusByCodes(invalidCodes);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldReturnEmptyWhenNoLoanRequestsFound() {
        List<String> statusCodes = List.of("APROBADO");

        when(loanStatusRepository.findStatusByCodes(statusCodes))
                .thenReturn(Flux.fromIterable(foundStatuses));

        when(repository.findLoanRequestsByStatusIn(List.of(1L, 2L, 3L), 10, 10)) // página 1 (offset = 10)
                .thenReturn(Flux.empty());

        when(repository.countLoanRequestByStatusIn(List.of(1L, 2L, 3L)))
                .thenReturn(Mono.just(2L));

        StepVerifier.create(getLoanRequestUseCase.getLoanRequestsByStatus(statusCodes, 1, 10))
                .assertNext(page -> {
                    assertThat(page.getContent()).isEmpty(); // o assertEquals(Collections.emptyList(), page.getContent());
                    assertThat(page.getPage()).isEqualTo(1);
                    assertThat(page.getSize()).isEqualTo(10);
                    assertThat(page.getTotalElements()).isEqualTo(2L); // según tu mock
                })
                .verifyComplete();

        verify(loanStatusRepository).findStatusByCodes(statusCodes);
        verify(repository).findLoanRequestsByStatusIn(List.of(1L, 2L, 3L), 10, 10);
    }


}
