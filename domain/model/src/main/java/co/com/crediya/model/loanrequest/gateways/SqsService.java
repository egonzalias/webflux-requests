package co.com.crediya.model.loanrequest.gateways;

import reactor.core.publisher.Mono;

public interface SqsService {
    public Mono<Void> sendMessage(Object messageBody, String queueName);
}
