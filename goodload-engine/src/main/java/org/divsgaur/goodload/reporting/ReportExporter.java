package org.divsgaur.goodload.reporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.config.GoodloadConfigurationProperties;
import org.divsgaur.goodload.exceptions.UnknownExportFormatException;
import org.divsgaur.goodload.internal.Util;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exports the generated reports in various file formats.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Component
@Slf4j
public class ReportExporter {
    @Resource
    private UserArgs userArgs;

    @Resource
    private GoodloadConfigurationProperties configuration;

    /**
     * Export the aggregate report for each simulation after it has completed.
     * The name of the exported file is in format {@code goodload-report-&lt;timestamp&gt;}
     * @param report The aggregate report to export
     * @throws IOException If the export files couldn't be created or written to.
     * @throws UnknownExportFormatException If any of the formats defined in
     *                                      {@code goodload.reporting.export-formats} is not recognized.
     */
    public void export(List<AggregateReport> report) throws IOException, UnknownExportFormatException {
        long currentTimestamp = Util.currentTimestamp();
        var exportFileName = "goodload-report-" + currentTimestamp;

        export(report, exportFileName);

        log.info("Exported aggregate reports to {} directory.",
                new File(userArgs.getConfiguration().getReporting().getExportDirectoryPath()).getAbsolutePath());
    }

    /**
     * Exports raw report to a file if {@code goodload.engine.debugging.exportRawReport=true}.
     * The name of the exported file is in format {@code &lt;simulation name&gt;-raw-report-&lt;timestamp&gt;}
     * @param simulationName The name of the simulation to which the report belongs.
     * @param report The report to export.
     */
    public void exportRawIfEnabled(String simulationName, List<Report> report) {
        if(configuration.getDebugging().isExportRawReport()) {
            try {
                long currentTimestamp = Util.currentTimestamp();
                var exportFileName = simulationName + "-raw-report-" + currentTimestamp;

                export(report, exportFileName);

            } catch (Exception e) {
                log.error("Failed to export raw report: ", e);
            }
        }
    }

    /**
     * Exports transformed raw report to a file if {@code goodload.engine.debugging.exportTransformedReport=true}.
     * The name of the exported file is in format {@code &lt;simulation name&gt;-trasformed-raw-report-&lt;timestamp&gt;}
     * @param simulationName The name of the simulation to which the report belongs.
     * @param report The report to export.
     */
    public void exportTransformedIfEnabled(String simulationName, List<Report> report) {
        if(configuration.getDebugging().isExportTransformedRawReport()) {
            try {
                long currentTimestamp = Util.currentTimestamp();
                var exportFileName = simulationName + "-transformed-raw-report-" + currentTimestamp;

                export(report, exportFileName);

            } catch (Exception e) {
                log.error("Failed to export transformed raw report: ", e);
            }
        }
    }

    private File getFileFromPath(File directory, String relativePathToFile) {
        return Paths.get(directory.toPath().toString(), relativePathToFile).toFile();
    }

    /**
     * Exports an object to given filename.
     * The directory path is fetched from configuration property {@code goodload.reporting.export-directory-path}.
     * Hence the absolute path of the exported file will be
     * {@code &lt;goodload.reporting.export-directory-path&gt;/&lt;exportFileName&gt;}
     *
     * @param object The object to export.
     * @param exportFileName The name of the file to which the object will be written.
     * @throws UnknownExportFormatException If any of the formats defined in
     *                                      {@code goodload.reporting.export-formats} is not recognized.
     * @throws IOException If the export directory was not found or the file couldn't be written.
     */
    private void export(Object object, String exportFileName) throws UnknownExportFormatException, IOException {
        Set<String> exportFormats = new HashSet<>(userArgs.getConfiguration().getReporting().getExportFormats());
        File exportDirectory = new File(userArgs.getConfiguration().getReporting().getExportDirectoryPath());
        if (exportFormats.contains("json")) {
            var objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            var exportFile = getFileFromPath(exportDirectory, exportFileName + ".json");
            objectMapper.writeValue(exportFile, object);

            exportFormats.remove("json");
        }
        if (exportFormats.contains("json-pretty")) {
            var objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            var exportFile = getFileFromPath(exportDirectory, exportFileName + "-pretty.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(exportFile, object);

            exportFormats.remove("json-pretty");
        }
        if (exportFormats.contains("yaml")) {
            var yamlWriter = new ObjectMapper(new YAMLFactory());
            yamlWriter.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            var exportFile = getFileFromPath(exportDirectory, exportFileName + ".yaml");
            yamlWriter.writeValue(exportFile, object);

            exportFormats.remove("yaml");
        }

        // If any export format couldn't be processed
        if (!exportFormats.isEmpty()) {
            throw new UnknownExportFormatException(String.format("The export formats `%s` are not recognized", exportFormats));
        }
    }

}
