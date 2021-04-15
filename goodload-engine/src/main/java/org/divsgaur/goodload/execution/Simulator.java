package org.divsgaur.goodload.execution;

import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.dsl.Simulation;
import org.divsgaur.goodload.exceptions.SimulatorInterruptedException;
import org.divsgaur.goodload.userconfig.ExecutionConfig;
import org.divsgaur.goodload.userconfig.UserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

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
    public void execute(ExecutionConfig simulationConfig) throws
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

        Thread[] runners = new Thread[simulationConfig.getConcurrency()];
        for(int runnerId=0; runnerId < simulationConfig.getConcurrency(); runnerId++) {
            runners[runnerId] = new Thread(new SimulationRunner(runnerId, simulationConfig, simulationClass));
            userArgs.getSimulationExecutorService().execute(runners[runnerId]);
        }

        try {
            for (var runner : runners) {
                synchronized (runner) {
                    runner.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new SimulatorInterruptedException(
                    String.format("The simulation `%s` was interrupted before completion.",
                            simulationConfig.getName()));
        }

        log.info("Simulation `{}` completed.", simulationConfig.getName());
    }

    private static class SimulationRunner implements Runnable {

        private final ExecutionConfig simulationConfig;

        private final Class<? extends Simulation> simulationClass;

        private final String TAG;

        SimulationRunner(int runnerId, ExecutionConfig simulationConfig, Class<? extends Simulation>  simulationClass) {
            this.simulationConfig = simulationConfig;
            this.simulationClass = simulationClass;

            TAG = String.format("Simulation `%s` : Runner %d:", simulationConfig.getName(), runnerId);
        }

        public void run() {
            log.debug("{} : Started", TAG);

            try {
                var simulation = simulationClass.getDeclaredConstructor().newInstance();
                simulation.execute();

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                // No handling required because the runner is only created after the parent thread has verified
                // that these exceptions won't occur.
            }

            log.debug("{} : Ended", TAG);
        }
    }
}
