package io.permasoft.katas.javaplays.exceptions;

import io.permasoft.katas.javaplays.exceptions.api.ExceptionEndPoint;
import io.permasoft.katas.javaplays.exceptions.domain.BusinessDomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
//@AutoConfigureMockMvc(SpringBootTest.WebEnvironment.MOCK)
public class SpringExceptionApplicationTest {
    //    @Autowired
//    MockMvc mvc;
    @Autowired
    ExceptionEndPoint endPoint;

    @Test
    void nominal_without_failure() {
        AtomicReference<String> result = new AtomicReference<>();
        assertThatCode(() -> {
            result.set(endPoint.endPointSucceedsHandledByFramework());
        }).doesNotThrowAnyException();
        assertThat(result.get()).isEqualTo("1, return from try, modify result in finally.");
    }

    @Test
    void with_failure() {
        AtomicReference<String> result = new AtomicReference<>();
        assertThatCode(() -> result.set(endPoint.endPointFailsHandledByFramework()))
                .isInstanceOf(BusinessDomainException.class)
                .hasMessageFindingMatch("Negative.*invalid");
        assertThat(result.get()).isNull();
    }
}
