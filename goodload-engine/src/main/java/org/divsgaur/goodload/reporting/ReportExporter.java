package org.divsgaur.goodload.reporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.divsgaur.goodload.exceptions.UnknownExportFormatException;
import org.divsgaur.goodload.internal.Util;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Component
public class ReportExporter {
    @Resource
    private UserArgs userArgs;

    public void export(List<AggregateReport> report) throws IOException, UnknownExportFormatException {
        Set<String> exportFormats = userArgs.getConfiguration().getReporting().getExportFormats();
        File exportDirectory = new File(userArgs.getConfiguration().getReporting().getExportDirectoryPath());

        long currentTimestamp = Util.currentTimestamp();
        var exportFileName = "goodload-report-" + currentTimestamp;

        if(exportFormats.contains("json")) {
            var objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            var exportFile = getFileFromPath(exportDirectory, exportFileName + ".json");
            objectMapper.writeValue(exportFile, report);

            exportFormats.remove("json");
        }
        if(exportFormats.contains("json-pretty")) {
            var objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            var exportFile = getFileFromPath(exportDirectory, exportFileName + "-pretty.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(exportFile, report);

            exportFormats.remove("json-pretty");
        }
        if(exportFormats.contains("yaml")) {
            var yamlWriter = new ObjectMapper(new YAMLFactory());
            yamlWriter.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            var exportFile = getFileFromPath(exportDirectory, exportFileName + ".yaml");
            yamlWriter.writeValue(exportFile, report);

            exportFormats.remove("yaml");
        }

        // If any export format couldn't be processed
        if(!exportFormats.isEmpty()) {
            throw new UnknownExportFormatException(String.format("The export formats `%s` are not recognized", exportFormats));
        }
    }

    private File getFileFromPath(File directory, String relativePathToFile) {
        return Paths.get(directory.toPath().toString(), relativePathToFile).toFile();
    }
}
