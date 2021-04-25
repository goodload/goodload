package org.divsgaur.goodload.exceptions;

import java.io.IOException;

/**
 * Thrown when the simulation config YAML file couldn't be read/found or the contents were invalid.
 * e.g. missing file, permissions issue, syntax error, missing required fields, etc.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class InvalidSimulationConfigFileException extends IOException {
    public InvalidSimulationConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
