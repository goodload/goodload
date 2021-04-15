package org.divsgaur.goodload.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.divsgaur.goodload.exceptions.InvalidSimulationConfigFileException;
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
            else if(exception.getCause() instanceof InvalidSimulationConfigFileException) {
                log.error(exception.getCause().getMessage());
                return 3;
            }
            else {
                log.error("Some error occurred during execution.");
                log.debug("Error details: {}", ExceptionUtils.getStackTrace(exception));
                return 1;
            }
        };
    }
}
