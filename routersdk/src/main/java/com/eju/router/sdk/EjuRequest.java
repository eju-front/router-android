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

    /**
     * init {@link EjuRequest}
     * @param url the url request
     * @param method the request type,you can get from{@link Method} ,such as {@link EjuRequest#METHOD_GET} or {@link EjuRequest#METHOD_POST}
     * @param headers the request headers
     * @param body the request body
     */
    public EjuRequest(String url, @Method int method, Map<String, String> headers, byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    /**
     * get {@link Builder} to create the request.
     * @return the {@link Builder}
     */
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

    /**
     * You can use this Builder to build your request.
     */
    public static class Builder {
        private String url;
        private int method;
        private Map<String, String> headers;
        private byte[] body;

        public Builder() {
            method = METHOD_GET;
            headers = new HashMap<>();
        }

        /**
         * init {@link EjuRequest} url
         * @param url the request url
         * @return Builder
         *
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * init {@link EjuRequest} request method
         * @param method {@link EjuRequest#METHOD_GET} or {@link EjuRequest#METHOD_POST} {@link EjuRequest#METHOD_DELETE} {@link EjuRequest#METHOD_PUT}
         * @return Builder
         */
        public Builder method(@Method int method) {
            this.method = method;
            return this;
        }

        /**
         * init {@link EjuRequest} request method with {@link EjuRequest#METHOD_GET}
         * @return Builder
         */
        public Builder get() {
            return method(METHOD_GET);
        }

        /**
         * init {@link EjuRequest} request method with {@link EjuRequest#METHOD_POST}
         * @return Builder
         */
        public Builder post() {
            return method(METHOD_POST);
        }

        /**
         * post with the the specified data.
         * @param jsonObject the json data
         * @return Builder
         */
        public Builder post(JSONObject jsonObject) {
            body(jsonObject);
            return post();
        }
        /**
         * put with the the specified data.
         * @param jsonObject the json data
         * @return Builder
         */
        public Builder put(JSONObject jsonObject) {
            body(jsonObject);
            return put();
        }
        /**
         * init {@link EjuRequest} request method with {@link EjuRequest#METHOD_PUT}
         * @return Builder
         */
        public Builder put() {
            return method(METHOD_PUT);
        }
        /**
         * init {@link EjuRequest} request method with {@link EjuRequest#METHOD_DELETE}
         * @return Builder
         */
        public Builder delete() {
            return method(METHOD_DELETE);
        }

        /**
         * add  the specified data to requestBody.
         * @param jsonObject the json data
         * @return Builder
         */
        public Builder body(JSONObject jsonObject) {
            this.body = jsonObject.toString().getBytes();
            headers.put(CONTENT_TYPE, CONTENT_TYPE_JSON);
            headers.put(CONTENT_LENGTH, "" + body.length);
            return this;
        }
        /**
         * add  the specified headers.
         * @param headers the map of header
         * @return Builder
         */
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }
        /**
         * add  the specified data to requestBody.
         * @param name the header key
         * @param value the header value
         * @return Builder
         */
        public Builder addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }
        /**
         * only if the value  not empty add the specified data to requestBody.
         * @param name the header key
         * @param value the header value
         * @return Builder
         */
        public Builder addHeaderIfNotEmpty(String name, String value) {
            if (TextUtils.isEmpty(value)) {
                return this;
            }
            headers.put(name, value);
            return this;
        }
        /**
         * add  the specified headers.
         * @param headers the map of header
         * @return Builder
         */
        public Builder addHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        /**
         * to init a {@link EjuRequest} by your specified url method and headers and so on.
         * @return the {@link EjuRequest}
         */
        public EjuRequest build() {
            return new EjuRequest(url, method, headers, body);
        }
    }

    /**
     * get the contentType
     * @return current contentType
     */
    public String getContentType() {
        if (!headers.containsKey(EjuRequest.CONTENT_TYPE)) {
            return EjuRequest.CONTENT_TYPE_JSON;
        }
        return headers.get(EjuRequest.CONTENT_TYPE);
    }

    /**
     * get the current Body data.
     * @return the data of RequestBody,maybe null.
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * set the RequestBody data.
     * @param body body data
     */
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

    /**
     * get the request type
     * @return one of {@link Method}
     */
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

    /**
     * set the request type
     * @param method one of the {@link Method}
     */
    public void setMethod(int method) {
        this.method = method;
    }

    /**
     * get headers
     * @return current headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * set headers
     * @param headers update the headers with your specified map.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
