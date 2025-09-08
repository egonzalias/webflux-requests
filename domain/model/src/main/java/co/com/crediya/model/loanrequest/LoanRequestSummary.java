package co.com.crediya.model.loanrequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRequestSummary {
    private Long id;
    private String documentNumber;
    private BigDecimal amount;
    private Integer termInMonths;
    private LoanType loanTypeCode;
    private LoanStatus loanStatus;
    private LocalDateTime createdAt;
    private BigDecimal approved_monthly_debt;
    private String firstName;
    private String lastName;
    private String email;
}
