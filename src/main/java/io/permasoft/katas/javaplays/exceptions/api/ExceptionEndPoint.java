package io.permasoft.katas.javaplays.exceptions.api;

import io.permasoft.katas.javaplays.exceptions.application.ExceptionUseCases;
import io.permasoft.katas.javaplays.exceptions.configuration.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ExceptionEndPoint {
    private static final Logger log = LoggerFactory.getLogger(ExceptionEndPoint.class);

    private ExceptionUseCases useCases;

    @Autowired
    public ExceptionEndPoint(ExceptionUseCases useCases) {
        this.useCases = useCases;
    }

    /**
     * this catch handling shall be configured with your framework or implemented in servlet filters or implemented with aspect pointcut to catch all your endpoints.
     */
    public String throwEarlyCatchLate() {
        log.debug("start processing throwEarlyCatchLate");
        String result;
        try {
            result = "200 OK : " + useCases.failExternalLibraryException(-1).toString();
            log.info("throwEarlyCatchLate succeeded returns {}", result);
        } catch (Exception any) { // don't catch throwable to avoid catching fatal errors and still catch checked and unchecked exceptions
            log.error("throwEarlyCatchLate failed");
            log.error("throwEarlyCatchLate failed due to :", any);
            result = "Error 500 due to : " + any.getMessage();
        }
        return result;
    }

    @Logging
    public String endPointFailsHandledByFramework() {
        return useCases.failExternalLibraryException(-1).toString();
    }
    @Logging
    public String endPointSucceedsHandledByFramework() {
        return useCases.failExternalLibraryException(1).toString();
    }
}
