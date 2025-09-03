package co.com.crediya.r2dbc.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequestExtendedDTO {
    private Long id;
    private String document_number;
    private BigDecimal amount;
    private Integer term_months;

    private Long loan_type_id;
    private String loan_type_name;
    private String loan_type_description;

    private Long status_id;
    private String status_code;
    private String status_description;

    private LocalDateTime created_at;
}
