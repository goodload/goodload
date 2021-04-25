package org.divsgaur.goodload.http.exceptions;

import org.divsgaur.goodload.http.HttpMethod;

/**
 * Thrown when the HTTP method provided doesn't support sending a request body.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class HttpMethodDoesNotSupportBodyException extends RuntimeException {
    public HttpMethodDoesNotSupportBodyException(String message) {
        super(message);
    }

    public static HttpMethodDoesNotSupportBodyException forMethod(HttpMethod httpMethod) {
        return new HttpMethodDoesNotSupportBodyException(
                String.format("%s request does not support request body", httpMethod));
    }
}
