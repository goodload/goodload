package org.divsgaur.goodload.exceptions;

import org.divsgaur.goodload.dsl.Action;

public class CheckFailedException extends RuntimeException {

    public CheckFailedException(String simulationName, Action action) {
        super(String.format("Check failed for simulation `%s` action `%s`",
                simulationName,
                action.getName()));
    }
}
