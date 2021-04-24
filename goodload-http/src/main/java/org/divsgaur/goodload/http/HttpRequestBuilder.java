package org.divsgaur.goodload.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.core.exceptions.ExecutionFailedException;
import org.divsgaur.goodload.dsl.Session;
import org.divsgaur.goodload.http.exceptions.HttpMethodDoesNotSupportBodyException;
import org.divsgaur.goodload.http.exceptions.HttpMethodRequiresNonNullBodyException;

import java.io.IOException;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class HttpRequestBuilder {
    private final Request.Builder httpRequest = new Request.Builder();

    private final OkHttpClient okHttpClient = new OkHttpClient();

    private RequestBody requestBody;

    private HttpMethod httpMethod;

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
    public void go() throws HttpMethodDoesNotSupportBodyException, HttpMethodRequiresNonNullBodyException {
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
            case GET:
                httpRequest.get();
            case PATCH:
                httpRequest.patch(requestBody);
            case PUT:
                httpRequest.put(requestBody);
            case POST:
                httpRequest.post(requestBody);
        }

        // TODO: Add logging here
        // TODO: Decide what happens when the request fails.
        //      Should we report the actual time or 0 to signify error occurred?

        try {
            var response = okHttpClient.newCall(httpRequest.build()).execute();
            //log.debug("HTTP Module: Response Code {}", response.code());
            //log.debug("HTTP Module: Response Headers {}", response.headers().toString());
            //log.debug("HTTP Module: Response Body {}", response.body().string());
        } catch (IOException e) {
            throw new ExecutionFailedException(e);
        }
    }

    private void setUrl(String url) {
        httpRequest.url(url);
    }
}
