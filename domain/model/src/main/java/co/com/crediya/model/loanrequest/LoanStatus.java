package co.com.crediya.model.loanrequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanStatus {
    private Long id;
    private String code;
    private String description;
}