package org.divsgaur.goodload.reporting;

import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Component
public class ReportAggregator {
    @Resource
    private UserArgs userArgs;

    @Resource
    private ReportExporter reportExporter;

    public AggregateReport aggregate(String simulationName, List<Report> rawReports, long totalSimulationRunTime) {
        reportExporter.exportRawIfEnabled(simulationName, rawReports);

        List<Report> transformedRawReport = new ArrayList<>(rawReports.size());

        for(var report: rawReports) {
            transformedRawReport.addAll(report.getIterations());
        }
        reportExporter.exportTransformedIfEnabled(simulationName, transformedRawReport);

        var finalReport = aggregate(transformedRawReport);
        finalReport.setTotalTimeInMillis(totalSimulationRunTime);
        finalReport.setIterations(rawReports.size());
        return finalReport;
    }

    private AggregateReport aggregate(List<Report> rawReportList) {
        if(rawReportList == null || rawReportList.isEmpty()) {
            return null;
        }

        int subStepCount = Optional.ofNullable(rawReportList.get(0).getSubSteps()).orElse(Collections.emptyList()).size();

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
