package org.divsgaur.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.divsgaur.goodload.dsl.*;
import org.divsgaur.goodload.exceptions.CheckFailedException;
import org.divsgaur.goodload.reporting.Report;
import org.divsgaur.goodload.userconfig.SimulationConfiguration;

import java.util.concurrent.Callable;

import static org.divsgaur.goodload.internal.Util.currentTimestamp;

/**
 * Runs a simulation in a thread.
 * n instances of SimulationRunner should be run for n concurrency.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Slf4j
class SimulationRunner implements Callable<Report> {

        private final long runAfterMillis;

        private final SimulationConfiguration simulationConfig;

        private final Class<? extends Simulation> simulationClass;

        private final String TAG;

        private final int runnerId;

        private final long holdForMillis;

        SimulationRunner(
                int runnerId,
                int runAfterMillis,
                SimulationConfiguration simulationConfig,
                Class<? extends Simulation>  simulationClass,
                long holdForMillis) {
            this.runAfterMillis = runAfterMillis;
            this.simulationConfig = simulationConfig;
            this.simulationClass = simulationClass;
            this.runnerId = runnerId;
            this.holdForMillis = holdForMillis;

            TAG = String.format("Simulation `%s` : Runner %d:", simulationConfig.getName(), runnerId);
        }

        @Override
        public Report call() {
            log.debug("{} : Started", TAG);

            try {
                Thread.sleep(runAfterMillis);

                final var runnerIdStr = String.valueOf(runnerId);

                long startTimestamp = currentTimestamp();
                // When the last iteration should be initiated
                long endIterationsWhenTimestamp = startTimestamp + holdForMillis;

                var report = new Report();
                report.setStepName(simulationConfig.getName());
                report.setRunnerId(runnerIdStr);
                report.setStartTimestampInMillis(startTimestamp);

                for(int iterationIndex = 0;
                    currentTimestamp() <= endIterationsWhenTimestamp
                        && iterationIndex < simulationConfig.getIterations();
                    iterationIndex++) {

                    var simulation = simulationClass.getDeclaredConstructor().newInstance();

                    var actionList = simulation.init();

                    var session = new Session();
                    final int finalIterationIndex = iterationIndex;
                    actionList.forEach(action -> {
                        var actionReport = execute(session, action, runnerIdStr, finalIterationIndex);
                        report.getIterations().add(actionReport);
                        if (!actionReport.isEndedNormally()) {
                            report.setEndedNormally(false);
                        }
                    });
                }

                // When the last iteration completed.
                long endTimestamp = currentTimestamp();

                report.setEndTimestampInMillis(endTimestamp);

                return report;

            } catch (Exception e) {
                log.error("{} : Unknown exception occurred during execution: ", TAG, e);
            }

            log.debug("{} : Ended", TAG);

            return null;
        }

        private Report execute(Session session, Action action, String runnerId, int iterationIndex) {
            Report actionReport = new Report();
            actionReport.setStepName(action.getName());
            actionReport.setRunnerId(runnerId);
            actionReport.setIterationIndex(iterationIndex);
            long actionStartTimestamp = currentTimestamp();
            actionReport.setStartTimestampInMillis(actionStartTimestamp);

            try {
                action.getExecutionSequence().forEach((step -> {
                    try {
                        if (step instanceof Check) {
                            Check check = (Check) step;
                            if (!check.condition(session)) {
                                throw new CheckFailedException(simulationConfig.getName(), action);
                            }
                        } else if (step instanceof Executable) {
                            Executable executable = (Executable) step;
                            executable.function(session);
                        } else if (step instanceof Action) {
                            Action nestedAction = (Action) step;

                            var nestedReport = execute(session, nestedAction, runnerId, iterationIndex);

                            if(!nestedReport.isEndedNormally()) {
                                actionReport.setEndedNormally(false);
                            }

                            actionReport.getSubSteps().add(nestedReport);
                        }
                    } catch (Exception e) {
                        log.debug("Error occurred in step {}: {}", actionReport.getStepName(), ExceptionUtils.getStackTrace(e));
                        actionReport.setEndedNormally(false);
                    }
                }));
            } catch (Exception e) {
                log.debug("Error occurred in step {}: {}", actionReport.getStepName(), ExceptionUtils.getStackTrace(e));
                actionReport.setEndedNormally(false);
            }
            actionReport.setEndTimestampInMillis(currentTimestamp());

            return actionReport;
        }
    }