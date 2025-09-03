package co.com.crediya.model.loanrequest;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanType {
    private Long id;
    private String name;
    private String description;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private BigDecimal interestRate;
    private boolean automaticValidation;
}
