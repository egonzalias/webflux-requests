package co.com.crediya.model.loanrequest;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaymentSchedule {

    private int month;
    private BigDecimal capital;
    private BigDecimal interest;
    private BigDecimal remainingBalance;

    @Override
    public String toString() {
        return "PaymentScheduleDTO{" +
                " Mes =" + month +
                ", Capital =" + capital +
                ", Intereses =" + interest +
                ", Saldo restante =" + remainingBalance +
                '}';
    }
}
