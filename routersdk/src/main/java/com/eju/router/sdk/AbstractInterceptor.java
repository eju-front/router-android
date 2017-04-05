package com.eju.router.sdk;

import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eju.router.sdk.exception.EjuException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class will take effect
 * in {@link android.webkit.WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)} and
 * intercept the process of {@link android.webkit.WebView#loadUrl(String)}. This class will load
 * the contents with {@link HtmlLoader} handle them by {@link #insert0(String, byte[])}
 *
 * @author tangqianwei
 */
abstract class AbstractInterceptor {

    private HtmlLoader mLoader;
    private HtmlHandler mParamHandler;

    AbstractInterceptor(HtmlLoader loader) {
        mLoader = loader;
    }

    /**
     * set parameter handler to handle parameter
     *
     * @param handler default parameter handler
     */
    void setParamHandler(HtmlHandler handler) {
        mParamHandler = handler;
    }

    /**
     * load contents of the specific url
     *
     * @param url page's url
     * @return contents of the specific url in bytes
     * @throws EjuException if error
     */
    @NonNull
    private HttpClient.Response load0(String url) throws EjuException {
        HttpClient.Response content;
        try {
            content = mLoader.load(url);
        } catch (IOException e) {
            throw new EjuException(e);
        }

        if(null == content) {
            throw new EjuException("cannot load anything");
        }
        return content;
    }

    /**
     * modify contents with {@link HtmlHandler}
     *
     * @param url page's url
     * @param buffer origin contents
     * @return modified contents
     * @throws EjuException if error
     */
    @NonNull
    private byte[] insert0(String url, @NonNull byte[] buffer) throws EjuException {
        if(null != mParamHandler) {
            buffer = mParamHandler.handle(url, buffer);
        }
        return buffer;
    }

    /**
     * intercept the process of
     * {@link WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)}
     *
     * @param url current url
     * @return {@link WebResourceResponse}
     */
    WebResourceResponse intercept(String url) {
        if(url.startsWith("blob:")) {
            return null;
        }

        final HttpClient.Response response;
        try {
            response = load0(url);
        } catch (EjuException e) {
            return null;
        }

        InputStream is;
        final String mimeType = response.getMimeType();
        switch (mimeType) {
            case "text/html": {
                byte[] data = readStream(response.getBody());
                try {
                    // parameter
                    data = insert0(url, data);
                } catch (EjuException ignored) {}
                is = new ByteArrayInputStream(data);
                break;
            }
            default: {
                is = response.getBody();
            }
        }

        WebResourceResponse wrr = new WebResourceResponse(mimeType, response.getEncoding(), is);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrr.setResponseHeaders(response.getHeaders());
            wrr.setStatusCodeAndReasonPhrase(response.getStatusCode(), response.getReasonPhrase());
        }
        return wrr;
    }

    private byte[] readStream(InputStream is) {
        byte[] buffer = new byte[0xff];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            int count;
            while(-1 != (count = is.read(buffer))) {
                baos.write(buffer, 0, count);
                baos.flush();
            }
        } catch (IOException ignored) {

        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException ignored) {}
        }
        return baos.toByteArray();
    }
}
