package org.divsgaur.goodload.exceptions;

import java.io.IOException;

/**
 * Thrown when the simulation jar file couldn't be read/found.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
public class JarFileNotFoundException extends IOException {
    public JarFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public JarFileNotFoundException(String message) {
        super(message);
    }
}
