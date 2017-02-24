package com.eju.router.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eju.router.sdk.exception.EjuException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Joe on 2017/2/23.
 * Email lovejjfg@gmail.com
 */

class WebViewEngineImpl implements WebViewEngine {
    static final String EXTRA_URL = "_url";

    private static final String END_HTML = "</html>";
    private static final String SCRIPT =
            "<script type=\"text/javascript\">" +
                    "var eju_router_param = " +
                    (BuildConfig.DEBUG ? "'" : "") +
                    "{" +
                    "%s" +
                    "}" +
                    (BuildConfig.DEBUG ? "'" : "") +
                    ";" +
                    "</script>\n";
    private final Router router;
    private final HtmlHandler mHtmlHandler;
    private BridgeWebView mWebView;
    private final Context mContext;


    WebViewEngineImpl(Context context, BridgeWebView webView, HtmlHandler handler) {
        if (webView == null) {
            throw new RuntimeException("请先设置对应的WebView");
        }
        router = Router.getInstance();
        mContext = context;
        mWebView = webView;
        mHtmlHandler = handler;
        initWebView();
    }

    // TODO: 2017/2/23 WebView初始化那些具体的设置 ？？ 移除BridgeWebView中重复的初始化设置。
    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void initWebView() {

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.disableSideEffect();
        remoteInterceptor.setParamHandler(mHtmlHandler);
        localInterceptor.setParamHandler(mHtmlHandler);

        //JSBridge
        mWebView.setDefaultHandler(new DefaultHandler(mContext));

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setClickable(true);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setWebViewClient(getWebViewClient());

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.e("TAG", "onJsPrompt: " + message);
                if (message.startsWith("http://") || message.startsWith("https://")) {
                    mWebView.loadUrl(message);
                    result.confirm("load new url");
                    return true;
                }
                if (router.isNativeRouteSchema(message)) {
                    try {
                        URI uri = new URI(message);
                        router.internalRoute(mContext, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
                    }
                    result.confirm("router new url");
                }
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
    }

    @Override
    public byte[] handle(String url, byte[] contents) throws EjuException {

        String html = new String(contents);

        int i = html.lastIndexOf(END_HTML);
        if (-1 == i) {
            throw new EjuException(String.format("[%s] has wrong html format !", url));
        }

        int length = html.length();
        if (i + END_HTML.length() > length) {
            html = html.substring(0, i + END_HTML.length());
        }

        Bundle bundle = ((Activity) mContext).getIntent().getExtras();
        StringBuilder builder = new StringBuilder();
        for (String key : bundle.keySet()) {
            // ignore '_url' parameter
            if (key.equalsIgnoreCase(EXTRA_URL)) {
                continue;
            }
            builder.append(key).append(':').append(parseObjectOfJS(bundle.get(key)))
                    .append(',');
        }
        String params = String.format(SCRIPT, builder.toString());

        i = html.indexOf("</head>");
        html = html.substring(0, i).concat(params).concat(html.substring(i));

        return html.getBytes();

    }

    private String parseObjectOfJS(Object object) {
        StringBuilder builder = new StringBuilder();

        if (null == object) {
            builder.append("null");
            return builder.toString();
        }

        Class<?> clazz = object.getClass();
        if (String.class.isAssignableFrom(clazz)) {
            builder.append('"').append(object).append('"');
        } else if (ArrayList.class.isAssignableFrom(clazz)
                || clazz.isArray()) {
            builder.append('[');
            for (Object o : ((ArrayList) object)) {
                builder.append(parseObjectOfJS(o)).append(',');
            }
            builder.append(']');
        } else if (Boolean.class.isAssignableFrom(clazz)
                || Byte.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Short.class.isAssignableFrom(clazz)
                || Integer.class.isAssignableFrom(clazz)
                || Float.class.isAssignableFrom(clazz)
                || Double.class.isAssignableFrom(clazz)
                || Long.class.isAssignableFrom(clazz)) {
            builder.append(object);
        } else {
            builder.append('{');

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // ignore {@code this} field.
                // Is there better solution to this ?
                if (field.getDeclaringClass() != clazz
                        || field.getName().matches(".*this.*")) {
                    continue;
                }

                try {
                    builder.append(field.getName()).append(':')
                            .append(parseObjectOfJS(field.get(object)));
                    builder.append(',');
                } catch (IllegalAccessException ignored) {
                }
            }
            builder.append('}');
        }

        return builder.toString();
    }

