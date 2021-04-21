package org.divsgaur.goodload.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Component
public class ReportAggregator {
    private ObjectMapper mapper = new ObjectMapper();

    @Resource
    private UserArgs userArgs;

    public AggregateReport aggregate(String simulationName, List<Report> rawReports, long totalSimulationRunTime) {
        var finalReport = aggregate(rawReports);
        finalReport.setTotalTimeInMillis(totalSimulationRunTime);
        return finalReport;
    }

    private AggregateReport aggregate(List<Report> rawReportList) {
        if(rawReportList == null || rawReportList.isEmpty()) {
            return null;
        }

        int subStepCount = rawReportList.get(0).getSubSteps().size();

        AggregateReport aggregateReportForStep = new AggregateReport();
        aggregateReportForStep.setRawReports(rawReportList);
        aggregateReportForStep.setStepName(rawReportList.get(0).getStepName());

        aggregateReportForStep.setSubSteps(new ArrayList<>());
        for(int subStepIndex =0; subStepIndex < subStepCount; subStepIndex++) {
            List<Report> nestedRawReportList = new ArrayList<>();
            if(rawReportList.get(0).getSubSteps() != null && !rawReportList.get(0).getSubSteps().isEmpty()) {
                for (Report report : rawReportList) {
                    nestedRawReportList.add(report
                            .getSubSteps()
                            .get(subStepIndex));
                }
                AggregateReport aggregateReportForSubStep = aggregate(nestedRawReportList);
                aggregateReportForStep.getSubSteps().add(aggregateReportForSubStep);
            }
        }
        if(!aggregateReportForStep.getRawReports().isEmpty()) {
            aggregateReportForStep.setErrorsOccured(
                    aggregateReportForStep
                            .getRawReports()
                            .stream().anyMatch(report -> !report.isEndedNormally())
            );

            aggregateReportForStep.setAverageTimeInMillis(
                    aggregateReportForStep
                            .getRawReports()
                            .stream()
                            .map(Report::getTotalTimeInMillis)
                            .reduce(0L, Long::sum)
                            / aggregateReportForStep.getRawReports().size()
            );

            redactRawReports(aggregateReportForStep);
        }

        return aggregateReportForStep;
    }

    private void redactRawReports(AggregateReport aggregateReport) {
        if(userArgs.getConfiguration().getReporting().isIncludeRawReport()) {
            for (Report rawReport : aggregateReport.getRawReports()) {
                rawReport.setSubSteps(null);
            }
        } else {
            aggregateReport.setRawReports(null);
        }
    }
}
