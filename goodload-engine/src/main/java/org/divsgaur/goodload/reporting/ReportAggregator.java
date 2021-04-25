package org.divsgaur.goodload.reporting;

import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Aggregates the raw thread-wise report generated for a simulation.
 *
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

        /*
         * Raw reports contain n iterations level report in 1 thread level report, and there are m such thread reports.
         * Linearize thread level reports and thread-iteration level reports.
         * The transformed report will have all the reports flattened as list of iterations, and hence will be a list of
         * n*m reports.
         */
        for(var report: rawReports) {
            transformedRawReport.addAll(report.getIterations());
        }
        reportExporter.exportTransformedIfEnabled(simulationName, transformedRawReport);

        var finalReport = aggregate(transformedRawReport);
        finalReport.setTotalTimeInMillis(totalSimulationRunTime);
        finalReport.setIterations(transformedRawReport.size());
        return finalReport;
    }

    /**
     * Recursively aggregate the raw reports for all the iterations.
     * @param rawReportList The list of a step where each item in the list
     *                      is report of the same step in different iteration.
     * @return Aggregate report for the step.
     */
    private AggregateReport aggregate(List<Report> rawReportList) {
        if(rawReportList == null || rawReportList.isEmpty()) {
            return null;
        }

        // Number of substeps of current step
        int subStepCount = Optional.ofNullable(rawReportList.get(0).getSubSteps()).orElse(Collections.emptyList()).size();

        AggregateReport aggregateReportForStep = new AggregateReport();
        aggregateReportForStep.setRawReports(rawReportList);
        aggregateReportForStep.setStepName(rawReportList.get(0).getStepName());

        // Recursively aggregate the sub step reports
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

        // Aggregate the report for the current step.
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
            aggregateReportForStep.setIterations(aggregateReportForStep.getRawReports().size());

            redactRawReports(aggregateReportForStep);
        }

        return aggregateReportForStep;
    }

    /**
     * Remove raw report information from the aggregate report.
     * <br>
     * If the configuration property {@code goodload.reporting.include-raw-report} is true,
     * then removes only the nested report information from raw reports for current step,
     * otherwise removes the complete raw report information.
     * @param aggregateReport The aggregate report from which to remove raw report information.
     */
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
