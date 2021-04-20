package org.divsgaur.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.dsl.*;
import org.divsgaur.goodload.exceptions.CheckFailedException;
import org.divsgaur.goodload.exceptions.SimulatorInterruptedException;
import org.divsgaur.goodload.reporting.AggregateReport;
import org.divsgaur.goodload.reporting.Report;
import org.divsgaur.goodload.reporting.ReportAggregator;
import org.divsgaur.goodload.userconfig.SimulationConfiguration;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.divsgaur.goodload.execution.Util.currentTimestamp;

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

        long simulationStartTime = currentTimestamp();

        List<Callable<Report>> runners = new ArrayList<>(simulationConfig.getConcurrency());
        for(int runnerId=0; runnerId < simulationConfig.getConcurrency(); runnerId++) {
            var runner = new SimulationRunner(runnerId, 0, simulationConfig, simulationClass);
            runners.add(runner);
        }

        try {
            var futures = userArgs.getSimulationExecutorService().invokeAll(runners);
            for(var future: futures) {
                simulationReport.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
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

        SimulationRunner(int runnerId, int runAfterMillis, SimulationConfiguration simulationConfig, Class<? extends Simulation>  simulationClass) {
            this.runAfterMillis = runAfterMillis;
            this.simulationConfig = simulationConfig;
            this.simulationClass = simulationClass;
            this.runnerId = runnerId;

            TAG = String.format("Simulation `%s` : Runner %d:", simulationConfig.getName(), runnerId);
        }

        @Override
        public Report call() {
            log.debug("{} : Started", TAG);

            try {
                Thread.sleep(runAfterMillis);

                var session = new Session();

                var simulation = simulationClass.getDeclaredConstructor().newInstance();

                var actionList = simulation.init();

                var report = new Report();
                report.setStepName(simulationConfig.getName());
                report.setRunnerId(String.valueOf(runnerId));

                long startTimestamp = currentTimestamp();
                actionList.forEach(action -> {
                    var actionReport = execute(session, action);
                    if(!actionReport.isEndedNormally()) {
                        report.setEndedNormally(false);
                    }
                });
                long endTimestamp = currentTimestamp();

                report.setTotalTimeInMillis(endTimestamp - startTimestamp);

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                // No handling required because the runner is only created after the parent thread has verified
                // that these exceptions won't occur.
                return null;
            } catch (Exception e) {
                log.error("{} : Unknown exception occurred during execution: ", TAG, e);
            }

            log.debug("{} : Ended", TAG);

            return null;
        }

        private Report execute(Session session, Action action) {
            Report actionReport = new Report();
            actionReport.setStepName(action.getName());
            long actionStartTimestamp = currentTimestamp();
            try {
                action.getExecutionSequence().forEach((step -> {
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

                        Report nestedReport = execute(session, nestedAction);

                        actionReport.getSubSteps().add(nestedReport);
                    }
                }));
            } catch (Exception e) {
                actionReport.setEndedNormally(false);
            }
            long actionEndTimestamp = currentTimestamp();
            actionReport.setTotalTimeInMillis(actionEndTimestamp - actionStartTimestamp);

            return actionReport;
        }
    }
}
