package org.divsgaur.goodload.reporting;

import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.reporting.reports.aggregate.AggregateActionReport;
import org.divsgaur.goodload.reporting.reports.aggregate.AggregateSimulationReport;
import org.divsgaur.goodload.reporting.reports.raw.ActionReport;
import org.divsgaur.goodload.reporting.reports.raw.Report;
import org.divsgaur.goodload.reporting.reports.raw.SimulationReport;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.lang.Nullable;
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
 * @since 1.0
 */
@Component
@Slf4j
public class ReportAggregator {
    @Resource
    private UserArgs userArgs;

    @Resource
    private ReportExporter reportExporter;

    @Nullable
    public AggregateSimulationReport aggregate(String simulationName, List<SimulationReport> rawReports, long totalSimulationRunTime) {
        reportExporter.exportRawIfEnabled(simulationName, rawReports);

        if(rawReports == null || rawReports.isEmpty()) {
            log.info("No reports found. Skipping export.");
            return null;
        }

        int scenarioCount = rawReports.get(0).getScenarios().size();

        var finalSimulationReport = new AggregateSimulationReport(simulationName);
        finalSimulationReport.setTotalTimeInMillis(totalSimulationRunTime);

        for(int scenarioIndex = 0; scenarioIndex < scenarioCount; scenarioIndex++) {
            var transformedRawReports = new ArrayList<ActionReport>();
            /*
             * Raw reports contain n iterations level report in 1 thread level report,
             * and there are m such thread reports.
             * We linearize thread level reports and thread-iteration level reports
             * as the aggregate only needs to know the total iterations and not the
             * thread-wise information. However, if thread-wise information is needed,
             * the information is already preserved in the reports as Report.runnerId.
             * The transformed report will have all the reports flattened as list of
             * iterations, and hence will be a list of n*m reports.
             */
            for(var report: rawReports) {
                transformedRawReports.addAll(report.getScenarios().get(scenarioIndex).getIterations());
            }
            reportExporter.exportTransformedIfEnabled(simulationName, transformedRawReports);

            var finalScenarioReport = aggregate(transformedRawReports);
            finalScenarioReport.setTotalTimeInMillis(totalSimulationRunTime);
            finalScenarioReport.setIterations(transformedRawReports.size());

            finalSimulationReport.getScenarios().add(finalScenarioReport);
        }

        return finalSimulationReport;
    }

    /**
     * Recursively aggregate the raw reports for all the iterations.
     * @param rawReportList The list of a step where each item in the list
     *                      is report of the same step in different iteration.
     * @return Aggregate report for the step.
     */
    private AggregateActionReport aggregate(List<ActionReport> rawReportList) {
        if(rawReportList == null || rawReportList.isEmpty()) {
            return null;
        }

        // Number of substeps of current step
        int subStepCount = Optional.ofNullable(rawReportList.get(0).getSubSteps()).orElse(Collections.emptyList()).size();

        AggregateActionReport aggregateReportForStep = new AggregateActionReport(rawReportList.get(0).getStepName());

        // Recursively aggregate the sub step reports
        aggregateReportForStep.setSubSteps(new ArrayList<>());
        for(int subStepIndex =0; subStepIndex < subStepCount; subStepIndex++) {
            List<ActionReport> nestedRawReportList = new ArrayList<>();
            if(rawReportList.get(0).getSubSteps() != null && !rawReportList.get(0).getSubSteps().isEmpty()) {
                for (ActionReport report : rawReportList) {
                    nestedRawReportList.add(report
                            .getSubSteps()
                            .get(subStepIndex));
                }
                AggregateActionReport aggregateReportForSubStep = aggregate(nestedRawReportList);
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
    private void redactRawReports(AggregateActionReport aggregateReport) {
        if(userArgs.getConfiguration().getReporting().isIncludeRawReport()) {
            for (var rawReport : aggregateReport.getRawReports()) {
                rawReport.setSubSteps(null);
            }
        } else {
            aggregateReport.setRawReports(null);
        }
    }
}
