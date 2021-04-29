/*
 * Copyright (C) 2021 Goodload
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.goodload.goodload.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.goodload.goodload.dsl.Session;

/**
 * Defines DSL for the HTTP simulations.
 * Can be used to write simulation for any HTTP service/application.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
@Slf4j
public class HttpDSL {
    private HttpDSL() {
        // Hide the default constructor.
        // No implementation required.
    }

    /**
     * Start a http request builder.
     * @param session The session object will be passed by the engine at runtime.
     * @return A builder to build and send the HTTP request when simulations are run.
     * @since 1.0
     */
    public static HttpRequestBuilder http(Session session) {
        return new HttpRequestBuilder(session);
    }

    /**
     * Converts any object to JSON.
     * Can be used to add JSON data as request body.
     * @param object The object to convert into JSON.
     * @return The body containing the JSON representation of the object.
     * @since 1.0
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
