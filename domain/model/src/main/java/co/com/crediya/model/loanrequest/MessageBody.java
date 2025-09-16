package co.com.crediya.model.loanrequest;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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
    private List<PaymentSchedule> paymentPlan;
    private BigDecimal loanAmount;
}
