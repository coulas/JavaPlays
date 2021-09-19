package io.permasoft.katas.javaplays.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionEndPoint {
    private static final Logger log = LoggerFactory.getLogger(ExceptionEndPoint.class);

    private final ExceptionUseCases useCases = new ExceptionUseCases();

    /**
     * this catch handling shall be configured with your framework or implemented in servlet filters or implemented with aspect pointcut to catch all your endpoints.
     */
    public String throwEarlyCatchLate(boolean doThrow, String message) {
        log.debug("start processing throwEarlyCatchLate with doThrow ? {}, with message : {}", doThrow, message);
        String result;
        try {
            result = "200 OK : " + useCases.failExternalLibraryException(doThrow, message).toString();
            log.info("throwEarlyCatchLate succeeded with doThrow ? {}, with message : {} returns {}", doThrow, message, result);
        } catch (Exception any) { // don't catch throwable to avoid catching fatal errors and still catch checked and unchecked exceptions
            log.error("throwEarlyCatchLate failed with doThrow ? {}, with message : {}", doThrow, message);
            log.error("throwEarlyCatchLate failed due to :", any);
            result = "Error 500 due to : " + any.getMessage();
        }
        return result;
    }
}
