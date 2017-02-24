package com.ejurouter.router_sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.eju.router.sdk.BridgeHandler;
import com.eju.router.sdk.BridgeWebView;
import com.eju.router.sdk.CallBackFunction;
import com.eju.router.sdk.JsCallBack;
import com.eju.router.sdk.ViewMapInfo;
import com.ejurouter.router_sdk.R;


public class TargetWebViewActivity extends AppCompatActivity {

    private BridgeWebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_web_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (BridgeWebView) findViewById(R.id.web_view);
        webView.handleIntent(getIntent());

        webView.addJavascriptInterface(new JsCallBack() {
            @Override
            @JavascriptInterface
            public String onMenuTextReady(String text) {//参数传给本地 返回值返回给调用者
                Log.e("TAG", "onMenuTextReady: " + text);
                return "This is the return value: " + text;
            }

            @Override
            @JavascriptInterface
            public void onMenuTextClicked(String url) {
                Log.e("TAG", "onMenuTextClicked: " + url);
            }
        }, "Android");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.inflateMenu(R.menu.web_menu);
        toolbar.setTitle("这不是标题");
//        setSupportActionBar(toolbar);
        //toolbar 与 js 的相关回调 自行添加
        webView.registerHandler("ToolBarJs", new BridgeHandler() {
            @Override
            public void handler(BridgeWebView webView, String url, CallBackFunction function) {
                if (TextUtils.equals("toggleToolBar", url)) {
                    toolbar.setVisibility(toolbar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    return;
                }
                if (TextUtils.equals("toggleMenu", url)) {
                    toolbar.showOverflowMenu();
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        webView.handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        webView.handleDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!webView.onBackPressed()) {
            super.onBackPressed();
        }
    }


}
