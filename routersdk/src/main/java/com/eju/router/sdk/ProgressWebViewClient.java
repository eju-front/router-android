package com.eju.router.sdk;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eju.router.sdk.exception.EjuException;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author SidneyXu
 */
public class ProgressWebViewClient extends BridgeWebViewClient {

    protected Context context;
    private static final String JS_DEVICE_READY = "javascript:onDeviceReady()";
    private ProgressDialog progressDialog;


    public ProgressWebViewClient(BridgeWebView webView) {
        super(webView);
        this.context = webView.getContext();
    }
//    ProgressWebViewClient(Context context) {
//        this.context = context;
//    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        EjuLog.d("onPageStarted");
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.show();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        EjuLog.d("onPageFinished");
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
        view.loadUrl(JS_DEVICE_READY);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        EjuLog.d("onReceivedError");
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }
}
