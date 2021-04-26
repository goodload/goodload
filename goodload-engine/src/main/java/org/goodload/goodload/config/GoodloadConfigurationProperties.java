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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties used to cnfigure the goodload engine before the simulations are run.
 * These affect how the simulations will be executed.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@ConfigurationProperties(value= "goodload.engine")
@Data
public class GoodloadConfigurationProperties {
    private static final long serialVersionUID = 1L;

    /**
     * Maximum value for hold-for. If hold-for value is more than this in simulation configuration,
     * it will be ignored and this value will be used instead.
     */
    private String maxHoldFor = "2h";

    /**
     * Percentage of max hold for value to use as grace period for long running tasks.
     * hold-for prevents only execution of next steps.
     * But if some simulation's step is stuck in a loop or infinite recursion then the engine will wait for
     * some grace period after the hold-for duration has expired for the step to complete.
     * If it is still not completed then the simulation will end forcefully with exception.
     */
    private int gracePeriodPercentage = 20;

    /**
     * Properties related to debugging
     */
    private DebuggingProperties debugging = new DebuggingProperties();

    @Data
    public static class DebuggingProperties {
        /**
         * If true, the actual reports generated by the engine for each thread will be exported as a separate file.
         * This is different from goodload.reporting.include-raw-report which is provided in user config along
         * with the simulations.
         */
        private boolean exportRawReport = false;

        /**
         * Export the transformed report generated after flattening the thread level report and simulation
         * level report by ReportAggregator as a separte file.
         */
        private boolean exportTransformedRawReport = false;
    }

}