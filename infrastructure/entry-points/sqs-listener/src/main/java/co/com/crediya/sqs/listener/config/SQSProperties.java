package co.com.crediya.sqs.listener.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "entrypoint.sqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SQSProperties {
    private String region;
    private String endpoint;
    private String queueUrl;
    private int waitTimeSeconds;
    private int visibilityTimeoutSeconds;
    private int maxNumberOfMessages;
    private int numberOfThreads;
}
