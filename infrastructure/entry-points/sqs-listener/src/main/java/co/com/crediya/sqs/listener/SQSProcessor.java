package co.com.crediya.sqs.listener;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequestUpdateStatus;
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

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final LoggerService logger;
    private final UpdateLoanRequestUseCase updateLoanRequestUseCase;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${aws.queue-loan-status-update}")
    private String queueLoanStatusUpdate;

    @Override
    public Mono<Void> apply(Message message) {

        logger.info("Listen queue... ",message.body());
        try{
            LoanEvaluationResultEvent dto = mapper.readValue(message.body(), LoanEvaluationResultEvent.class);

            LoanRequestUpdateStatus loanRequestUpdateStatus = new LoanRequestUpdateStatus();
            loanRequestUpdateStatus.setId(Long.parseLong(dto.getIdLoanRequest()));
            loanRequestUpdateStatus.setStatus(dto.getDecision());
            return updateLoanRequestUseCase.updateLoanStatus(loanRequestUpdateStatus, queueLoanStatusUpdate);
        }catch (Exception e){
            logger.error("Error processing message queue... ", e);
            return Mono.error(new ValidationException(List.of(e.getMessage())));
        }
    }
}
