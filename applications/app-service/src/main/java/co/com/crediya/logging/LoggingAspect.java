package co.com.crediya.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* co.com.crediya..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Entrando a: {} con argumentos = {}", joinPoint.getSignature(), joinPoint.getArgs());
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();

            if (logger.isDebugEnabled()) {
                long elapsedTime = System.currentTimeMillis() - start;
                logger.debug("Saliendo de: {} con resultado = {} en {} ms", joinPoint.getSignature(), result, elapsedTime);
            }

            return result;
        } catch (Throwable e) {
            logger.error("Excepción en: {} mensaje = {}", joinPoint.getSignature(), e.getMessage());
            throw e;
        }
    }
}
