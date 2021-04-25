package org.divsgaur.goodload.reporting;

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