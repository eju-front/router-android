package com.eju.router.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.eju.router.sdk.exception.EjuException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class DefaultHandler implements BridgeHandler {

    private final Router router;
    private final Context context;

    public DefaultHandler(Context context) {
        this.context = context;
        router = Router.getInstance();
    }

    String TAG = "DefaultHandler";

    @Override
    public void handler(BridgeWebView webView, String url, CallBackFunction function) {
        EjuLog.e(TAG, "handler: " + url);
//        EjuLog.d("shouldOverrideUrlLoading");
        if (function != null) {
            function.onCallBack("DefaultHandler response data:" + url);
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            webView.loadUrl(url);
            return;
        }
        if (router.isNativeRouteSchema(url)) {
            try {
                URI uri = new URI(url);
                router.internalRoute(context, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
            }
        }
    }


}
