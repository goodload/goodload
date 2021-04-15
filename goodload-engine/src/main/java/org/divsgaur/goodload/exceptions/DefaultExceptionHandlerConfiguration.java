package org.divsgaur.goodload.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DefaultExceptionHandlerConfiguration {

    @Bean
    ExitCodeExceptionMapper exitCodeExceptionMapper() {
        log.debug("Initializing exit code exception mapper.");
        return exception -> {
            if(exception.getCause() instanceof ParseException)
                return 2;
            else
                return 1;
        };
    }
}
