package com.eju.router.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

import com.eju.router.sdk.exception.EjuException;

/**
 * @author Sidney
 */
@Deprecated
public class ProgressWebView extends BridgeWebView implements HtmlHandler, IWebView {




    private WebViewEngineImpl mHelper;

    public ProgressWebView(Context context) {
        this(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        mHelper.initWebView(this);
    }




}
