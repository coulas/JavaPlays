package io.permasoft.katas.javaplays.exceptions;

public class BusinessDomainException extends RuntimeException {
    public BusinessDomainException(String message) {
        super(message);
    }

    public BusinessDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
