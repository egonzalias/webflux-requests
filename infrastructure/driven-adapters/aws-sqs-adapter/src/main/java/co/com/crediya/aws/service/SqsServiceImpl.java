package co.com.crediya.aws.service;

import co.com.crediya.model.loanrequest.gateways.LoggerService;
import co.com.crediya.model.loanrequest.gateways.SqsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SqsServiceImpl implements SqsService {

    private final SqsAsyncClient sqsAsyncClient;
    private final LoggerService loggerService;

    @Value("${aws.url}")
    private String awsUrl;
    @Value("${aws.account-id}")
    private String accountId;

    public Mono<Void> sendMessage(String messageBody, String queueName) {
        String url = awsUrl + "/" + accountId + "/" + queueName;
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(messageBody)
                .build();

        CompletableFuture<?> future = sqsAsyncClient.sendMessage(request);

        return Mono.fromFuture(future)
                .doOnSuccess(response -> loggerService.info("Mensaje enviado a SQS: {}" , response))
                .doOnError(error -> loggerService.error("Error al enviar mensaje a SQS: {}" , error))
                .then();
    }
}
