package io.permasoft.katas.javaplays.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "cycle")
public class SpringExceptionHandlingTest {
    @Test
    void name() {
        // made a cycle in test config to force a cascading exception in spring.
    }
}
