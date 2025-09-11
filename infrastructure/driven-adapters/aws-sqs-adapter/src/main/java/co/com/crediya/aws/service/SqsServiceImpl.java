package co.com.crediya.aws.service;

import co.com.crediya.model.loanrequest.gateways.LoggerService;
import co.com.crediya.model.loanrequest.gateways.SqsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.CompletableFuture;

@Service
public class SqsServiceImpl implements SqsService {

    private final SqsAsyncClient sqsAsyncClient;
    private final LoggerService loggerService;

    @Value("${aws.url}")
    private String awsUrl;
    @Value("${aws.account-id}")
    private String accountId;

    public SqsServiceImpl(@Qualifier("sqsAsyncClient") SqsAsyncClient sqsAsyncClient, LoggerService loggerService) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.loggerService = loggerService;
    }



    public Mono<Void> sendMessage(Object messageBody, String queueName) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(messageBody);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }

        String url = awsUrl + "/" + accountId + "/" + queueName;
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(jsonMessage)
                .build();

        CompletableFuture<?> future = sqsAsyncClient.sendMessage(request);

        return Mono.fromFuture(future)
                .doOnSuccess(response -> loggerService.info("Mensaje enviado a SQS: {}" , response))
                .doOnError(error -> loggerService.error("Error al enviar mensaje a SQS: {}" , error))
                .then();
    }
}
