package org.divsgaur.goodload.reporting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class ReportAggregator {
    private ObjectMapper mapper = new ObjectMapper();

    public AggregateReport aggregate(String simulationName, List<Report> reportPerThread, long totalSimulationRunTime) {

        try {
            List<Report> rawReports = mapper.readValue("[\n" +
                    "    {\n" +
                    "        \"stepName\": \"Root\",\n" +
                    "        \"subSteps\": [\n" +
                    "            {\n" +
                    "                \"stepName\": \"Level1 Step 1\",\n" +
                    "                \"subSteps\": [\n" +
                    "                    {\n" +
                    "                        \"stepName\": \"Level2 Step 1\",\n" +
                    "                        \"subSteps\": [\n" +
                    "                            {\n" +
                    "                                \"stepName\": \"Level3 Step 1\",\n" +
                    "                                \"subSteps\": [\n" +
                    "                                    \n" +
                    "                                ],\n" +
                    "                                \"totalTimeInMillis\": 25,\n" +
                    "                                \"runnerId\": 2,\n" +
                    "                                \"endedNormally\":false\n" +
                    "                            }\n" +
                    "                        ],\n" +
                    "                        \"totalTimeInMillis\": 25,\n" +
                    "                        \"runnerId\": 2,\n" +
                    "                        \"endedNormally\":false\n" +
                    "                    }        \n" +
                    "                ],\n" +
                    "                \"totalTimeInMillis\": 24,\n" +
                    "                \"runnerId\": 2,\n" +
                    "                \"endedNormally\":true\n" +
                    "            },      \n" +
                    "            {\n" +
                    "                \"stepName\": \"Level1 Step 2\",\n" +
                    "                \"subSteps\": [\n" +
                    "                    \n" +
                    "                ],\n" +
                    "                \"totalTimeInMillis\": 25,\n" +
                    "                \"runnerId\": 2,\n" +
                    "                \"endedNormally\":false\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"totalTimeInMillis\": 24,\n" +
                    "        \"runnerId\": 2,\n" +
                    "        \"endedNormally\":true\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"stepName\": \"Root\",\n" +
                    "        \"subSteps\": [\n" +
                    "            {\n" +
                    "                \"stepName\": \"Level1 Step 1\",\n" +
                    "                \"subSteps\": [\n" +
                    "                    {\n" +
                    "                        \"stepName\": \"Level2 Step 1\",\n" +
                    "                        \"subSteps\": [\n" +
                    "                            {\n" +
                    "                                \"stepName\": \"Level3 Step 1\",\n" +
                    "                                \"subSteps\": [\n" +
                    "                                    \n" +
                    "                                ],\n" +
                    "                                \"totalTimeInMillis\": 25,\n" +
                    "                                \"runnerId\": 0,\n" +
                    "                                \"endedNormally\":false\n" +
                    "                            }\n" +
                    "                        ],\n" +
                    "                        \"totalTimeInMillis\": 25,\n" +
                    "                        \"runnerId\": 0,\n" +
                    "                        \"endedNormally\":false\n" +
                    "                    }        \n" +
                    "                ],\n" +
                    "                \"totalTimeInMillis\": 24,\n" +
                    "                \"runnerId\": 0,\n" +
                    "                \"endedNormally\":true\n" +
                    "            },      \n" +
                    "            {\n" +
                    "                \"stepName\": \"Level1 Step 2\",\n" +
                    "                \"subSteps\": [\n" +
                    "                    \n" +
                    "                ],\n" +
                    "                \"totalTimeInMillis\": 25,\n" +
                    "                \"runnerId\": 0,\n" +
                    "                \"endedNormally\":false\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"totalTimeInMillis\": 24,\n" +
                    "        \"runnerId\": 0,\n" +
                    "        \"endedNormally\":true\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"stepName\": \"Root\",\n" +
                    "        \"subSteps\": [\n" +
                    "            {\n" +
                    "                \"stepName\": \"Level1 Step 1\",\n" +
                    "                \"subSteps\": [\n" +
                    "                    {\n" +
                    "                        \"stepName\": \"Level2 Step 1\",\n" +
                    "                        \"subSteps\": [\n" +
                    "                            {\n" +
                    "                                \"stepName\": \"Level3 Step 1\",\n" +
                    "                                \"subSteps\": [\n" +
                    "                                    \n" +
                    "                                ],\n" +
                    "                                \"totalTimeInMillis\": 25,\n" +
                    "                                \"runnerId\": 1,\n" +
                    "                                \"endedNormally\":false\n" +
                    "                            }\n" +
                    "                        ],\n" +
                    "                        \"totalTimeInMillis\": 25,\n" +
                    "                        \"runnerId\": 1,\n" +
                    "                        \"endedNormally\":false\n" +
                    "                    }        \n" +
                    "                ],\n" +
                    "                \"totalTimeInMillis\": 24,\n" +
                    "                \"runnerId\": 1,\n" +
                    "                \"endedNormally\":true\n" +
                    "            },      \n" +
                    "            {\n" +
                    "                \"stepName\": \"Level1 Step 2\",\n" +
                    "                \"subSteps\": [\n" +
                    "                    \n" +
                    "                ],\n" +
                    "                \"totalTimeInMillis\": 25,\n" +
                    "                \"runnerId\": 1,\n" +
                    "                \"endedNormally\":false\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"totalTimeInMillis\": 24,\n" +
                    "        \"runnerId\": 1,\n" +
                    "        \"endedNormally\":true\n" +
                    "    }\n" +
                    "]", new TypeReference<>() {
            });
            var finalReport = aggregate(rawReports, rawReports.size(), rawReports.get(0).getSubSteps().size());
            //AggregateReport aggregateReport = aggregate(rawReports, );
            return finalReport;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private AggregateReport aggregate(List<Report> rawReportList, int threadCount, int subStepCount) throws JsonProcessingException {
        long totalTimeSum = 0;
        long avgTime = 0;

        if(rawReportList == null || rawReportList.isEmpty()) {
            return null;
        }

        AggregateReport aggregateReportForStep = new AggregateReport();
        aggregateReportForStep.setRawReports(rawReportList);
        aggregateReportForStep.setStepName(rawReportList.get(0).getStepName());

        aggregateReportForStep.setSubSteps(new ArrayList<>());
        for(int subStepIndex =0; subStepIndex < subStepCount; subStepIndex++) {
            List<Report> nestedRawReportList = new ArrayList<>();
            if(rawReportList.get(0).getSubSteps() != null && !rawReportList.get(0).getSubSteps().isEmpty()) {
                for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                    nestedRawReportList.add(rawReportList
                            .get(threadIndex)
                            .getSubSteps()
                            .get(subStepIndex));
                }
                AggregateReport aggregateReportForSubStep = aggregate(nestedRawReportList, threadCount, nestedRawReportList.get(0).getSubSteps().size());
                aggregateReportForStep.getSubSteps().add(aggregateReportForSubStep);
            }
        }
        if(!aggregateReportForStep.getRawReports().isEmpty()) {
            aggregateReportForStep.setAverageTimeInMillis(
                    aggregateReportForStep
                            .getRawReports()
                            .stream()
                            .map(Report::getTotalTimeInMillis)
                            .reduce(0L, Long::sum)
                            / aggregateReportForStep.getRawReports().size()
            );
        };

        return aggregateReportForStep;
    }
}
