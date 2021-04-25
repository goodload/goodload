package org.divsgaur.goodload.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.divsgaur.goodload.dsl.Session;

/**
 * Defines DSL for the HTTP simulations.
 * Can be used to write simulation for any HTTP service/application.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Slf4j
public class HttpDSL {
    /**
     * Start a http request builder.
     * @param session The session object will be passed by the engine at runtime.
     * @return A builder to build and send the HTTP request when simulations are run.
     */
    public static HttpRequestBuilder http(Session session) {
        return new HttpRequestBuilder(session);
    }

    /**
     * Converts any object to JSON.
     * Can be used to add JSON data as request body.
     * @param object The object to convert into JSON.
     * @return The body containing the JSON representation of the object.
     */
    public static RequestBody jsonBody(Object object) {
        var mapper = new ObjectMapper();
        try {
            return RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    mapper.writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert POJO to JSON.", e);
        }
        return null;
    }
}
