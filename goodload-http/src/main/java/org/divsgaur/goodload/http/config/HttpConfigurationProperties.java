package org.divsgaur.goodload.http.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 */
@Data
public class HttpConfigurationProperties {
    private LoggingConfiguration logging;

    @Data
    public static class LoggingConfiguration {
        @JsonProperty(value="request-headers")
        private boolean requestHeaders = false;
        @JsonProperty(value="request-body")
        private boolean requestBody = false;
        @JsonProperty(value="response-code")
        private boolean responseCode = false;
        @JsonProperty(value="response-headers")
        private boolean responseHeaders = false;
        @JsonProperty(value="response-body")
        private boolean responseBody = false;
    }
}
