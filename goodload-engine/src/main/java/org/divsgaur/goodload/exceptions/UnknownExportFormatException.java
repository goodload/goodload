package org.divsgaur.goodload.exceptions;

/**
 * Thrown when any of the values goodload.reporting.export-formats is not recognized.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class UnknownExportFormatException extends Exception {
    public UnknownExportFormatException(String message) {
        super(message);
    }
}
