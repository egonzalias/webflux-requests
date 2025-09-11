package co.com.crediya.r2dbc.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveLoanDTO {
    private Long id;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
}
