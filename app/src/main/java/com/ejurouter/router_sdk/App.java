package com.ejurouter.router_sdk;

import android.app.Application;

import com.eju.router.sdk.EjuRequest;
import com.eju.router.sdk.Option;
import com.eju.router.sdk.Router;
import com.ejurouter.router_sdk.activity.DefaultActivity;
import com.ejurouter.router_sdk.activity.TargetWebViewActivity;

/**
 * Created by Sidney on 2016/11/25.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router router = Router.getInstance();
        Option option = new Option();
        option.defaultVersion = "V1.0.0";
        option.request = EjuRequest.newBuilder()
                .url("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android")
                .method(EjuRequest.METHOD_GET)
                .build();
        router.initialize(this, option);
        router.set404ViewMap(DefaultActivity.class.getName());
        router.setWebViewActivity(TargetWebViewActivity.class);
        router.registerPageNeedNativeParameter(".*172\\.29\\.32\\.215:8080.*");
    }
}
