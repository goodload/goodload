package org.divsgaur.goodload.dsl;

/**
 * Functional interface that can be overridden to evaluate a condition and return a boolean value.
 * If the boolean value returned is false, the check is deemed to be failed and will be treated as
 * fail in simulation.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 *
 * @since 1.0
 */
public interface Check extends SequenceElement {
    /**
     * Implement this method to provide the logic for checking a condition.
     * @param session The simulation engine will provide a Session object containing information about the current
     *                iteration and information stored by any other steps that have been executed previously in the
     *                current iteration.
     * @return {@code true} if the conditions have passed, false if the conditions have failed. If any of the checks
     *                      return false, the group of steps and the simulation therefore, is treated as failed.
     *
     * @since 1.0
     */
    boolean condition(Session session);
}
