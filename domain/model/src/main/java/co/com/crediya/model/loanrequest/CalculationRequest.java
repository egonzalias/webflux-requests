package co.com.crediya.model.loanrequest;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculationRequest {
    private String documentNumber;
    private String loanRequestId;
    private BigDecimal amount;
    private BigDecimal baseSalary;
    private int termMonths;
    private BigDecimal interestRate; // annual
    private List<ActiveLoan> activeLoans;
}
