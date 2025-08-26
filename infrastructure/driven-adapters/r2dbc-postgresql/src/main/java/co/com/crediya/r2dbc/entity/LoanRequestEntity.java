package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("loan_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestEntity {
    @Id
    private Long id;
    @Column("document_number")
    private String documentNumber;
    private BigDecimal amount;
    @Column("term_months")
    private Integer termInMonths;
    @Column("loan_type_id")
    private Long loanType;
    @Column("status_id")
    private Long status;
    @Column("created_at")
    private LocalDateTime createdAt;
}
