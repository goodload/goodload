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
package org.goodload.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.goodload.goodload.dsl.*;
import org.goodload.goodload.exceptions.CheckFailedException;
import org.goodload.goodload.internal.Util;
import org.goodload.goodload.reporting.data.ActionReport;
import org.goodload.goodload.reporting.data.SimulationReport;
import org.goodload.goodload.userconfig.SimulationConfiguration;
import org.goodload.goodload.userconfig.UserArgs;

import java.util.concurrent.Callable;
import java.util.concurrent.SubmissionPublisher;

/**
 * Runs a simulation in a thread.
 * n instances of SimulationRunner should be run for n concurrency.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Slf4j
class SimulationRunner implements Callable<SimulationReport> {

    /**
     * After how many milliseconds the runner should start execution.
     */
    private final long runAfterMillis;

    /**
     * The configuration of the simulation that this runner will execute.
     */
    private final SimulationConfiguration simulationConfig;

    /**
     * The class file containing the simulation that this runner will execute.
     */
    private final Class<? extends Simulation> simulationClass;

    private final SubmissionPublisher<ActionReport> ActionReportSubmissionPublisher;

    /**
     * Tag to identify the runner in the logs. It has the format "Simulation `%s` : Runner %d:"
     * and contains the simulation name and an integer as ID of the runner.
     */
    private final String tag;

    /**
     * The ID of the current runner.
     */
    private final int runnerId;

    /**
     * For how many milliseconds should the simulation execute.
     * New iterations will be started until the duration is over.
     * This is not being read directly from simulation config as
     * the {@link Simulator} might want to override the value if
     * the value exceeds the maxHoldFor value.
     */
    private final long holdForMillis;

    /**
     * The options set by the user either from command line or parsed from the config file.
     */
    private final UserArgs userArgs;

    /**
     * Creates a runner to execute a simulation asynchronously.
     * One runner maintains one concurrency.
     *
     * @param runnerId         The unique ID by which to identify the runner.
     * @param runAfterMillis   After how many milliseconds should the runner start execution.
     * @param simulationConfig The configuration of the simulation that the runner will execute.
     * @param simulationClass  The class file of the simulation that the runner will execute.
     * @param holdForMillis    For how many milliseconds should the simulation execute.
     *                         This is not being read directly from simulation config as
     *                         the {@link Simulator} might want to override the value if
     *                         the value exceeds the maxHoldFor value.
     * @param userArgs         The options set by the user either from command line or parsed from the config file.
     */
    SimulationRunner(
            int runnerId,
            int runAfterMillis,
            SimulationConfiguration simulationConfig,
            Class<? extends Simulation> simulationClass,
            SubmissionPublisher<ActionReport> ActionReportSubmissionPublisher,
            long holdForMillis,
            UserArgs userArgs) {
        this.runAfterMillis = runAfterMillis;
        this.simulationConfig = simulationConfig;
        this.simulationClass = simulationClass;
        this.ActionReportSubmissionPublisher = ActionReportSubmissionPublisher;
        this.runnerId = runnerId;
        this.holdForMillis = holdForMillis;
        this.userArgs = userArgs;

        tag = String.format("Simulation `%s` : Runner %d:", simulationConfig.getName(), runnerId);
    }

    @Override
    public SimulationReport call() {
        log.debug("{} : Started", tag);

        try {
            Thread.sleep(runAfterMillis);

            final var runnerIdStr = String.valueOf(runnerId);

            long startTimestamp = Util.currentTimestamp();
            // Time after which no iterations will be started
            long endIterationsWhenTimestamp = startTimestamp + holdForMillis;

            var simulationReport = new SimulationReport(simulationConfig.getName());
            simulationReport.setRunnerId(runnerIdStr);

            var simulation = simulationClass.getDeclaredConstructor().newInstance();

            simulation.beforeSimulation();

            simulationReport.setStartTimestampInMillis(startTimestamp);

            var scenarios = simulation.init();

            // Sequentially execute all scenarios in the given simulation
            for (var currentScenario : scenarios) {
                var scenarioReport = new ActionReport(currentScenario.getId());
                scenarioReport.setRunnerId(runnerIdStr);
                scenarioReport.setStartTimestampInMillis(Util.currentTimestamp());

                simulation.beforeEachScenario(currentScenario.getName());

                // Run iterations until the hold-for duration is over, or user-defined number of iterations
                // have been completed.
                for (var iterationIndex = 0;
                     Util.currentTimestamp() <= endIterationsWhenTimestamp
                             && (simulationConfig.getIterations() == null || iterationIndex < simulationConfig.getIterations());
                     iterationIndex++
                ) {
                    maintainThroughput(startTimestamp, iterationIndex);

                    simulation.beforeEachIteration(currentScenario.getName(), iterationIndex);

                    var session = new Session();
                    session.setCustomConfigurationProperties(userArgs.getYamlConfiguration().getCustom());

                    var iterationCompletedNormally = execute(session, currentScenario, runnerIdStr, iterationIndex);
                    if (iterationCompletedNormally) {
                        scenarioReport.setEndedNormally(false);
                    }

                    simulation.afterEachIteration(currentScenario.getName(), iterationIndex);
                }
                if (scenarioReport.isEndedNormally()) {
                    simulationReport.setEndedNormally(false);
                }

                simulation.afterEachScenario(currentScenario.getName());
            }

            // When the last iteration completed.
            long endTimestamp = Util.currentTimestamp();

            simulationReport.setEndTimestampInMillis(endTimestamp);

            simulation.afterSimulation();

            return simulationReport;

        } catch (InterruptedException e) {
            log.error(String.format("%s : The runner thread was interrupted.", tag), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(String.format("%s : Unknown exception occurred during execution: ", tag), e);
        }

        log.debug("{} : Ended", tag);

        return null;
    }

    /**
     * Execute all the steps in the given action recursively and generate report.
     *
     * @param session        The session object holding information about current iteration.
     * @param action         The action whose steps are to be executed.
     * @param runnerId       The id of the current runner.
     * @param iterationIndex The iteration in which this action is getting executed.
     * @return true if the execution completed successfully, false if execution of this or any of the substeps failed.
     */
    private boolean execute(Session session, Action action, String runnerId, int iterationIndex) {
        var actionReport = new ActionReport(action.getId());
        actionReport.setIterationIndex(iterationIndex);
        actionReport.setRunnerId(runnerId);
        long actionStartTimestamp = Util.currentTimestamp();
        actionReport.setStartTimestampInMillis(actionStartTimestamp);

        action.getExecutionSequence().forEach((step -> {
            try {
                if (step instanceof Check) {
                    var check = (Check) step;
                    if (!check.condition(session)) {
                        throw new CheckFailedException(simulationConfig.getName(), action);
                    }
                } else if (step instanceof Executable) {
                    var executable = (Executable) step;
                    executable.function(session);
                } else if (step instanceof Action) {
                    var nestedAction = (Action) step;

                    var nestedActionCompletedNormally = execute(session, nestedAction, runnerId, iterationIndex);

                    if (nestedActionCompletedNormally) {
                        actionReport.setEndedNormally(false);
                    }
                }
            } catch (CheckFailedException e) {
                actionReport.setEndedNormally(false);
            } catch (Exception e) {
                log.debug("Error occurred in step {}: {}", actionReport.getStepName(), ExceptionUtils.getStackTrace(e));
                actionReport.setEndedNormally(false);
            }
        }));
        actionReport.setEndTimestampInMillis(Util.currentTimestamp());

        ActionReportSubmissionPublisher.submit(actionReport);

        return actionReport.isEndedNormally();
    }

    /**
     * Sleep the thread for some time to maintain max throughput.
     * Throughput is the number simulation iterations per second.
     * The sleep time needed is calculated using formula- <br/>
     * sleep time (Tsl) = iterationCount (C) / max throughput (M) - simulation duration (T)
     *
     * @param startTimestamp When the simulation was started.
     * @param iterationCount How many iterations have been completed till now.
     * @throws InterruptedException If the sleeping thread is interrupted.
     */
    private void maintainThroughput(long startTimestamp, int iterationCount)
            throws InterruptedException {
        var maxThroughput = simulationConfig.getThroughput();

        if (maxThroughput == null) {
            return;
        }
        long simulationDurationInSeconds = (Util.currentTimestamp() - startTimestamp) / 1000;
        float currentThroughput = (float) iterationCount /
                simulationDurationInSeconds;
        if (currentThroughput > maxThroughput) {
            long requiredWaitTimeInSeconds = iterationCount / maxThroughput - simulationDurationInSeconds;
            if (requiredWaitTimeInSeconds * 1000 > 0) {
                Thread.sleep(requiredWaitTimeInSeconds * 1000);
            }
        }
    }
}