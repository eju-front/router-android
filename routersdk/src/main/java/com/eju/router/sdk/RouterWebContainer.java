package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


/**
 * class description
 *
 * @author tangqianwei
 */
public class RouterWebContainer {

    private IRouterWebContainer mContainer;
    private Activity mActivity;

    private boolean isNeedReload = false;

//    private Router mRouter = Router.getInstance();

    public RouterWebContainer(IRouterWebContainer container) {
        mContainer = container;
        try {
            mActivity = Activity.class.cast(container);
        } catch (ClassCastException e) {
            throw new ClassCastException(container.getClass().getName() + "must be an Activity");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        isNeedReload = true;
    }


    public void onResume() {
        if(isNeedReload) {
            Intent intent = mActivity.getIntent();
            String url = null;
            if (null != intent && intent.getExtras() != null) {
                url = intent.getStringExtra("_url");
            }

            mContainer.loadUrl(url);
        }
        isNeedReload = false;
    }

    public void onNewIntent(Intent intent) {
        mActivity.setIntent(intent);
        isNeedReload = true;
    }

    public boolean onBackPressed(WebView webView) {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
