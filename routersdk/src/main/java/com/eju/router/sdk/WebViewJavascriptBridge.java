package com.eju.router.sdk;


interface WebViewJavascriptBridge {

    void send(String data);

    void send(String data, CallBackFunction responseCallback);

    void callHandler(String handlerName, String data, CallBackFunction callBack);

    void registerHandler(String handlerName, BridgeHandler handler);

    void loadUrl(String jsUrl, CallBackFunction returnCallback);


}
