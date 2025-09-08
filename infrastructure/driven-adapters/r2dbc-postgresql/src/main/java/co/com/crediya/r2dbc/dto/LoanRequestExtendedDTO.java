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
    private BigDecimal loan_type_minimum_amount;
    private BigDecimal loan_type_maximum_amount;
    private BigDecimal loan_type_interest_rate;
    private boolean loan_type_automatic_validation;

    private Long status_id;
    private String status_code;
    private String status_description;

    private BigDecimal approved_monthly_debt;

    private LocalDateTime created_at;

    private String first_name;
    private String last_name;
    private String email;
}
