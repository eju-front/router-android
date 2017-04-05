package com.eju.router.sdk;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * @author Sidney
 */
public class RouterWebView extends WebView {

//    private static final String FILE_SCHEME = "file://";

    public RouterWebView(Context context) {
        this(context, null);
    }

    public RouterWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouterWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        if(!RouterWebViewClient.class.isAssignableFrom(client.getClass())) {
            throw new IllegalArgumentException("WebViewClient must extend RouterWebViewClient");
        }
        super.setWebViewClient(client);
    }

    public void setDebuggable(boolean debuggable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(debuggable);
        }
    }

    public void setNativeCodeInstance(Object obj) {
        addJavascriptInterface(new JsProxy(obj), "_routerNative");
    }

    public void disableSideEffect() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void initialize() {
        String filePath = getContext().getApplicationContext()
                .getDir("router", Context.MODE_PRIVATE).getPath();

        WebSettings webSettings = getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setAppCacheMaxSize(5 * 1048576);
        webSettings.setAppCachePath(filePath);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(filePath);
        webSettings.setGeolocationDatabasePath(filePath);
        webSettings.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        setDebuggable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
    }
}
