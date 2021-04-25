package org.divsgaur.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

/**
 * The configuration properties relating to how the reports are generated.
 * The prefix is {@code goodload.reporting}
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Data
public class ReportingConfiguration {
    /**
     * If true, the generated report will include raw data.
     * We recommend to keep it disabled (set to {@code false}) for improved performance and smaller memory footprint.
     */
    @JsonProperty(value="include-raw-report")
    private boolean includeRawReport = false;

    @JsonProperty(value="export-formats")
    private Set<String> exportFormats;

    @JsonProperty(value="export-directory-path")
    private String exportDirectoryPath;
}
