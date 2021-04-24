package org.divsgaur.goodload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@ConfigurationProperties(value= "goodload.debugging")
@Data
public class GoodloadConfigurationProperties {
    private static final long serialVersionUID = 1L;

    private boolean exportRawReport = false;

    private boolean exportTransformedRawReport = false;

    public void setExportTransformedRawReport(boolean exportTransformedRawReport) {
        this.exportTransformedRawReport = exportTransformedRawReport;
    }
}
