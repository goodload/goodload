package org.divsgaur.goodload.core.exceptions;

/**
 * Thrown when execution of an executable failed due to some error.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 *
 * @since 1.0
 */
public class ExecutionFailedException extends RuntimeException {
    /**
     * @since 1.0
     */
    public ExecutionFailedException() {
    }

    /**
     * @since 1.0
     */
    public ExecutionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionFailedException(Throwable cause) {
        super(cause);
    }
}
