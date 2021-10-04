package io.permasoft.katas.javaplays.exceptions.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class LoggingInOuts {
    @Around("@annotation(io.permasoft.katas.javaplays.exceptions.configuration.Logging)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String joinPointLog = joinPoint.toString();
        String joinPointArgs = Arrays.deepToString(joinPoint.getArgs());
        logger.debug("receiving request for {} with arguments {}",
                joinPointLog,
                joinPointArgs);
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            logger.info("processed request in {} ms for {} with arguments {} returned {}",
                    endTime - startTime,
                    joinPointLog,
                    joinPointArgs,
                    result);
            return result;
        } catch (Throwable t) {
            long endTime = System.currentTimeMillis();
            logger.error("failed request in {} ms for {} with arguments {} throwing ",
                    endTime - startTime,
                    joinPointLog,
                    joinPointArgs,
                    t);
            // Rest Handler can return a 500 status with a problem media type with t.getMessage as their content
            throw t;
        }
    }
}
