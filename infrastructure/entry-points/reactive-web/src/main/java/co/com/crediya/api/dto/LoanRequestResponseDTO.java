package co.com.crediya.api.dto;

import co.com.crediya.model.loanrequest.LoanType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRequestResponseDTO {
    private Long id;
    private String documentId;
    private BigDecimal amount;
    private Integer termInMonths;
    private LoanTypeDTO loanType;
    private LoanStatusDTO status;
    private LocalDateTime createdAt;
}
