package com.ejurouter.router_sdk.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.eju.router.sdk.BridgeHandler;
import com.eju.router.sdk.BridgeWebView;
import com.eju.router.sdk.CallBackFunction;
import com.eju.router.sdk.ProgressWebView;
import com.eju.router.sdk.WebViewActivity;
import com.ejurouter.router_sdk.R;


public class TargetWebViewActivity extends WebViewActivity {

    private ProgressWebView webView;
    private Toolbar toolbar;

    @Override
    protected void initView(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (ProgressWebView) findViewById(R.id.web_view);

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
    protected int getLayoutId() {
        return R.layout.activity_target_web_view;
    }

    @Override
    public ProgressWebView getWebView() {
        return webView;
    }

}
