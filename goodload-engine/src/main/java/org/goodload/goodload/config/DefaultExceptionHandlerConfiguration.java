/*
Copyright (C) 2021 Goodload

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.goodload.goodload.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.goodload.goodload.dsl.Simulation;
import org.goodload.goodload.exceptions.InvalidSimulationConfigFileException;
import org.goodload.goodload.exceptions.SimulatorInterruptedException;
import org.goodload.goodload.exceptions.UnknownExportFormatException;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handles various exceptions thrown by the engine at runtime and return the proper exit code.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Configuration
@Slf4j
public class DefaultExceptionHandlerConfiguration {

    /**
     * Maps the exceptions to corresponding exit codes.
     * @return Mapper containing information about which exception to map with which exit code.
     * @since 1.0
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
            } else if(exception.getCause() instanceof SimulatorInterruptedException
                    || exception.getCause() instanceof InterruptedException) {
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
