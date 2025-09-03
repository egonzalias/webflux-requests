package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;


@Table("loan_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanTypeEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    @Column("minimum_amount")
    private BigDecimal minimumAmount;
    @Column("maximum_amount")
    private BigDecimal maximumAmount;
    @Column("interest_rate")
    private BigDecimal interestRate;
    @Column("automatic_validation")
    private boolean automaticValidation;
}
