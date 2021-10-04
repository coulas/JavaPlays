package io.permasoft.katas.javaplays.exceptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSampleApplication.class, args);
    }
}
