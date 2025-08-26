package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("loan_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanTypeEntity {
    @Id
    private Long id;
    private String code;
    private String description;
}
