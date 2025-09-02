package co.com.crediya.r2dbc.adapter.impl;


import co.com.crediya.model.loanrequest.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggerServiceAdapter implements LoggerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void info(String message, Object... args) {
        logger.info(message,args);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message);
    }
}
