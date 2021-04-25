package org.divsgaur.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.divsgaur.goodload.dsl.*;
import org.divsgaur.goodload.exceptions.CheckFailedException;
import org.divsgaur.goodload.internal.Util;
import org.divsgaur.goodload.reporting.reports.raw.ActionReport;
import org.divsgaur.goodload.reporting.reports.raw.SimulationReport;
import org.divsgaur.goodload.userconfig.SimulationConfiguration;
import org.divsgaur.goodload.userconfig.UserArgs;

import java.util.concurrent.Callable;

import static org.divsgaur.goodload.internal.Util.currentTimestamp;

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
     * @param runnerId The unique ID by which to identify the runner.
     * @param runAfterMillis After how many milliseconds should the runner start execution.
     * @param simulationConfig The configuration of the simulation that the runner will execute.
     * @param simulationClass The class file of the simulation that the runner will execute.
     * @param holdForMillis For how many milliseconds should the simulation execute.
     *                      This is not being read directly from simulation config as
     *                      the {@link Simulator} might want to override the value if
     *                      the value exceeds the maxHoldFor value.
     * @param userArgs The options set by the user either from command line or parsed from the config file.
     */
    SimulationRunner(
            int runnerId,
            int runAfterMillis,
            SimulationConfiguration simulationConfig,
            Class<? extends Simulation>  simulationClass,
            long holdForMillis,
            UserArgs userArgs) {
        this.runAfterMillis = runAfterMillis;
        this.simulationConfig = simulationConfig;
        this.simulationClass = simulationClass;
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

            long startTimestamp = currentTimestamp();
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
                var scenarioReport = new ActionReport(currentScenario.getName());
                scenarioReport.setRunnerId(runnerIdStr);
                scenarioReport.setStartTimestampInMillis(Util.currentTimestamp());

                simulation.beforeEachScenario(currentScenario.getName());

                // Run iterations until the hold for duration is over, or user-defined number of iterations
                // have been completed.
                for (var iterationIndex = 0;
                     currentTimestamp() <= endIterationsWhenTimestamp
                             && (simulationConfig.getIterations() == null || iterationIndex < simulationConfig.getIterations());
                     iterationIndex++
                ) {
                    maintainThroughput(startTimestamp, iterationIndex);

                    simulation.beforeEachIteration(currentScenario.getName(), iterationIndex);

                    var session = new Session();
                    session.setCustomConfigurationProperties(userArgs.getConfiguration().getCustom());

                    var iterationReport = execute(session, currentScenario, runnerIdStr, iterationIndex);
                    scenarioReport.getIterations().add(iterationReport);
                    if (!iterationReport.isEndedNormally()) {
                        scenarioReport.setEndedNormally(false);
                    }

                    simulation.afterEachIteration(currentScenario.getName(), iterationIndex);
                }
                simulationReport.getScenarios().add(scenarioReport);
                if (scenarioReport.isEndedNormally()) {
                    simulationReport.setEndedNormally(false);
                }

                simulation.afterEachScenario(currentScenario.getName());
            }

            // When the last iteration completed.
            long endTimestamp = currentTimestamp();

            simulationReport.setEndTimestampInMillis(endTimestamp);

            simulation.afterSimulation();

            return simulationReport;

        } catch(InterruptedException e) {
            log.error("{} : The runner thread was interrupted. {}", tag, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("{} : Unknown exception occurred during execution: ", tag, e);
        }

        log.debug("{} : Ended", tag);

        return null;
    }

    /**
     * Execute all the steps in the given action recursively and generate report.
     * @param session The session object holding information about current iteration.
     * @param action The action whose steps are to be executed.
     * @param runnerId The id of the current runner.
     * @param iterationIndex The iteration in which this action is getting executed.
     * @return Report for the action after it has been executed. The report is generated even
     *         if the execution has failed.
     */
    private ActionReport execute(Session session, Action action, String runnerId, int iterationIndex) {
        var actionReport = new ActionReport(action.getName());
        actionReport.setRunnerId(runnerId);
        actionReport.setIterationIndex(iterationIndex);
        long actionStartTimestamp = currentTimestamp();
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

                    var nestedReport = execute(session, nestedAction, runnerId, iterationIndex);

                    if(!nestedReport.isEndedNormally()) {
                        actionReport.setEndedNormally(false);
                    }

                    actionReport.getSubSteps().add(nestedReport);
                }
            }
            catch(CheckFailedException e) {
                actionReport.setEndedNormally(false);
            } catch (Exception e) {
                log.debug("Error occurred in step {}: {}", actionReport.getStepName(), ExceptionUtils.getStackTrace(e));
                actionReport.setEndedNormally(false);
            }
        }));
        actionReport.setEndTimestampInMillis(currentTimestamp());

        return actionReport;
    }

    /**
     * Sleep the thread for some time to maintain max throughput.
     * Throughput is the number simulation iterations per second.
     * The sleep time needed is calculated using formula- <br/>
     * sleep time (Tsl) = iterationCount (C) / max throughput (M) - simulation duration (T)
     * @param startTimestamp When the simulation was started.
     * @param iterationCount How many iterations have been completed till now.
     * @throws InterruptedException If the sleeping thread is interrupted.
     */
    private void maintainThroughput(long startTimestamp, int iterationCount)
            throws InterruptedException {
        var maxThroughput = simulationConfig.getThroughput();

        if(maxThroughput == null) {
            return;
        }
        long simulationDurationInSeconds = (Util.currentTimestamp() - startTimestamp) / 1000;
        float currentThroughput = (float) iterationCount /
                simulationDurationInSeconds;
        if(currentThroughput > maxThroughput) {
            long requiredWaitTimeInSeconds = iterationCount / maxThroughput - simulationDurationInSeconds;
            if(requiredWaitTimeInSeconds * 1000 > 0) {
                Thread.sleep(requiredWaitTimeInSeconds * 1000);
            }
        }
    }
}