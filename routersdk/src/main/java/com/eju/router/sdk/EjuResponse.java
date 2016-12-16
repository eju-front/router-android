package com.eju.router.sdk;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/*package*/ class EjuResponse {

    private Map<String, String> headers = new HashMap<>();

    private byte[] body;

    private int statusCode;

    EjuResponse(int statusCode, Map<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    boolean isSuccessful() {
        return (statusCode >= 200 && statusCode < 300) || statusCode == 304;
    }

    Map<String, String> getHeaders() {
        return headers;
    }

    void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    String getContentType() {
        if (headers.containsKey(EjuRequest.CONTENT_TYPE)) {
            return headers.get(EjuRequest.CONTENT_TYPE);
        }
        return null;
    }

    byte[] getBody() {
        return body;
    }

    String getBodyAsString() {
        if (null == body) return null;
        try {
            return new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(body);
        }
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
