package org.divsgaur.goodload.reporting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.divsgaur.goodload.config.GoodloadConfiguration;
import org.divsgaur.goodload.config.TestConfiguration;
import org.divsgaur.goodload.userconfig.GoodloadUserConfigurationProperties;
import org.divsgaur.goodload.userconfig.ReportingConfiguration;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GoodloadConfiguration.class, TestConfiguration.class})
public class ReportAggregatorTests {
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ReportAggregator reportAggregator;

    @MockBean
    private UserArgs userArgs;

    @BeforeEach
    private void setup() {
        when(userArgs.getConfiguration()).thenReturn(mock(GoodloadUserConfigurationProperties.class));
        when(userArgs.getConfiguration().getReporting()).thenReturn(mock(ReportingConfiguration.class));
    }

    public void testAggregator(boolean rawDataIncludedInAggregate) throws IOException {
        List<Report> rawReports = objectMapper.readValue(
                ResourceUtils.getFile("classpath:sample-data/raw-report.json"),
                new TypeReference<>(){});
        AggregateReport aggregateReport = objectMapper.readValue(
                ResourceUtils.getFile(rawDataIncludedInAggregate ?
                        "classpath:sample-data/aggregated-report-with-raw-data.json"
                        : "classpath:sample-data/aggregated-report-without-raw-data.json"),
                AggregateReport.class);

        Assertions.assertEquals(
                objectMapper.writeValueAsString(aggregateReport),
                objectMapper.writeValueAsString(reportAggregator.aggregate("Sample simulation", rawReports,0L)));
    }

    @Test
    public void testAggregatorWithRawDataEnabled() throws IOException {
        when(userArgs.getConfiguration().getReporting().isIncludeRawReport())
                .thenReturn(true);
        testAggregator(true);
    }

    @Test
    public void testAggregatorWithRawDataDisabled() throws IOException {
        when(userArgs.getConfiguration().getReporting().isIncludeRawReport())
                .thenReturn(false);
        testAggregator(false);
    }
}
