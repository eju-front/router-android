package com.eju.router.sdk;

import android.annotation.SuppressLint;
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
public class ProgressWebView extends WebView {

    public ProgressWebView(Context context) {
        this(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        setClickable(true);
        setFocusableInTouchMode(true);
        setWebViewClient(getWebViewClient());
    }

    public void setDebuggable(boolean debuggable) {
        if (debuggable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(debuggable);
        }
    }

    public void disableSideEffect() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    protected WebViewClient getWebViewClient() {
        return new ProgressWebViewClient(getContext());
    }
}
