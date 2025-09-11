package co.com.crediya.model.loanrequest;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveLoan {
    private Long id;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
}
