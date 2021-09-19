package io.permasoft.katas.javaplays.exceptions;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith({SoftAssertionsExtension.class, OutputCaptureExtension.class})
@TestClassOrder(ClassOrderer.DisplayName.class)
public class ExceptionsTest {
    private final ExceptionUseCases useCases = new ExceptionUseCases();
    // SUT  : System Under Test
    private ExceptionProvider sut = new ExceptionProvider();

    @InjectSoftAssertions
    SoftAssertions should;

    @Nested
    @DisplayName("1. show all types of exceptions")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class TypesOfExceptions {
        @Test
        @DisplayName("1. Not really a type but most method does not throw exception at all..")
        void noExceptions() {
            String message = "user's basket not found";
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(sut.dontThrow(message)))
                    .doesNotThrowAnyException();
            should.assertThat(result.get()).contains(message);
        }

        @Test
        @DisplayName("2. Checked exception are child of Exception that describe fatal library condition that business code must catch so they can throw their own unchecked business related exception and avoid ugly method signatures.")
        void checkedExceptions() {
            String message = "no record found in basket with given id";
            assertThatCode(() -> sut.throwChecked(message))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining(message);
        }

        @Test
        @DisplayName("3. Unchecked exception are child of RuntimeException that describe fatal use case condition that business code shall not catch.")
        void uncheckedExceptions() {
            String message = "user's basket not found";
            assertThatCode(() -> sut.throwUnchecked(message))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(message);
        }

        @Test
        @DisplayName("4. Errors are child of throwable that describe fatal running conditions that applications shall not catch.")
        void errors() {
            String message = "Out of Memory";
            assertThatCode(() -> sut.throwError(message))
                    .isInstanceOf(Error.class)
                    .hasMessageContaining(message);
        }
    }

    @Nested
    @DisplayName("2. handle exception as soon as you can and throw early so you can fail fast and analyse between failure and throw is short")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class HandlingExceptionInLowerLayers {
        @Test
        @DisplayName("1.1. execute without external library exception and with recovery handling")
        void recovery_handling_without_exception(CapturedOutput out) {
            String message = "success message";
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.surviveExternalLibraryException(false, message).toString()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsExactly(
                            "INFO.*call external ressource",
                            "INFO.*success message",
                            "INFO.*after call",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get()).isEqualTo(
                    message + ", return from try, modify result in finally."
            );
        }

        @Test
        @DisplayName("1.2. execute with external library exception and recovery from it")
        void recovery_handling_with_exception(CapturedOutput out) {
            String message = "failure message";
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.surviveExternalLibraryException(true, message).toString()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "ERROR.*failure message",
                            "WARN.*provide a default result",
                            "YourUseOfMyLibraryIsInvalid: failure message",
                            "at .*surviveExternalLibraryException",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get()).isEqualTo(
                    message + ", return from catch, modify result in finally."
            );
        }

        @Test
        @DisplayName("2.1. execute without external library exception and with failure handling")
        void failure_handling_without_exception(CapturedOutput out) {
            String message = "success message";
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.failExternalLibraryException(false, message).toString()))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "INFO.*success message",
                            "INFO.*after call",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get())
                    .isEqualTo("success message, return from try, modify result in finally.");
        }

        @Test
        @DisplayName("2.2. execute with external library exception and fail due to it")
        void failure_handling_with_exception(CapturedOutput out) {
            String message = "failure message";
            AtomicReference<String> result = new AtomicReference<>();
            should.assertThatCode(() -> result.set(useCases.failExternalLibraryException(true, message).toString()))
                    .isInstanceOf(BusinessDomainException.class)
                    .hasMessageContaining("fail calling external library due to : " + message);
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "INFO.*call external ressource",
                            "ERROR.*failure message",
                            "ERROR.*process fails",
                            "YourUseOfMyLibraryIsInvalid: failure message",
                            "at .*failExternalLibraryException",
                            "INFO.*ensure resources are closed");
            should.assertThat(result.get()).isNull();
        }

        @Test
        @DisplayName("2.3. execute with external library exception, fail due to it and handle failure")
        void handling_failure_handling_with_exception(CapturedOutput out) {
            String message = "process me";
            boolean doThrow = true;
            AtomicReference<String> result = new AtomicReference<>();
            //useCases.failExternalLibraryException(true, message).toString()
            should.assertThatCode(() -> result.set(new ExceptionEndPoint().throwEarlyCatchLate(doThrow, message)))
                    .doesNotThrowAnyException();
            should.assertThat(out.getAll().split(System.lineSeparator()))
                    .usingComparatorForType(ExceptionsTest::findRegexInActual, String.class)
                    .containsSubsequence(
                            "DEBUG.*start processing throwEarlyCatchLate with doThrow \\? " + doThrow + ", with message : " + message,
                            "INFO.*call external ressource",
                            "ERROR.*" + message,
                            "ERROR.*process fails",
                            "YourUseOfMyLibraryIsInvalid: " + message,
                            "INFO.*ensure resources are closed",
                            "ERROR.*throwEarlyCatchLate failed with doThrow \\? " + doThrow + ", with message : " + message,
                            "ERROR.*throwEarlyCatchLate failed due to :",
                            "BusinessDomainException: fail calling external library due to : " + message,
                            "Caused by:.*YourUseOfMyLibraryIsInvalid: " + message
                    );
            should.assertThat(result.get())
                    .isEqualTo("Error 500 due to : fail calling external library due to : " + message);
        }
    }
    // method that throws a child unchecked exception with catch on parent and child
    // method using try with ressources and suppressed exceptions
    // method that try str.append b4; call; str.append after catch (e) {str.append caught e; return} finally str.append finaly stil appending text at each step calling method that throws unchecked
    // method that try str.append b4; call; str.append after catch (e) {str.append caught e; return} finally str.append finaly stil appending text at each step calling method that don't throw

    // cascading exception catch (cause) throw new Ex("msg", cause) with three ex (Two causes) and neutrel methods


    private static int findRegexInActual(String actual, String expected) {
        Pattern p = Pattern.compile(expected);
        Matcher m = p.matcher(actual);
        return m.find() ? 0 : 1;
    }

}
