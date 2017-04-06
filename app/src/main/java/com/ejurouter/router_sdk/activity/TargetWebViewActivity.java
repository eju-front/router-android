package com.ejurouter.router_sdk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.eju.router.sdk.HttpClient;
import com.eju.router.sdk.RouterWebView;
import com.eju.router.sdk.RouterWebViewClient;

import java.io.IOException;


/**
 * @author tangqianwei
 */
public class TargetWebViewActivity extends Activity implements Handler.Callback {

    private static final int SHOW_DIALOG = 0x123456;
    private static final int DISMISS_DIALOG = 0x654321;

    private RouterWebView mWebView;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RouterWebView webView = new RouterWebView(this);
        webView.setWebViewClient(new RouterWebViewClient(webView, new HttpClient() {
            @Override
            public Response execute(String url) throws IOException {
                return null;
            }
        }) {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mHandler.sendEmptyMessage(SHOW_DIALOG);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mHandler.sendEmptyMessage(DISMISS_DIALOG);
            }
        });
        setContentView(webView);
        mWebView = webView;

        mHandler = new Handler(this);
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        mWebView = null;

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mWebView.loadUrl(getIntent().getDataString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_DIALOG: {
                mProgressDialog.show();
                return true;
            }
            case DISMISS_DIALOG: {
                mProgressDialog.dismiss();
                return true;
            }
            default: {
                return false;
            }
        }
    }

//    @Override
//    protected void initView(Bundle savedInstanceState) {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        webView = (RouterWebView) findViewById(R.id.web_view);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        toolbar.inflateMenu(R.menu.web_menu);
//        toolbar.setTitle("这不是标题");
////        setSupportActionBar(toolbar);
//    }

}
