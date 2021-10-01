package io.permasoft.katas.javaplays.exceptions.persistence.jpa;

import io.permasoft.katas.javaplays.exceptions.externallibrary.YourUseOfMyLibraryIsInvalid;
import org.springframework.stereotype.Component;

@Component
public interface ExceptionDao {

    default Integer apiWhenLibWantsYouToHandleItsExceptions(int id) throws YourUseOfMyLibraryIsInvalid {
        if (id < 0) { // checked exceptions extends Exception which extends Throwable
            throw new YourUseOfMyLibraryIsInvalid("Negative input["+id+"] is invalid");
        }
        return Integer.valueOf(id);
    }
    default  Integer apiWhenLibShallWork(int id) {
        if (id < 0) { // unchecked Excpetion extends RuntimeException which extends Exception which extends Throwable
            throw new IllegalArgumentException("Negative input["+id+"] is illegal");
        }
        return Integer.valueOf(id);
    }

    default  Integer apiWhenApplicationCantKeepRunning(int id) {
        // Errors extends Error which extends Throwable and means JVM cannot keep running, it must stop, so you shall never catch them !
        throw new OutOfMemoryError("You called me too much, you are out of memory");
    }
}
