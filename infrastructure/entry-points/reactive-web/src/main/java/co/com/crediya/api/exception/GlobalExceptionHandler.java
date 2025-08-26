package co.com.crediya.api.exception;


import co.com.crediya.model.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(ValidationException.class)
    public Mono<Void> handleValidationException(ServerWebExchange exchange, ValidationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String requestId = exchange.getRequest().getId();
        String path = exchange.getRequest().getPath().value();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .path(path)
                .status(status.value())
                .error(status.getReasonPhrase())
                .errors(ex.getErrors())
                .requestId(requestId)
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    @ExceptionHandler(Exception.class)
    public Mono<Void> handleGenericException(ServerWebExchange exchange, Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String requestId = exchange.getRequest().getId();
        String path = exchange.getRequest().getPath().value();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .path(path)
                .status(status.value())
                .error(status.getReasonPhrase())
                .errors(List.of(ex.getMessage() != null ? ex.getMessage() : "Internal Server Error"))
                .requestId(requestId)
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }
}
