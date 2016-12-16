package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.eju.router.sdk.exception.EjuException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The {@code WebViewActivity} is used to display html resource.
 *
 * @author SidneyXu
 */

public class WebViewActivity extends Activity {

    public static final String EXTRA_URL = "_url";

    private ProgressWebView webView;
    private Router router;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        router = Router.getInstance();
        webView = getWebView();
        setContentView(webView);

        handleIntent();
    }

    /**
     * Override this if you want to customize webview.
     *
     * @return an instance of com.eju.router.sdk.ProgressWebView
     */
    protected ProgressWebView getWebView() {
        webView = new ProgressWebView(this);
        webView.disableSideEffect();
        webView.setWebViewClient(new ProgressWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebViewActivity.this.url = url;
                if (router.isNativeRouteSchema(url)) {
                    try {
                        URI uri = new URI(url);
                        router.internalRoute(WebViewActivity.this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        return webView;
    }

    private void load(String url) {
        if (null == url) {
            return;
        }
        webView.loadUrl(url);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String url = null;
        if (null != intent && intent.getExtras() != null) {
            url = intent.getStringExtra(EXTRA_URL);
        }
        load(url);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
