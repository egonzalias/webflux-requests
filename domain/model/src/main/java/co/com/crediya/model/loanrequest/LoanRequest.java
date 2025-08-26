package co.com.crediya.model.loanrequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRequest {
    private String documentNumber;
    private BigDecimal amount;
    private Integer termInMonths;
    private LoanType loanTypeCode;
    private LoanStatus loanStatus;
    private LocalDateTime createdAt;
}
