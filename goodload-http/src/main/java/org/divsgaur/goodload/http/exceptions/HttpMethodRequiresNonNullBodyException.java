package org.divsgaur.goodload.http.exceptions;

import org.divsgaur.goodload.http.HttpMethod;

public class HttpMethodRequiresNonNullBodyException extends RuntimeException {
    public HttpMethodRequiresNonNullBodyException(String message) {
        super(message);
    }

    public static HttpMethodRequiresNonNullBodyException forMethod(HttpMethod httpMethod) {
        return new HttpMethodRequiresNonNullBodyException(
                String.format("%s request requires a non-null request body", httpMethod));
    }
}
