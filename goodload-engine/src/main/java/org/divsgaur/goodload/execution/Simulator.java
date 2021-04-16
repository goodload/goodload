package org.divsgaur.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.dsl.*;
import org.divsgaur.goodload.exceptions.CheckFailedException;
import org.divsgaur.goodload.exceptions.SimulatorInterruptedException;
import org.divsgaur.goodload.userconfig.SimulationConfiguration;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

    /**
     * Takes a simulation configuration and executes it.
     * Also generates the report for that simulation.
     * @param simulationConfig The simulation to execute.
     */
    public void execute(SimulationConfiguration simulationConfig) throws
            SimulatorInterruptedException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        log.info("Starting simulation `{}`", simulationConfig.getName());

        var simulationClass = Class.forName(
                simulationConfig.getFullClassName(),
                true,
                userArgs.getUserSimulationsClassLoader()).asSubclass(Simulation.class);

        // Create an instance just to verify that it can be created and the user's simulation class is not invalid.
        // It will be harder to properly report the error if this verification is left for the runner threads.
        // It will also prevent the same errors from being thrown by every runner thread because the error will be
        // detected and handled before the runners are even started.
        var simulationInstance = simulationClass.getDeclaredConstructor().newInstance();

        List<Callable<String>> runners = new ArrayList<>(simulationConfig.getConcurrency());
        for(int runnerId=0; runnerId < simulationConfig.getConcurrency(); runnerId++) {
            var runner = new SimulationRunner(runnerId, 0, simulationConfig, simulationClass);
            runners.add(runner);
        }

        try {
            userArgs.getSimulationExecutorService().invokeAll(runners);
        } catch (InterruptedException e) {
            throw new SimulatorInterruptedException(
                    String.format("The simulation `%s` was interrupted before completion.",
                            simulationConfig.getName()));
        }

        log.info("Simulation `{}` completed.", simulationConfig.getName());
    }

    private static class SimulationRunner implements Callable<String> {

        private final long runAfterMillis;

        private final SimulationConfiguration simulationConfig;

        private final Class<? extends Simulation> simulationClass;

        private final String TAG;

        SimulationRunner(int runnerId, int runAfterMillis, SimulationConfiguration simulationConfig, Class<? extends Simulation>  simulationClass) {
            this.runAfterMillis = runAfterMillis;
            this.simulationConfig = simulationConfig;
            this.simulationClass = simulationClass;

            TAG = String.format("Simulation `%s` : Runner %d:", simulationConfig.getName(), runnerId);
        }

        @Override
        public String call() {
            log.debug("{} : Started", TAG);

            try {
                Thread.sleep(runAfterMillis);

                var session = new Session();

                var simulation = simulationClass.getDeclaredConstructor().newInstance();

                List<Action> actionList = simulation.init();
                actionList.forEach(action -> execute(session, action));

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                // No handling required because the runner is only created after the parent thread has verified
                // that these exceptions won't occur.
                return null;
            } catch (InterruptedException e) {
                log.error("{} was interrupted before completion.", TAG);
                return null;
            }

            log.debug("{} : Ended", TAG);

            return null;
        }

        private void execute(Session session, Action action) {
            action.getExecutionSequence().forEach((step -> {
                if(step instanceof Check) {
                    Check check = (Check) step;
                    if(!check.condition(session)) {
                        throw new CheckFailedException(simulationConfig.getName(), action);
                    }
                } else if(step instanceof Executable) {
                    Executable executable = (Executable) step;
                    executable.function(session);
                } else if(step instanceof Action) {
                    Action nestedAction = (Action) step;
                    execute(session, nestedAction);
                }
            }));
        }
    }
}
