package org.divsgaur.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.divsgaur.goodload.config.GoodloadConfigurationProperties;
import org.divsgaur.goodload.dsl.*;
import org.divsgaur.goodload.exceptions.CheckFailedException;
import org.divsgaur.goodload.exceptions.SimulatorInterruptedException;
import org.divsgaur.goodload.internal.Util;
import org.divsgaur.goodload.reporting.AggregateReport;
import org.divsgaur.goodload.reporting.Report;
import org.divsgaur.goodload.reporting.ReportAggregator;
import org.divsgaur.goodload.userconfig.SimulationConfiguration;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.divsgaur.goodload.internal.Util.currentTimestamp;

/**
 * As the name suggests,
 * it is responsible for running a simulation and generating report for that simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Slf4j
@Component
public class Simulator {

    @Resource
    private UserArgs userArgs;

    @Resource
    private ReportAggregator reportAggregator;

    @Resource
    private GoodloadConfigurationProperties goodloadConfigurationProperties;

    /**
     * Takes a simulation configuration and executes it.
     * Also generates the report for that simulation.
     * @param simulationConfig The simulation to execute.
     */
    public AggregateReport execute(SimulationConfiguration simulationConfig) throws
            SimulatorInterruptedException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        if(!simulationConfig.isEnabled()) {
            log.info("Simulation `{}` ignored as it is disabled.", simulationConfig.getName());
            return null;
        }

        log.info("Starting simulation `{}`", simulationConfig.getName());

        var simulationClass = Class.forName(
                simulationConfig.getFullClassName(),
                true,
                userArgs.getUserSimulationsClassLoader()).asSubclass(Simulation.class);

        // DO NOT REMOVE UNUSED VARIABLE simulationInstance
        // We are creating an instance just to verify that it can be created and the user's simulation class is not invalid.
        // It will be harder to properly report the error if this verification is left for the runner threads.
        // It will also prevent the same errors from being thrown by every runner thread because the error will be
        // detected and handled before the runners are even started.
        var simulationInstance = simulationClass.getDeclaredConstructor().newInstance();

        var simulationReport = new ArrayList<Report>();

        long maxHoldFor = Util.parseDurationToMillis(goodloadConfigurationProperties.getMaxHoldFor());
        long simulationHoldFor = Util.parseDurationToMillis(simulationConfig.getHoldFor());

        if(maxHoldFor < simulationHoldFor) {
            log.warn("The hold-for duration {} is greater than max allowed value of {}, " +
                    "hence the simulation will be run only for {} duration.",
                    simulationConfig.getHoldFor(),
                    goodloadConfigurationProperties.getMaxHoldFor(),
                    goodloadConfigurationProperties.getMaxHoldFor());
        }

        long holdForMillis = Math.min(maxHoldFor, simulationHoldFor);

        // Forcibly terminate the runner threads if some long running step in simulation is causing
        // it to run for more than 120% of the hold for value.
        // This prevents Denial of Service attacks due to infinite recursions or loops in simulation.
        long forceEndAfterDuration = (long) (1.2 * maxHoldFor);

        var runners = new ArrayList<Callable<Report>>(simulationConfig.getConcurrency());
        for(int runnerId=0; runnerId < simulationConfig.getConcurrency(); runnerId++) {
            var runner = new SimulationRunner(runnerId, 0, simulationConfig, simulationClass, holdForMillis);
            runners.add(runner);
        }

        long simulationStartTime = currentTimestamp();
        try {
            var futures = userArgs.getSimulationExecutorService().invokeAll(
                    runners,
                    forceEndAfterDuration,
                    TimeUnit.MILLISECONDS);
            for(var future: futures) {
                simulationReport.add(future.get());
            }
        } catch(CancellationException e) {
            throw new SimulatorInterruptedException(
                    String.format(
                            "The simulation %s was cancelled forcefully because it exceeded max duration " +
                                    "(including %d grace period) of %d milliseconds.",
                            simulationConfig.getName(),
                            20,
                            forceEndAfterDuration),
                    e);
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SimulatorInterruptedException(
                    String.format("The simulation `%s` was interrupted before completion.",
                            simulationConfig.getName()), e);
        }

        long simulationEndTime = currentTimestamp();

        log.info("Simulation `{}` completed.", simulationConfig.getName());
        log.info("Simulation `{}`: Generating aggregate report...", simulationConfig.getName());
        return reportAggregator.aggregate(
                simulationConfig.getName(),
                simulationReport,
                simulationEndTime - simulationStartTime);
    }

    private static class SimulationRunner implements Callable<Report> {

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

                long startTimestamp = Util.currentTimestamp();
                // When the last iteration should be initiated
                long endIterationsWhenTimestamp = startTimestamp + holdForMillis;

                var report = new Report();
                report.setStepName(simulationConfig.getName());
                report.setRunnerId(runnerIdStr);
                report.setStartTimestampInMillis(startTimestamp);

                for(int iterationIndex = 0; Util.currentTimestamp() <= endIterationsWhenTimestamp; iterationIndex++) {

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
            actionReport.setEndTimestampInMillis(Util.currentTimestamp());

            return actionReport;
        }
    }
}
