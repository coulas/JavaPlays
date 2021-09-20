package io.permasoft.katas.javaplays.exceptions;

import io.permasoft.katas.javaplays.exceptions.externallibrary.YourUseOfMyLibraryIsInvalid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExceptionUseCases {
    private static final Logger log = LoggerFactory.getLogger(ExceptionUseCases.class);

    ExceptionProvider externalLib = new ExceptionProvider();

    public StringBuilder surviveExternalLibraryException(boolean withException, String message) {
        StringBuilder result = new StringBuilder();
        try {
            log.info("call external ressource");
            result.append(externalLib.conditionalThrow(withException, message)).append(", ");
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

    public StringBuilder failExternalLibraryException(boolean withException, String message) {
        StringBuilder result = new StringBuilder();
        try {
            log.info("call external ressource");
            result.append(externalLib.conditionalThrow(withException, message)).append(", ");
            log.info("after call");
            return result.append("return from try, ");
        } catch (Exception e) {
            log.error("process fails due to : ", e);
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
            this.externalLib.throwChecked("error in try");
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
