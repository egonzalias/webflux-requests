package co.com.crediya.api.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private ZonedDateTime timestamp;
    private String path;
    private int status;
    private String error;
    private List<String> errors;
    private String requestId;
}
