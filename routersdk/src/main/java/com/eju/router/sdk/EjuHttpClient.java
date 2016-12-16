package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;

import java.io.IOException;

/*package*/ abstract class EjuHttpClient<ExternalRequest, ExternalResponse> {

    private static final String OKHTTPCLIENT_PATH = "okhttp3.OkHttpClient";
    private static final String VOLLEY_PATH = "com.android.volley.toolbox.Volley";

    /*package*/ int timeout;

    /*package*/ EjuHttpClient(int timeout) {
        this.timeout = timeout;
    }

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

    private static boolean hasOkHttpOnClassPath() {
        try {
            Class.forName(OKHTTPCLIENT_PATH);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

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
