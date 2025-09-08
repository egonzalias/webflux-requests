package co.com.crediya.model.loanrequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageBody {
    private String idLoanRequest;
    private String status;
    private String email;
    private String fullName;
}
