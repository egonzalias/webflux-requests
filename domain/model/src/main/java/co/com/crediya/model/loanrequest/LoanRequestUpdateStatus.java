package co.com.crediya.model.loanrequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRequestUpdateStatus {
    private Long id;
    private String status;
    private List<PaymentSchedule> paymentPlan;
}
