package co.com.crediya.model.loanrequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanType {
    private Long id;
    private String code;
    private String description;
}
