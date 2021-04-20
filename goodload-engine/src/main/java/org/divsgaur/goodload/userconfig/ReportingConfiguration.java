package org.divsgaur.goodload.userconfig;

import lombok.Data;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Data
public class ReportingConfiguration {
    /**
     * If true, the generated report will include raw data.
     * We recommend to keep it disabled (set to {@code false}) for improved performance and smaller memory footprint.
     */
    private boolean includeRawReport = false;
}
