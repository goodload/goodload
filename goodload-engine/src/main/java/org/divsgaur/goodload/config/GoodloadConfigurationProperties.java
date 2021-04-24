package org.divsgaur.goodload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
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
    private DebuggingProperties debugging;

    @Data
    public static class DebuggingProperties {
        private boolean exportRawReport = false;

        private boolean exportTransformedRawReport = false;
    }

}
