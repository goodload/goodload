package org.divsgaur.goodload.userconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * The configuration properties relating to how the reports are generated.
 * The prefix is {@code goodload.reporting}
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Data
public class ReportingConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * If true, the generated report will include raw data.
     * We recommend to keep it disabled (set to {@code false}) for improved
     * performance and smaller memory footprint.
     * @since 1.0
     */
    @JsonProperty(value="include-raw-report")
    private boolean includeRawReport = false;

    /**
     * The list of formats in which the simulation reports will be exported.
     * <strong>Accepted values</strong>
     * <ul>
     *     <li><u>json</u> : The report will be exported as json without any spaces.
     *     Reduces export file size.</li>
     *     <li><u>json-pretty</u> : JSON formatted to be understood easily by humans.</li>
     *     <li><u>yaml</u> : Humand readable YAML format.</li>
     * </ul>
     * @since 1.0
     */
    @JsonProperty(value="export-formats")
    private Set<String> exportFormats;

    /**
     * The absolute or relative path to the directory where the exported reports will
     * be saved. If relative path is provided, the path will be considered relative to
     * the directory from where the simulator engine is started.
     */
    @JsonProperty(value="export-directory-path")
    private String exportDirectoryPath;
}
