package io.permasoft.katas.javaplays.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SpringExceptionHandlingTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();
    private Logger log = LoggerFactory.getLogger(SpringExceptionHandlingTest.class);

    @Test
    @DisplayName("raw springBootApplication works")
    void without_added_profiles_it_just_works() {
        // made a cycle in test config to force a cascading exception in spring.
        contextRunner
                .withUserConfiguration(SpringSampleApplication.class)
                .run(applicationContext -> {
                            log.warn("this run hasn't failed as expected : "+ applicationContext.getStartupFailure());
                            assertThat(applicationContext)
                                    .hasNotFailed();
                        }
                );
    }

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
