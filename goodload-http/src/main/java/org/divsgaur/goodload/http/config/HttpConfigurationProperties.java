package org.divsgaur.goodload.http.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Properties that affect behaviour of HTTP module.
 * These properties are defined in simulation configuration file.
 * Prefix is {@code goodload.custom.http}
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Data
public class HttpConfigurationProperties {
    /**
     * Configurations that affect how the logging is performed by HTTP module.
     */
    private LoggingConfiguration logging;

    @Data
    public static class LoggingConfiguration {
        /**
         * If true, all the request headers will be logged.
         */
        @JsonProperty(value="request-headers")
        private boolean requestHeaders = false;

        /**
         * If true, body of all the requests will be logged.
         */
        @JsonProperty(value="request-body")
        private boolean requestBody = false;

        /**
         * If true, response code returned by the server will be returned.
         */
        @JsonProperty(value="response-code")
        private boolean responseCode = false;

        /**
         * If true, all the headers in the response will be logged.
         */
        @JsonProperty(value="response-headers")
        private boolean responseHeaders = false;

        /**
         * If true, the body of all the responses will be logged.
         */
        @JsonProperty(value="response-body")
        private boolean responseBody = false;
    }
}
