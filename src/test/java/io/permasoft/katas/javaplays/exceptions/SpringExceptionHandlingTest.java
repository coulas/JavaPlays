package io.permasoft.katas.javaplays.exceptions;

import io.permasoft.katas.javaplays.exceptions.api.ExceptionEndPoint;
import io.permasoft.katas.javaplays.exceptions.domain.BusinessDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Spring exception handling at startup time")
class SpringExceptionHandlingTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();
    private Logger log = LoggerFactory.getLogger(SpringExceptionHandlingTest.class);

    @Test
    @DisplayName("Cycle profile add a second bean for a bean definition without primary or qualifier available that make startup fails ")
    void duplicateBeanWithoutSelectorsFailsStartup() {
        contextRunner.withInitializer((context) ->
                        context.getEnvironment().addActiveProfile("cycle"))
                .withUserConfiguration(SpringSampleApplication.class)
                .run(applicationContext -> {
                            log.warn("this run failed as expected : ", applicationContext.getStartupFailure());
                            assertThat(applicationContext)
                                    .getFailure()
                                    .isInstanceOf(UnsatisfiedDependencyException.class)
                                    .hasMessageContainingAll(
                                            "UnsatisfiedDependencyException",
                                            "exceptionEndPoint",
                                            "exceptionUseCases",
                                            "exceptionStore",
                                            "No qualifying bean of type", "exceptionDao", "expected single matching bean but found 2: exceptionDaoFailingImpl,exceptionDaoImpl")
                                    .getRootCause().hasMessageContainingAll(
                                            "No qualifying bean of type", "exceptionDao", "expected single matching bean but found 2: exceptionDaoFailingImpl,exceptionDaoImpl");
                        }
                );
    }

}
