package org.divsgaur.goodload.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

public class HttpDSL {
    public static HttpRequestBuilder http() {
        return new HttpRequestBuilder();
    }

    public static RequestBody jsonBody(Object object) {
        var mapper = new ObjectMapper();
        try {
            return RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    mapper.writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            //log.error("Failed to convert POJO to JSON.", e);
        }
        return null;
    }
}
