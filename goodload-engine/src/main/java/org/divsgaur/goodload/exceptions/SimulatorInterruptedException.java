package org.divsgaur.goodload.exceptions;

/**
 * Thrown when an simulator is interrupted before the child runner threads could complete execution.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class SimulatorInterruptedException extends InterruptedException {
    public SimulatorInterruptedException(String message) {
        super(message);
    }
}
