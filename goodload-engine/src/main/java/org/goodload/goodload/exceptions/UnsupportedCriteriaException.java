package org.goodload.goodload.exceptions;

/**
 * Thrown when a fail-when criteria defined by the user is not recognized.
 * The reason could be syntax error in the criteria string or unknown criteria.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class UnsupportedCriteriaException extends Exception {
    public UnsupportedCriteriaException(String message) {
        super(message);
    }
}
