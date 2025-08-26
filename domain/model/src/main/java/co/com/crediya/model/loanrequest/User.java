package co.com.crediya.model.loanrequest;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String document_number;
    private String first_name;
    private String last_name;
    private LocalDate birth_date;
    private String address;
    private String phone;
    private String email;
    private BigDecimal base_salary;
}
