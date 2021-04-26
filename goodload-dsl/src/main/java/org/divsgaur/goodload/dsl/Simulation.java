/*
Copyright (C) 2021 Goodload

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.divsgaur.goodload.dsl;

import java.util.List;

/**
 * User can create multiple simulations to measure performance of their product.
 * Each simulation is defined as a class implementing {@link Simulation}.
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
public interface Simulation {
    /**
     * Override this method to define your simulation and provide a list of scenarios to be
     * executed as part of the simulation.
     * @return The list of scenarios to be executed when the performance tests are run.
     * @since 1.0
     */
    List<Action> init();

    /**
     * This will run when the simulation is initialized.
     * It is run only once.
     * Override this in your simulation to run some prerequisite steps.
     */
    default void beforeSimulation() {}

    /**
     * This will run after the simulation has ended.
     * It is run only once.
     * Override this in your simulation to run some cleanup or post-process steps.
     */
    default void afterSimulation() {}

    /**
     * This will run before the first iteration of each scenario.
     * @param scenarioName The name of the scenario which will be run after this.
     */
    default void beforeEachScenario(String scenarioName) {}

    /**
     * This will run after the last iteration of each scenario.
     * @param scenarioName The name of the scenario which has just completed.
     */
    default void afterEachScenario(String scenarioName) {}

    /**
     * This will run before each iteration.
     * @param scenarioName The name of the scenario whose iteration is going to start.
     * @param iterationIndex The index of the iteration which is going to run for
     *                       the given scenario. Starts from 0.
     */
    default void beforeEachIteration(String scenarioName, int iterationIndex) {}

    /**
     * This will run after each iteration.
     * @param scenarioName The name of the scenario whose iteration has completed.
     * @param iterationIndex The index of the iteration which has completed for
     *                       the given scenario. Starts from 0.
     */
    default void afterEachIteration(String scenarioName, int iterationIndex) {}
}
