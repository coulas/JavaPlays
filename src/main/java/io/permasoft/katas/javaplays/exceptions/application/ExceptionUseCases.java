package io.permasoft.katas.javaplays.exceptions.application;

import io.permasoft.katas.javaplays.exceptions.domain.BusinessDomainException;
import io.permasoft.katas.javaplays.exceptions.persistence.ExceptionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ExceptionUseCases {
    private static final Logger log = LoggerFactory.getLogger(ExceptionUseCases.class);

    private ExceptionStore externalLib;

    @Autowired
    public ExceptionUseCases(ExceptionStore externalLib) {
        this.externalLib = externalLib;
    }

    public StringBuilder warnUseCaseWorksDespiteException(int positiveId) {
        StringBuilder result = new StringBuilder();
        try {
            log.info("call external ressource");
            result.append(externalLib.conditionalThrow(positiveId)).append(", ");
            log.info("after call");
            return result.append("return from try, ");
        } catch (Exception e) {
            log.warn("provide a default result, due to : ", e);
            result.append(e.getMessage()).append(", ");
            // resume business process despite external exception
            return result.append("return from catch, ");
        } finally { // this block shall never contain return or throws statements : https://www.baeldung.com/java-finally-keyword#common-pitfalls
            log.info("ensure resources are closed, ");
            result.append("modify result in finally.");
        }
        // unreachable statement return result.append("return from end.").toString();
    }

    public StringBuilder errorUseCaseFailsDueToException(int positiveId) {
        StringBuilder result = new StringBuilder();
        try {
            log.info("call external ressource");
            result.append(externalLib.conditionalThrow(positiveId)).append(", ");
            log.info("after call");
            return result.append("return from try, ");
        } catch (Exception e) {
            log.error("process fails due to : {}", e.getMessage());
            result.append(e.getMessage()).append(", ");
            // abort business process due to external exception
            throw new BusinessDomainException("fail calling external library due to : "+e.getMessage(), e);
        } finally { // this block shall never contain return or throws statements : https://www.baeldung.com/java-finally-keyword#common-pitfalls
            log.info("ensure resources are closed, ");
            result.append("modify result in finally.");
        }
        // unreachable statement return result.append("return from end.").toString();
    }

    public String failOnMissingRessources() {

        try (AutoCloseable file = new FailingResourceClosing()) {
            this.externalLib.throwChecked();
        } catch (Exception e) {
            log.error("process failed due to ", e);
            throw new BusinessDomainException("wrap checked in unchecked due to "+e.getMessage(), e);
        } finally {
            log.debug("You don't need to close autocloseable resources.");
        }
        return "end of method";
    }
    class FailingResourceClosing implements AutoCloseable {
        @Override
        public void close() throws Exception {
            throw new IOException("error at closing time");
        }

    }
}
