package com.vwo.event;

import java.util.Map;

public class DispatchEvent {

    private final String host;
    private final String path;
    private final Map<String, Object> requestParams;
    private final RequestMethod requestMethod;
    private final String body;

    public DispatchEvent(String host, String path, Map<String, Object> requestParams, RequestMethod requestMethod, String body) {
        this.host = host;
        this.path = path;
        this.requestParams = requestParams;
        this.requestMethod = requestMethod;
        this.body = body;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getBody() {
        return body;
    }

    public enum RequestMethod {
        GET,
        POST
    }


}
