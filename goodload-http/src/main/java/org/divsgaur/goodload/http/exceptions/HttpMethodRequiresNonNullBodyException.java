package org.divsgaur.goodload.http.exceptions;

import org.divsgaur.goodload.http.HttpMethod;

/**
 * Thrown when a HTTP method requires request body to be present but none has been provided in the request.
 */
public class HttpMethodRequiresNonNullBodyException extends RuntimeException {
    public HttpMethodRequiresNonNullBodyException(String message) {
        super(message);
    }

    public static HttpMethodRequiresNonNullBodyException forMethod(HttpMethod httpMethod) {
        return new HttpMethodRequiresNonNullBodyException(
                String.format("%s request requires a non-null request body", httpMethod));
    }
}
