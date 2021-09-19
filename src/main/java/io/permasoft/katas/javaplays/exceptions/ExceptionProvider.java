package io.permasoft.katas.javaplays.exceptions;

import io.permasoft.katas.javaplays.exceptions.externallibrary.YourUseOfMyLibraryIsInvalid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionProvider {
    private static final Logger log = LoggerFactory.getLogger(ExceptionProvider.class);

    public String dontThrow(String message) {
        log.debug(message);
        return message;
    }

    public void throwError(String message) {
        log.error(message);
        throw new OutOfMemoryError(message);
    }

    public String throwUnchecked(String message) {
        log.error(message);
        throw new IllegalArgumentException(message);
    }

    public void throwChecked(String message) throws YourUseOfMyLibraryIsInvalid {
        log.error(message);
        throw new YourUseOfMyLibraryIsInvalid(message);
    }

    public String conditionalThrow (boolean doThrow, String message) throws YourUseOfMyLibraryIsInvalid {
        if (doThrow) {
            log.error(message);
            throw new YourUseOfMyLibraryIsInvalid(message);
        }
        log.info(message);
        return message;
    }
}
