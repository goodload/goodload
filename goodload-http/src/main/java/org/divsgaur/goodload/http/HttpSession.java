package org.divsgaur.goodload.http;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;

public class HttpSession {
    private final HashMap<String, HttpHeaders> httpHeaders = new HashMap<>();

    public List<String> header(String httpRequestId, String headerName) {
        if(!httpHeaders.containsKey(httpRequestId)) {
            return null;
        }
        return httpHeaders.get(httpRequestId).allValues(headerName);
    }

    public HttpHeaders headers(String httpRequestId) {
        return httpHeaders.get(httpRequestId);
    }
}
