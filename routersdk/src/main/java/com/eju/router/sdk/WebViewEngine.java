package com.eju.router.sdk;

import android.content.Intent;

import com.eju.router.sdk.exception.EjuException;

/**
 * Created by Joe on 2017/2/23.
 * Email lovejjfg@gmail.com
 */

 interface WebViewEngine {

    void initWebView();

    byte[] handle(String url, byte[] contents) throws EjuException;

    void handleIntent(Intent intent);

    boolean onBackPressed();

    void onDestroy();


}



