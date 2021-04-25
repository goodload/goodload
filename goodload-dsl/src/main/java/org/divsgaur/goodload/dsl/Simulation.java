package org.divsgaur.goodload.dsl;

import java.util.List;

/**
 * User can create multiple simulations to measure performance of their product.
 * Each simulation is defined as a subclass of {@link Simulation}.
 * The user defines a list of scenarios by overriding the {@code Simulation.init()} method,
 * and then returns the list from the function.
 * <br>
 * The simulation engine will pickup the list of scenarios returned by the init() method and
 * execute them in sequence.
 * <br>
 * Note that the simulation, and the scenarios defined within them run sequentially and not
 * concurrently. We believe this is more user-friendly and helpful than having the simulations
 * and scenarios run concurrently as it allows you to define all the simulations at once and they
 * won't falsely overload your software and negatively affect the performance metrics.
 *
 * @since 1.0
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public abstract class Simulation {
    /**
     * Override this method to define your simulation and provide a list of scenarios to be
     * executed as part of the simulation.
     * @return The list of scenarios to be executed when the performance tests are run.
     * @since 1.0
     */
    public abstract List<Action> init();

    /**
     * This will run when the simulation is initialized.
     * It is run only once.
     * Override this in your simulation to run some prerequisite steps.
     */
    public void beforeSimulation() {}

    /**
     * This will run after the simulation has ended.
     * It is run only once.
     * Override this in your simulation to run some cleanup or post-process steps.
     */
    public void afterSimulation() {}

    /**
     * This will run before the first iteration of each scenario.
     */
    public void beforeEachScenario() {}

    /**
     * This will run after the last iteration of each scenario.
     */
    public void afterEachScenario() {}

    /**
     * This will run before each iteration.
     */
    public void beforeEachIteration() {}

    /**
     * This will run after each iteration.
     */
    public void afterEachIteration() {}
}
