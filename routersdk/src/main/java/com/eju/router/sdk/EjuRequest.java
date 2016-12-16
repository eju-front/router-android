package com.eju.router.sdk;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EjuRequest {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_PUT = 3;
    public static final int METHOD_DELETE = 4;

    private String url;

    private int method;

    private Map<String, String> headers = new HashMap<>();

    private byte[] body;

    public EjuRequest() {
    }

    public EjuRequest(String url, @Method int method, Map<String, String> headers, byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EjuRequest{");
        sb.append("url='").append(url).append('\'');
        sb.append(", method=").append(method);
        sb.append(", headers=").append(headers);
        sb.append(", body=").append(Arrays.toString(body));
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private String url;
        private int method;
        private Map<String, String> headers;
        private byte[] body;

        public Builder() {
            method = METHOD_GET;
            headers = new HashMap<>();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(@Method int method) {
            this.method = method;
            return this;
        }

        public Builder get() {
            return method(METHOD_GET);
        }

        public Builder post() {
            return method(METHOD_POST);
        }

        public Builder post(JSONObject jsonObject) {
            body(jsonObject);
            return post();
        }

        public Builder put(JSONObject jsonObject) {
            body(jsonObject);
            return put();
        }

        public Builder put() {
            return method(METHOD_PUT);
        }

        public Builder delete() {
            return method(METHOD_DELETE);
        }

        public Builder body(JSONObject jsonObject) {
            this.body = jsonObject.toString().getBytes();
            headers.put(CONTENT_TYPE, CONTENT_TYPE_JSON);
            headers.put(CONTENT_LENGTH, "" + body.length);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder addHeaderIfNotEmpty(String name, String value) {
            if (TextUtils.isEmpty(value)) {
                return this;
            }
            headers.put(name, value);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }


        public EjuRequest build() {
            return new EjuRequest(url, method, headers, body);
        }
    }

    public String getContentType() {
        if (!headers.containsKey(EjuRequest.CONTENT_TYPE)) {
            return EjuRequest.CONTENT_TYPE_JSON;
        }
        return headers.get(EjuRequest.CONTENT_TYPE);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public @Method int getMethod() {
        return method;
    }

    public String getMethodAsString() {
        switch (method) {
            case METHOD_PUT:
                return "PUT";
            case METHOD_POST:
                return "POST";
            case METHOD_DELETE:
                return "DELETE";
            default:
                return "GET";
        }
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
