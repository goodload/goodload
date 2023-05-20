/*
 * Copyright (C) 2021 Divyansh Shekhar Gaur
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

import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.reporting.reports.aggregate.AggregateActionReport;
import org.goodload.goodload.reporting.reports.aggregate.AggregateSimulationReport;
import org.goodload.goodload.reporting.reports.raw.ActionReport;
import org.goodload.goodload.reporting.reports.raw.SimulationReport;
import org.goodload.goodload.userconfig.ParsedUserArgs;
import org.goodload.goodload.userconfig.UserArgs;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private ParsedUserArgs parsedUserArgs;

    @Resource
    private ReportExporter reportExporter;

    @Nullable
    public AggregateSimulationReport aggregate(
            String simulationName,
            List<SimulationReport> rawReports,
            long totalSimulationRunTime) {
        reportExporter.exportRawIfEnabled(simulationName, rawReports);

        if (rawReports == null || rawReports.isEmpty()) {
            log.info("No reports found. Skipping export.");
            return null;
        }

        int scenarioCount = rawReports.get(0).getScenarios().size();

        var finalSimulationReport = new AggregateSimulationReport(simulationName);
        finalSimulationReport.setTotalTimeInMillis(totalSimulationRunTime);

        for (var scenarioIndex = 0; scenarioIndex < scenarioCount; scenarioIndex++) {
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
            for (var report : rawReports) {
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
     *
     * @param rawReportList The list of a step where each item in the list
     *                      is report of the same step in different iteration.
     * @return Aggregate report for the step.
     */
    private AggregateActionReport aggregate(List<ActionReport> rawReportList) {
        if (rawReportList == null || rawReportList.isEmpty()) {
            return new AggregateActionReport(null);
        }

        // Number of substeps of current step
        int subStepCount =
                Optional.ofNullable(rawReportList.get(0).getSubSteps()).orElse(Collections.emptyList()).size();

        var aggregateReportForStep = new AggregateActionReport(rawReportList.get(0).getStepName());
        aggregateReportForStep.setRawReports(rawReportList);
        aggregateReportForStep.setIterationsStartTimestamp(Long.MAX_VALUE);
        aggregateReportForStep.setIterationsEndTimestamp(Long.MIN_VALUE);

        // Recursively aggregate the sub step reports
        aggregateReportForStep.setSubSteps(new ArrayList<>());
        for (var subStepIndex = 0; subStepIndex < subStepCount; subStepIndex++) {
            List<ActionReport> nestedRawReportList = new ArrayList<>();
            if (rawReportList.get(0).getSubSteps() != null && !rawReportList.get(0).getSubSteps().isEmpty()) {
                for (ActionReport report : rawReportList) {
                    nestedRawReportList.add(report
                            .getSubSteps()
                            .get(subStepIndex));
                }
                var aggregateReportForSubStep = aggregate(nestedRawReportList);
                aggregateReportForStep.getSubSteps().add(aggregateReportForSubStep);
            }
        }

        // Aggregate the report for the current step.
        if (!aggregateReportForStep.getRawReports().isEmpty()) {
            checkFailPassCriteria(aggregateReportForStep);

            for (var rawReport : aggregateReportForStep.getRawReports()) {
                if (rawReport.getStartTimestampInMillis() < aggregateReportForStep.getIterationsStartTimestamp()) {
                    aggregateReportForStep.setIterationsStartTimestamp(rawReport.getStartTimestampInMillis());
                }
                if (rawReport.getEndTimestampInMillis() > aggregateReportForStep.getIterationsEndTimestamp()) {
                    aggregateReportForStep.setIterationsEndTimestamp(rawReport.getEndTimestampInMillis());
                }
                if (!rawReport.isEndedNormally()) {
                    aggregateReportForStep.setErrorsOccured(true);
                }
                aggregateReportForStep.setTotalTimeInMillis(
                        aggregateReportForStep.getTotalTimeInMillis() + rawReport.getTotalTimeInMillis());
            }

            aggregateReportForStep.setIterations(aggregateReportForStep.getRawReports().size());

            aggregateReportForStep.setAverageTimeInMillis(
                    aggregateReportForStep.getTotalTimeInMillis() / aggregateReportForStep.getIterations());

            computeHitsAtEverySecond(aggregateReportForStep);

            redactRawReports(aggregateReportForStep);
        }

        return aggregateReportForStep;
    }

    private void computeHitsAtEverySecond(AggregateActionReport aggregateReportForStep) {
        var startAndEndTimesInSec =
                aggregateReportForStep.getRawReports().stream()
                        .map(report -> Map.entry(
                                (int) ((report.getStartTimestampInMillis()
                                        - aggregateReportForStep.getIterationsStartTimestamp()) / 1000),
                                (int) ((report.getEndTimestampInMillis()
                                        - aggregateReportForStep.getIterationsStartTimestamp())/ 1000)))
                        .collect(Collectors.toUnmodifiableList());

        var hitsAtSecond = new LinkedList<Integer>();

        var numberOfSeconds = (int) ((aggregateReportForStep.getIterationsEndTimestamp()
                        - aggregateReportForStep.getIterationsStartTimestamp()) / 1000 + 1);

        var hitsAtSecondArray = new int[numberOfSeconds];

        for (var entry : startAndEndTimesInSec) {
            // For every iteration, increment the total iterations when the iteration starts
            hitsAtSecondArray[entry.getKey()] += 1;
            // For every iteration, decrement the total iterations when the iteration ends
            hitsAtSecondArray[entry.getValue()] -= 1;
        }

        // Calculate the total hits at every second by adding current value with previous value
        hitsAtSecond.add(hitsAtSecondArray[0]);
        var hitsAtPrevSecond = hitsAtSecondArray[0];
        for(var second=1; second < numberOfSeconds; second++) {
            var hitsAtCurrentSecond = hitsAtPrevSecond + hitsAtSecondArray[second];
            hitsAtSecond.add(hitsAtCurrentSecond);
            hitsAtPrevSecond = hitsAtCurrentSecond;
        }

        aggregateReportForStep.setHitsAtEverySecond(hitsAtSecond);
    }

    /**
     * Checks the fail pass criteria for the current report and sets the {@code AggregateActionReport.passed}
     * accordingly.
     *
     * @param aggregateReport The aggregate report which is to be checked for fail-pass criteria
     */
    private void checkFailPassCriteria(AggregateActionReport aggregateReport) {
        aggregateReport.setPassed(true);
        for (var criteria : parsedUserArgs.getFailPassCriteria()) {
            if (!criteria.matches(aggregateReport.getRawReports())) {
                aggregateReport.setPassed(false);
                return;
            }
        }
    }

    /**
     * Remove raw report information from the aggregate report.
     * <br>
     * If the configuration property {@code goodload.reporting.include-raw-report} is true,
     * then removes only the nested report information from raw reports for current step,
     * otherwise removes the complete raw report information.
     *
     * @param aggregateReport The aggregate report from which to remove raw report information.
     */
    private void redactRawReports(AggregateActionReport aggregateReport) {
        if (userArgs.getYamlConfiguration().getReporting().isIncludeRawReport()) {
            for (var rawReport : aggregateReport.getRawReports()) {
                rawReport.setSubSteps(null);
            }
        } else {
            aggregateReport.setRawReports(null);
        }
    }
}
