package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;

import java.io.IOException;

/**
 * handle http request and response.
 */
/*package*/ abstract class EjuHttpClient<ExternalRequest, ExternalResponse> {

    private static final String OKHTTPCLIENT_PATH = "okhttp3.OkHttpClient";
    private static final String VOLLEY_PATH = "com.android.volley.toolbox.Volley";

    /*package*/ int timeout;

    /*package*/ EjuHttpClient(int timeout) {
        this.timeout = timeout;
    }

    /**
     * decide to use which Client.
     * @param timeout the timeout with ms.
     * @return the client
     */
    /*package*/ static EjuHttpClient newClient(int timeout) {
        EjuHttpClient client;
        if (hasOkHttpOnClassPath()) {
            client = new EjuOkHttpClient(timeout);
        } else if (hasVolleyOnClassPath()) {
            client = new EjuVolleyClient(timeout);
        } else {
            client = new EjuURLConnectionClient(timeout);
        }
        return client;
    }

    /**
     * whether it can handle with OkHttp or not.
     * @return true it can by handle by OkHttp false otherwise.
     */
    private static boolean hasOkHttpOnClassPath() {
        try {
            Class.forName(OKHTTPCLIENT_PATH);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    /**
     * whether it can handle with Volley or not.
     * @return true it can by handle by Volley false otherwise.
     */
    private static boolean hasVolleyOnClassPath() {
        try {
            Class.forName(VOLLEY_PATH);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public abstract EjuResponse execute(EjuRequest ejuRequest) throws EjuException;

    abstract ExternalRequest getRequest(EjuRequest ejuRequest) throws IOException;

    abstract EjuResponse getResponse(ExternalResponse externalResponse) throws EjuException;

}
