/*
Copyright (C) 2021 Goodload

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.divsgaur.goodload.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.core.exceptions.ExecutionFailedException;
import org.divsgaur.goodload.dsl.Session;
import org.divsgaur.goodload.http.config.HttpConfigurationProperties;
import org.divsgaur.goodload.http.exceptions.HttpMethodDoesNotSupportBodyException;
import org.divsgaur.goodload.http.exceptions.HttpMethodRequiresNonNullBodyException;

import java.io.IOException;
import java.util.function.Function;

/**
 * Provides DSL for building a HTTP request.
 * Each method in the builder acts as a DSL element to easily create requests.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@RequiredArgsConstructor
@Slf4j
public class HttpRequestBuilder {
    private final Request.Builder httpRequest = new Request.Builder();

    private final OkHttpClient okHttpClient = new OkHttpClient();

    private RequestBody requestBody;

    private HttpMethod httpMethod;

    private HttpConfigurationProperties httpConfigurationProperties;

    @NonNull
    private Session session;

    /**
     * Configures the current request as HTTP DELETE request.
     * @param url The URL to reach
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder delete(String url) {
        setUrl(url);
        httpMethod = HttpMethod.DELETE;
        return this;
    }

    /**
     * Configures the current request as HTTP HEAD request.
     * @param url The URL to reach
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder head(String url) {
        setUrl(url);
        httpMethod = HttpMethod.HEAD;
        return this;
    }

    /**
     * Configures the current request as HTTP GET request.
     * @param url The URL to reach
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder get(String url) {
        setUrl(url);
        httpMethod = HttpMethod.GET;
        return this;
    }

    /**
     * Configures the current request as HTTP PATCH request.
     * @param url The URL to reach
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder patch(String url) {
        setUrl(url);
        httpMethod = HttpMethod.PATCH;
        return this;
    }

    /**
     * Configures the current request as HTTP POST request.
     * @param url The URL to reach
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder post(String url) {
        setUrl(url);
        httpMethod = HttpMethod.POST;
        return this;
    }

    /**
     * Configures the current request as HTTP PUT request.
     * @param url The URL to reach
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder put(String url) {
        setUrl(url);
        httpMethod = HttpMethod.PUT;
        return this;
    }

    /**
     * Adds the header to the request.
     * If you want to provide multiple values for the header, call this method multiple times.
     * @param name The name of the header
     * @param value The value of the header
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder header(String name, String value) {
        httpRequest.addHeader(name, value);
        return this;
    }

    /**
     * Add a body to the request.
     * @param requestBody The body to add to the request
     * @return The current builder for chaining calls.
     */
    public HttpRequestBuilder body(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    /**
     * Access and/or modify the HTTP request directly.
     * @param function The user code that accesses the http request
     * @return The current builder for chaining.
     */
    public HttpRequestBuilder customRequest(Function<Request.Builder, Void> function) {
        function.apply(httpRequest);
        return this;
    }

    /**
     * Finalize the current http request.
     * @throws HttpMethodDoesNotSupportBodyException If the HTTP method does not allow sending a request body.
     * @throws HttpMethodRequiresNonNullBodyException if the HTTP method requires request body to be provided
*                                                     but the provided request body is null.
     */
    public void go()
            throws HttpMethodDoesNotSupportBodyException,
            HttpMethodRequiresNonNullBodyException {

        readHttpConfigurationProperties(session);

        if(requestBody != null && !com.squareup.okhttp.internal.http.HttpMethod.permitsRequestBody(httpMethod.name())) {
            throw HttpMethodDoesNotSupportBodyException.forMethod(httpMethod);
        } else if(requestBody == null && com.squareup.okhttp.internal.http.HttpMethod.requiresRequestBody(httpMethod.name())) {
            throw HttpMethodRequiresNonNullBodyException.forMethod(httpMethod);
        }

        switch(httpMethod) {
            case DELETE:
                if (requestBody != null) {
                    httpRequest.delete(requestBody);
                } else {
                    httpRequest.delete();
                }
                break;
            case HEAD:
                httpRequest.head();
                break;
            case GET:
                httpRequest.get();
                break;
            case PATCH:
                httpRequest.patch(requestBody);
                break;
            case PUT:
                httpRequest.put(requestBody);
                break;
            case POST:
                httpRequest.post(requestBody);
                break;
            default:
                throw new UnsupportedOperationException("The http method " + httpMethod + "is not yet supported.");
        }

        try {
            var request = httpRequest.build();
            var response = okHttpClient.newCall(request).execute();

            if(httpConfigurationProperties.getLogging().isRequestHeaders()) {
                log.debug("HTTP: Request headers {}", request.headers().toString());
            }
            if(httpConfigurationProperties.getLogging().isRequestBody()) {
                log.debug("HTTP: Request body {}", request.body().toString());
            }
            if(httpConfigurationProperties.getLogging().isResponseCode()) {
                log.debug("HTTP: Response code {}", response.code());
            }
            if(httpConfigurationProperties.getLogging().isResponseHeaders()) {
                log.debug("HTTP: Response headers {}", response.headers().toString());
            }
            if(httpConfigurationProperties.getLogging().isResponseBody()) {
                log.debug("HTTP: Response body {}", response.body().string());
            }
        } catch (IOException e) {
            throw new ExecutionFailedException(e);
        }
    }

    /**
     * Sets URL to the request.
     * @param url The URL to which the request will be sent.
     */
    private void setUrl(String url) {
        httpRequest.url(url);
    }

    /**
     * Reads the configuration properties defined with prefix {@code goodload.custom.http}
     * as a POJO. Expects the engine to provide these values in the session object as a LinkedHashMap.
     * @param session This contains the information about current iteration.
     *                It is provided by the engine when running the simulation.
     *                The method expects the engine to put the user-provided
     *                goodload.custom to be provided in the session.
     */
    private void readHttpConfigurationProperties(Session session) {
        if(session.getCustomConfigurationProperties() == null
                || session.getCustomConfigurationProperties().get("http") == null) {
            return;
        }

        var objectMapper = new ObjectMapper();
        httpConfigurationProperties = objectMapper
                .convertValue(
                        session.getCustomConfigurationProperties().get("http"),
                        HttpConfigurationProperties.class);
        if(httpConfigurationProperties == null) {
            log.error("The configuration for http module goodload.custom.http is invalid.");
        }
    }
}
