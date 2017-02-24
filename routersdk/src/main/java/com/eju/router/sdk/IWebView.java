package com.eju.router.sdk;

import android.content.Intent;

/**
 * Created by Joe on 2017/2/23.
 * Email lovejjfg@gmail.com
 */

public interface IWebView {

//    boolean isInitialized();

//    View getView();

    void stopLoading();

    boolean canGoBack();

    void clearCache();

    void clearHistory();

    boolean onBackPressed();

    void handlePause(boolean keepRunning);

    void handleIntent(Intent intent);

    void handleResume(boolean keepRunning);

    void handleStart();

    void handleStop();

    void handleDestroy();


    WebViewEngine getEngine();

    String getUrl();

    void loadUrl(String url);

//    Object postMessage(String id, Object data);

}