    @Override
    public void handleIntent(Intent intent) {
        String url = null;
        if (null != intent && intent.getExtras() != null) {
            url = intent.getStringExtra(EXTRA_URL);
        }
        if (null == url) {
            return;
        }
        mWebView.loadUrl(url);
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }

    protected WebViewClient getWebViewClient() {

        return new ProgressWebViewClient(mWebView) {
            //            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                EjuLog.d("shouldOverrideUrlLoading");
//
//                if (router.isNativeRouteSchema(url)) {
//                    try {
//                        URI uri = new URI(url);
//                        router.internalRoute(context, uri);
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                        router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
//                    }
//                    return true;
//                }
//                return super.shouldOverrideUrlLoading(view, url);
//            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                EjuLog.d("shouldInterceptRequest");

                final Interceptor interceptor;
                if (router.isNativeRouteSchema(url)) {
                    interceptor = localInterceptor;
                } else {
                    interceptor = remoteInterceptor;
                }
                return interceptor.intercept(url);
            }
        };
    }


    private final Interceptor remoteInterceptor = new Interceptor(new RemoteHtmlLoader()) {
        @Override
        WebResourceResponse intercept(String url) {
            RouterUrl routerUrl = router.getRouterUrlMatchUrl(url);
            if (null == routerUrl || !routerUrl.shouldBeIntercept()) {
                return null;
            }

            byte[] data;
            try {
                data = load0(url);
            } catch (EjuException e) {
                return null;
            }

            try {
                // parameter
                if (routerUrl.isNeedParameter()) {
                    data = insert0(routerUrl.getCurrentUrl(), data);
                }
                // user's handler
                HtmlHandler handler = routerUrl.getHandler();
                if (null != handler) {
                    data = handler.handle(routerUrl.getCurrentUrl(), data);
                }
            } catch (EjuException e) {
                return null;
            }

            return new WebResourceResponse(
                    "text/html", "utf-8", new ByteArrayInputStream(data));
        }
    };

    private final Interceptor localInterceptor = new Interceptor(new NativeHtmlLoader()) {
        @Override
        WebResourceResponse intercept(String url) {
            byte[] data;
            try {
                data = load0("file://".concat(url.substring("eju://".length())));
            } catch (EjuException e) {
                return new WebResourceResponse("text/html", "utf-8", null);
            }

            RouterUrl routerUrl = router.getRouterUrlMatchUrl(url);
            if (null != routerUrl && routerUrl.shouldBeIntercept()) {
                try {
                    // parameter
                    if (routerUrl.isNeedParameter()) {
                        data = insert0(routerUrl.getCurrentUrl(), data);
                    }
                    // user's handler
                    HtmlHandler handler = routerUrl.getHandler();
                    if (null != handler) {
                        data = handler.handle(routerUrl.getCurrentUrl(), data);
                    }
                } catch (EjuException e) {
                    if (BuildConfig.DEBUG) EjuLog.e(e.getMessage());
                }
            }

            return new WebResourceResponse(
                    "text/html", "utf-8", new ByteArrayInputStream(data));
        }
    };

    private class RemoteHtmlLoader implements HtmlLoader {

        private EjuHttpClient mHttpClient = EjuHttpClient.newClient(5000);

        @Override
        public byte[] load(String url) throws IOException {
            EjuResponse response;
            try {
                response = mHttpClient.execute(new EjuRequest.Builder()
                        .url(url)
                        .method(EjuRequest.METHOD_GET)
                        .build());
            } catch (EjuException e) {
                throw new IOException(e);
            }

            return response.getBody();
        }
    }

    private class NativeHtmlLoader implements HtmlLoader {

        private final String ASSETS_BASE = "file:///android_asset/";

        @Override
        public byte[] load(String url) throws IOException {
            ByteArrayOutputStream os = null;
            InputStream is = null;
            try {
                os = new ByteArrayOutputStream();
                is = getInputStreamByUrl(url);

                byte[] buff = new byte[512];
                int count;
                while (-1 != (count = is.read(buff))) {
                    os.write(buff, 0, count);
                }

                return os.toByteArray();

            } finally {
                close(os);
                close(is);
            }
        }

        private InputStream getInputStreamByUrl(String url) throws IOException {
            Resources resources = mContext.getResources();
            if (url.startsWith(ASSETS_BASE)) {
                return resources.getAssets().open(url.substring(ASSETS_BASE.length()));
            } else {
                throw new IOException("invalid url");
            }
        }

        private void close(Closeable closeable) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    EjuLog.e(e.getMessage());
                }
            }
        }
    }
}



