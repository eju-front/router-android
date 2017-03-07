package com.eju.router.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * The {@code WebViewActivity} is used to display html resource.
 * <p>
 * BTW, this class is also the default html parameter handler.
 * it's used to handle parameter that in url like 'http:\\aa.com?a=1' and take these parameter
 * into html contents.
 *
 * @author SidneyXu
 */
public class WebViewActivity extends AppCompatActivity implements IRouterWebContainer {

//    private static final String TAG = WebViewActivity.class.getSimpleName();

    private RouterWebContainer container = new RouterWebContainer(this);
    private RouterWebView webView;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container.onCreate(savedInstanceState);

        webView = new RouterWebView(this);
        webView.disableSideEffect();
        webView.setNativeCodeInstance(getNativeCodeInstance());

        setContentView(webView);
    }

    @Override
    public void onResume() {
        super.onResume();
        container.onResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        container.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if(!container.onBackPressed(webView)) super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

    public Object getNativeCodeInstance() {
        return null;
    }

    @Override
    public void loadUrl(String url) {
        if (null == url) {
            return;
        }

        webView.loadUrl(url);
    }
}
