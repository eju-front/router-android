package com.eju.router.sdk;


import java.io.IOException;
import java.io.InputStream;
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
     * @param url url to load.
     * @return {@link HttpClient.Response}.
     * @throws IOException if exception.
     */
    Response execute(String url) throws IOException;
}
