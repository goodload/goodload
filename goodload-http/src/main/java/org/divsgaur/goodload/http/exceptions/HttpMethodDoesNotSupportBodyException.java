package org.divsgaur.goodload.http.exceptions;

import org.divsgaur.goodload.http.HttpMethod;

public class HttpMethodDoesNotSupportBodyException extends RuntimeException {
    public HttpMethodDoesNotSupportBodyException(String message) {
        super(message);
    }

    public static HttpMethodDoesNotSupportBodyException forMethod(HttpMethod httpMethod) {
        return new HttpMethodDoesNotSupportBodyException(
                String.format("%s request does not support request body", httpMethod));
    }
}
