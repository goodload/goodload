/*
 * Copyright (C) 2021 Goodload
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.goodload.goodload.reporting;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {GoodloadConfiguration.class, TestConfiguration.class})
public class ReportAggregatorTests {
/*    @Resource
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
        List<SimulationReport> rawReports = objectMapper.readValue(
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
    */
}