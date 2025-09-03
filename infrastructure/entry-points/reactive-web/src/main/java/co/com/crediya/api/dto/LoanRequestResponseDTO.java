package co.com.crediya.api.dto;

import co.com.crediya.model.loanrequest.LoanStatus;
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
    /*private Long id;
    private String documentId;
    private BigDecimal amount;
    private Integer termInMonths;
    private LoanTypeDTO loanType;
    private LoanStatusDTO status;
    private LocalDateTime createdAt;*/
    private Long id;
    private String documentNumber;
    private BigDecimal amount;
    private Integer termInMonths;
    private LoanType loanType;
    private LoanStatus loanStatus;
    private LocalDateTime createdAt;
}
