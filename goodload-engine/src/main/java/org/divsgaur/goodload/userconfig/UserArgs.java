package org.divsgaur.goodload.userconfig;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;

/**
 * Defines the arguments and configuration/settings passed by the user
 * when launching the goodload tool/engine/application.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Component
@Data
public class UserArgs {
    /**
     * Path to the configuration yaml file which defines the simulation options and configuration
     */
    private String configFilePath;
    /**
     * The jar file which contains the simulation code.
     */
    private String jarFilePath;
    /**
     * The list of names of simulations to execute.
     * If null or empty, then execute all simulations declared in the config file.
     */
    private String[] simulationsToExecute;

    /**
     * The configuration properties deserialized from configuration yaml file.
     */
    private SimulationConfig configuration;

    /**
     * The loader used to load classes from user's simulation jar file.
     */
    private URLClassLoader userSimulationsClassLoader;

    /**
     * Thread pool to be used for execution of simulations.
     */
    private ExecutorService simulationExecutorService;
}
