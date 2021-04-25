package org.divsgaur.goodload.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.divsgaur.goodload.dsl.Simulation;
import org.divsgaur.goodload.exceptions.InvalidSimulationConfigFileException;
import org.divsgaur.goodload.exceptions.SimulatorInterruptedException;
import org.divsgaur.goodload.exceptions.UnknownExportFormatException;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handles various exceptions thrown by the engine at runtime and return the proper exit code.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Configuration
@Slf4j
public class DefaultExceptionHandlerConfiguration {

    /**
     * Maps the exceptions to corresponding exit codes.
     * @return Mapper containing information about which exception to map with which exit code.
     */
    @Bean
    ExitCodeExceptionMapper exitCodeExceptionMapper() {
        log.debug("Initializing exit code exception mapper.");
        return exception -> {
            if(exception.getCause() instanceof ParseException) {
                return 2;
            } else if(exception.getCause() instanceof InvalidSimulationConfigFileException) {
                log.error(exception.getCause().getMessage());
                return 3;
            } else if(exception.getCause() instanceof SimulatorInterruptedException) {
                log.error(exception.getCause().getMessage());
                return 4;
            } else if(exception.getCause() instanceof ClassNotFoundException) {
                log.error("Failed to find simulation class {}", exception.getCause().getMessage());
                return 5;
            } else if(exception.getCause() instanceof ClassCastException) {
                log.error("The specified simulation file is not an instance of {}. Actual error message: {}",
                        Simulation.class.getCanonicalName(),
                        exception.getCause().getMessage());
                return 6;
            } else if(exception.getCause() instanceof UnknownExportFormatException) {
                log.error(exception.getCause().getMessage());
                return 7;
            } else {
                log.error("Some error occurred during execution.");
                log.debug("Error details: {}", ExceptionUtils.getStackTrace(exception));
                return 1;
            }
        };
    }
}
