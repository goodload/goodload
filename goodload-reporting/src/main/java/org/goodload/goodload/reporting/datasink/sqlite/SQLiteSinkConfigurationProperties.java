package org.goodload.goodload.reporting.datasink.sqlite;

import lombok.Data;
import org.goodload.goodload.reporting.config.ReportingConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SQLiteSinkConfigurationProperties.PREFIX)
@Data
public class SQLiteSinkConfigurationProperties {
    public static final String PREFIX = ReportingConfigurationProperties.PREFIX + ".sink.sqlite.batch-size";

    private int batchSize = 1000;
}
