package org.divsgaur.goodload.exceptions;

import org.divsgaur.goodload.dsl.Action;

/**
 * Thrown when a check step in a scenario fails.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class CheckFailedException extends RuntimeException {

    /**
     * Creates an instane of CheckFailedException.
     * @param simulationName The name of the simulation that failed.
     * @param action The name of the check step that failed.
     *               Note that a check is encapsulated by a single step Action object
     *               which provides the check a name.
     */
    public CheckFailedException(String simulationName, Action action) {
        super(String.format("Check failed for simulation `%s` action `%s`",
                simulationName,
                action.getName()));
    }
}
