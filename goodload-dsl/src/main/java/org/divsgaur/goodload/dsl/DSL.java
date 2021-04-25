package org.divsgaur.goodload.dsl;

/**
 * DSL stands for Domain-Specific Language.
 * It defines special functions that are used to create simulations.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class DSL {
    /**
     * Creates a scenario. The scenario can be named and be provided a list of steps.
     * The returned {@link Action} object must be collected in a List and returned by the {@code init}
     * method of your {@link Simulation}.
     *
     * @see Simulation
     *
     * @param name The name by which to identify the scenario.
     * @param steps The steps to execute as part of the scenario. These will be executed sequentially.
     *              You can also provide group of steps as a step.
     * @return The scenario created by the defined steps.
     * @since 1.0
     */
    public static Action scenario(String name, SequenceElement... steps) {
        return new Action(name, steps);
    }

    /**
     * Defines a basic step of a scenario. It contains the code to be executed and whose performance
     * will be measured.
     * @param name The name by which to identify the step in the report.
     * @param executable The code to execute and log performance. Override the functional interface
     *                   {@link Executable} to provide the statements to execute.
     * @return An {@link Action} object encapsulating the provided Executable and its name.
     * @since 1.0
     */
    public static Action exec(String name, Executable executable) {
        return new Action(name, executable);
    }

    /**
     * A named boolean 'check'. It can be used as a boolean evaluation step that can be used to find if the simulation is
     * passing or failing required use cases. For example, you can check if the HTTP request that
     * you made in previous step has succeeded or failed.
     *
     * @param name Name by which to identify the check in reports.
     * @param check The check to perform. Override the functional interface {@link Check} to provide
     *              your custom logic to perform the check.
     * @return An {@link Action} object encapsulating the check and its name.
     * @since 1.0
     */
    public static Action check(String name, Check check) {
        return new Action(name, check);
    }

    /**
     * An unnamed boolean 'check'. It can be used as a boolean evaluation step that can be used to find if the simulation is
     * passing or failing required use cases. For example, you can check if the HTTP request that
     * you made in previous step has succeeded or failed.
     *
     * @param check The check to perform. Override the functional interface {@link Check} to provide
     *              your custom logic to perform the check.
     * @return An {@link Action} object encapsulating the check and {@code null} as its name.
     * @since 1.0
     */
    public static Action check(Check check) {
        return check(null, check);
    }

    /**
     * You can group multiple steps as one, and also put one group into another upto any level that you need.
     * It allows you to treat multiple steps as one and get the performance metrics for whole block of steps
     * as one step. For example, your website needs 3 steps to login, but you want not only the average time
     * of the individual steps but also the overall average time required to login. You can group these three
     * steps as one and the report will be generated with aggregate for all steps separately as well as one.
     * @param name The name by which to identify the group in report.
     * @param steps The steps you want to put into the group.
     * @return An Action object with the same name and steps as the group.
     * @since 1.0
     */
    public static Action group(String name, SequenceElement... steps) {
        return new Action(name, steps);
    }
}
