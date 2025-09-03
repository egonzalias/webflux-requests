package co.com.crediya.usecase.user;

import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoanTypeRepository;
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
import java.time.LocalTime;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class GetLoanRequestUseCaseTest {

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

    @BeforeEach
    void setup(){
        loanStatus = new LoanStatus(1L, "PEND", "Pendiente de revision");
        loanType = new LoanType(1L, "LIBRE", "Prestamos personal para libre inversion");
        loanRequest = new LoanRequest(1L,
                "112233",
                BigDecimal.valueOf(1400000),
                6,
                loanType,
                loanStatus,
                LocalDate.of(1995, 5, 20).atTime(LocalTime.now()));

        getLoanRequestUseCase = new GetLoanRequestUseCase(loanStatusRepository, repository, loggerService);
        page = 0;
        size = 10;
    }

}
