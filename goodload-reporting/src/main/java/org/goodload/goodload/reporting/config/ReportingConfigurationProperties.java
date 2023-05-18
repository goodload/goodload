package org.goodload.goodload.reporting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = ReportingConfigurationProperties.PREFIX)
public class ReportingConfigurationProperties {

    public static final String PREFIX = "goodload.reporting";

    private SinkType sinkType;

    private enum SinkType {
        SQLite
    }
}
