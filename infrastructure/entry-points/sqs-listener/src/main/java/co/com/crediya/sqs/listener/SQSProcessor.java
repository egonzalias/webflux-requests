package co.com.crediya.sqs.listener;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequestUpdateStatus;
import co.com.crediya.model.loanrequest.PaymentSchedule;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import co.com.crediya.sqs.listener.dto.LoanEvaluationResultEvent;
import co.com.crediya.usecase.user.UpdateLoanRequestUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final LoggerService logger;
    private final UpdateLoanRequestUseCase updateLoanRequestUseCase;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${aws.queue-loan-status-update}")
    private String queueLoanStatusUpdate;
    @Value("${aws.queue-loan-approved-reports}")
    private String queueLoanApprovedReports;

    @Override
    public Mono<Void> apply(Message message) {

        logger.info("Listen queue... ",message.body());
        try{
            LoanEvaluationResultEvent dto = mapper.readValue(message.body(), LoanEvaluationResultEvent.class);

            LoanRequestUpdateStatus loanRequestUpdateStatus = new LoanRequestUpdateStatus();
            loanRequestUpdateStatus.setId(Long.parseLong(dto.getIdLoanRequest()));
            loanRequestUpdateStatus.setStatus(dto.getDecision());

            List<PaymentSchedule> paymentPlan = dto.getPaymentPlan().stream()
                            .map(item ->{
                               PaymentSchedule schedule = new PaymentSchedule();
                                schedule.setMonth(item.getMonth());
                                schedule.setCapital(item.getCapital());
                                schedule.setInterest(item.getInterest());
                                schedule.setRemainingBalance(item.getRemainingBalance());
                               return schedule;
                            })
                    .collect(Collectors.toList());

            loanRequestUpdateStatus.setPaymentPlan(paymentPlan);
            return updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueLoanStatusUpdate, queueLoanApprovedReports);
        }catch (Exception e){
            logger.error("Error processing message queue... ", e);
            return Mono.error(new ValidationException(List.of(e.getMessage())));
        }
    }
}
