package com.eju.router.sdk;

import android.webkit.JavascriptInterface;


/**
 * class description
 *
 * @author tangqianwei
 */
class JsProxy {

    private static final String TAG = "JsProxy";

    private RouterBridge<?> bridge;

    JsProxy(Object obj) {
        bridge = new RouterBridge<>(obj);
    }

    @JavascriptInterface
    public void exec(String func, String params) {
        bridge.execute(func, params);
    }
}
