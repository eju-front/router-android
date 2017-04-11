package com.eju.router.sdk;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


/**
 * Class description.
 * <p>
 * Created on 3/31/17.
 *
 * @author tangqianwei.
 */
@SuppressWarnings("unused")
public interface HttpClient {

    /**
     * Http request for execute.
     */
    interface Request {
        /**
         * request url.
         *
         * @return url.
         */
        String getUrl();

        /**
         * request method.
         *
         * @return method.
         */
        String getMethod();

        /**
         * request headers.
         *
         * @return headers.
         */
        Map<String, String> getHeaders();

        /**
         * request body (for post).
         *
         * @return body.
         */
        @Nullable OutputStream getBody();

        /**
         * request content-type.
         *
         * @return content-type.
         */
        @Nullable String getContentType();
    }


    /**
     * Http response from execute.
     */
    interface Response {
        /**
         * return response body through {@link InputStream}.
         *
         * @return input stream.
         */
        InputStream getBody();

        /**
         * return response body's mime type/
         *
         * @return mime type.
         */
        String getMimeType();

        /**
         * return response body's encoding.
         *
         * @return encoding.
         */
        String getEncoding();

        /**
         * return response headers.
         * @return headers.
         */
        Map<String, String> getHeaders();

        /**
         * return status code.
         *
         * @return code.
         */
        int getStatusCode();

        /**
         * return reason phrase.
         *
         * @return reason phrase.
         */
        String getReasonPhrase();
    }

    /**
     * Do http request
     *
     * @param request http request.
     * @return {@link HttpClient.Response}.
     * @throws IOException if exception.
     */
    Response execute(Request request) throws IOException;
}
