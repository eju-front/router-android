package com.eju.router.sdk;

import android.support.annotation.NonNull;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.eju.router.sdk.exception.EjuException;

import java.io.IOException;


/**
 * This class will take effect
 * in {@link android.webkit.WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)} and
 * intercept the process of {@link android.webkit.WebView#loadUrl(String)}. This class will load
 * the contents with {@link HtmlLoader} handle them by {@link #insert0(String, byte[])}
 *
 * @author tangqianwei
 */
abstract class Interceptor {

    private HtmlLoader mLoader;
    private HtmlHandler mParamHandler;

    Interceptor(HtmlLoader loader) {
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
    byte[] load0(String url) throws EjuException {
        byte[] content;
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
    byte[] insert0(String url, @NonNull byte[] buffer) throws EjuException {
        if(null != mParamHandler) {
            buffer = mParamHandler.handle(url, buffer);
        }
        return buffer;
    }

    /**
     * intercept the process of
     * {@link android.webkit.WebViewClient#shouldInterceptRequest(WebView, WebResourceRequest)}
     *
     * @param url current url
     * @return {@link WebResourceResponse}
     */
    abstract WebResourceResponse intercept(String url);
}
