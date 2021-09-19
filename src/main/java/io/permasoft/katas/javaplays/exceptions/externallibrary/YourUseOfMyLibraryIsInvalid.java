package io.permasoft.katas.javaplays.exceptions.externallibrary;

public class YourUseOfMyLibraryIsInvalid extends Exception {
    public YourUseOfMyLibraryIsInvalid(String message) {
        super(message);
    }
    public YourUseOfMyLibraryIsInvalid(String message, Throwable cause) {
        super(message, cause);
    }
}
