package com.eju.router.sdk;

import android.annotation.TargetApi;
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
import java.io.OutputStream;
import java.util.Map;


/**
 * This class will take effect
 * in {@link android.webkit.WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)} and
 * intercept the process of {@link android.webkit.WebView#loadUrl(String)}. This class will load
 * the contents with {@link HtmlLoader} and handle them by {@link #insert0(String, byte[])}
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
     * @param request page's request
     * @return contents of the specific url in bytes
     * @throws EjuException if error
     */
    @NonNull
    private HttpClient.Response load0(HttpClient.Request request) throws EjuException {
        HttpClient.Response content;
        try {
            content = mLoader.load(request);
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
    private byte[] insert0(@NonNull String url, @NonNull byte[] buffer) throws EjuException {
        if(null != mParamHandler) {
            buffer = mParamHandler.handle(url, buffer);
        }
        return buffer;
    }

    /**
     * intercept the process of
     * {@link WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)}
     *
     * @param request current request.
     * @return {@link WebResourceResponse}
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    WebResourceResponse intercept(final WebResourceRequest request) {
        return intercept(new HttpClient.Request() {
            @Override
            public String getUrl() {
                return request.getUrl().toString();
            }

            @Override
            public String getMethod() {
                return request.getMethod();
            }

            @Override
            public Map<String, String> getHeaders() {
                return request.getRequestHeaders();
            }

            @Override
            public OutputStream getBody() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }
        });
    }

    /**
     * intercept the process of
     * {@link WebViewClient#shouldInterceptRequest(WebView, String)}
     *
     * @param url current url.
     * @return {@link WebResourceResponse}
     */
    WebResourceResponse intercept(final String url) {
        return intercept(new HttpClient.Request() {
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public String getMethod() {
                return "GET";
            }

            @Override
            public Map<String, String> getHeaders() {
                return null;
            }

            @Override
            public OutputStream getBody() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }
        });
    }

    private WebResourceResponse intercept(HttpClient.Request request) {
        final HttpClient.Response response;
        try {
            response = load0(request);
        } catch (EjuException e) {
            return null;
        }

        String mimeType = response.getMimeType();
        mimeType = null == mimeType ? "text/html" : mimeType;

        InputStream is = response.getBody();
        if(null == is) {
            return null;
        }

        switch (mimeType) {
            case "text/html": {
                byte[] data = readStream(is);
                try {
                    // parameter
                    data = insert0(request.getUrl(), data);
                } catch (EjuException ignored) {}
                is = new ByteArrayInputStream(data);
                break;
            }
            default: {}
        }

        String encoding = null == response.getEncoding() ? "utf-8" : response.getEncoding();
        WebResourceResponse wrr = new WebResourceResponse(mimeType, encoding, is);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrr.setResponseHeaders(response.getHeaders());
            try {
                wrr.setStatusCodeAndReasonPhrase(
                        response.getStatusCode(), response.getReasonPhrase());
            } catch (Exception ignored) {}
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException ignored) {}
        }
        return baos.toByteArray();
    }
}
