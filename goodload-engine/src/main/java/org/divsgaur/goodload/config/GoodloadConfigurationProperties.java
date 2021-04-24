package org.divsgaur.goodload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@ConfigurationProperties(value= "goodload")
@Data
public class GoodloadConfigurationProperties {
    private static final long serialVersionUID = 1L;

    /**
     * Maximum value for hold-for. If hold-for value is more than this in simulation configuration,
     * it will be ignored and this value will be used instead.
     */
    private String maxHoldFor = "1h";

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
