package io.permasoft.katas.javaplays.exceptions;

import io.permasoft.katas.javaplays.exceptions.api.ExceptionEndPoint;
import io.permasoft.katas.javaplays.exceptions.application.ExceptionUseCases;
import io.permasoft.katas.javaplays.exceptions.domain.BusinessDomainException;
import io.permasoft.katas.javaplays.exceptions.externallibrary.YourUseOfMyLibraryIsInvalid;
import io.permasoft.katas.javaplays.exceptions.persistence.ExceptionStore;
import io.permasoft.katas.javaplays.exceptions.persistence.jpa.ExceptionDao;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith({SoftAssertionsExtension.class, OutputCaptureExtension.class})
// Too Early : @TestClassOrder(ClassOrderer.DisplayName.class)
class ExceptionsTest {
    private final ExceptionStore store = new ExceptionStore(new ExceptionDao(){});
    private final ExceptionUseCases useCases = new ExceptionUseCases(store);
    private final ExceptionEndPoint endPoint = new ExceptionEndPoint(useCases);

    @InjectSoftAssertions
    SoftAssertions should;

    @Nested
    @DisplayName("1. show all types of exceptions")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class TypesOfExceptions {
        @Test
        @DisplayName("1. Not really a type but most method does not throw exception at all..")
        void noExceptions() {
            AtomicReference<Integer> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(store.dontThrow()))
                    .doesNotThrowAnyException();
            should.assertThat(result.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("2. Checked exception are child of Exception that describe fatal library condition that business code must catch so they can throw their own unchecked business related exception and avoid ugly method signatures.")
        void checkedExceptions() {
            assertThatCode(store::throwChecked)
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("-1")
                    .hasMessageContaining("is invalid");
        }

        @Test
        @DisplayName("3. Unchecked exception are child of RuntimeException that describe fatal use case condition that business code shall not catch.")
        void uncheckedExceptions() {
            assertThatCode(store::throwUnchecked)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("-1")
                    .hasMessageContaining("is illegal");
        }

        @Test
        @DisplayName("4. Errors are child of throwable that describe fatal running conditions that applications shall not catch.")
        void errors() {
            assertThatCode(store::throwError)
                    .isInstanceOf(Error.class)
                    .hasMessageContaining("out of memory");
        }
    }

    @Nested
    @DisplayName("2. handle exception as soon as you can and throw early so you can fail fast and analyse between failure and throw is short")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class HandlingExceptionInLowerLayers {
        @Test
        @DisplayName("1.1. execute without external library exception and with recovery handling")
        void recovery_handling_without_exception(CapturedOutput out) {
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.surviveExternalLibraryException(1).toString()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "INFO.*after call",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get()).isEqualTo(
                    "1, return from try, modify result in finally."
            );
        }

        @Test
        @DisplayName("1.2. execute with external library exception and recovery from it")
        void recovery_handling_with_exception(CapturedOutput out) {
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.surviveExternalLibraryException(-1).toString()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "WARN.*provide a default result",
                            "at .*surviveExternalLibraryException",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get()).isEqualTo(
                    "Negative input[-1] is invalid, return from catch, modify result in finally."
            );
        }

        @Test
        @DisplayName("2.1. execute without external library exception and with failure handling")
        void failure_handling_without_exception(CapturedOutput out) {
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.failExternalLibraryException(1).toString()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "INFO.*after call",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get())
                    .isEqualTo("1, return from try, modify result in finally.");
        }

        @Test
        @DisplayName("2.2. execute with external library exception and fail due to it")
        void failure_handling_with_exception(CapturedOutput out) {
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.failExternalLibraryException(-1).toString()))
                    .isInstanceOf(BusinessDomainException.class)
                    .hasMessageContaining("fail calling external library");
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "ERROR.*process fails",
                            "YourUseOfMyLibraryIsInvalid: ",
                            "at .*failExternalLibraryException",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get()).isNull();
        }

        @Test
        @DisplayName("2.3. execute with external library exception, fail due to it and handle failure")
        void handling_failure_handling_with_exception(CapturedOutput out) {
            AtomicReference<String> result = new AtomicReference<>();
            //useCases.failExternalLibraryException(true, message).toString()
            should.assertThatCode(() -> result.set(endPoint.throwEarlyCatchLate()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "DEBUG.*start processing throwEarlyCatchLate",
                            "INFO.*call external ressource",
                            "ERROR.*process fails",
                            "YourUseOfMyLibraryIsInvalid: ",
                            "INFO.*ensure resources are closed",
                            "ERROR.*throwEarlyCatchLate failed",
                            "ERROR.*throwEarlyCatchLate failed due to :",
                            "BusinessDomainException: fail calling external library",
                            "Caused by:.*YourUseOfMyLibraryIsInvalid: "
                    );
            should.assertThat(result.get())
                    .isEqualTo("Error 500 due to : fail calling external library due to : Negative input[-1] is invalid");
        }
    }

    @Nested
    @DisplayName("3. show one last type of exception, a nested one : the suppressed exception")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class OneLastTypeOfNestedException {
        @Test
        @DisplayName("1. autoclosable closes nicely their ressource that throw exception and hide closing exception has suppressed in the exception's data structure. So you may have a resource leak or not...")
        void try_with_ressources() {
            should.assertThatCode(useCases::failOnMissingRessources)
                    .isInstanceOf(BusinessDomainException.class)
                    .hasMessageContaining("wrap checked in unchecked")
                    .hasMessageContaining("-1")
                    .hasMessageContaining("is invalid")
                    .hasNoSuppressedExceptions()
                    .getCause()
                    .isInstanceOf(YourUseOfMyLibraryIsInvalid.class)
                    .hasMessageContaining("-1")
                    .hasMessageContaining("is invalid")
                    .hasSuppressedException(new IOException("error at closing time"));
        }
    }

    private static int findRegexInActual(String actual, String expected) {
        Pattern p = Pattern.compile(expected);
        Matcher m = p.matcher(actual);
        return m.find() ? 0 : 1;
    }

}
