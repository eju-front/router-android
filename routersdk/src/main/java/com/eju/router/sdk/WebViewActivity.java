package com.eju.router.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.eju.router.sdk.exception.EjuException;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * The {@code WebViewActivity} is used to display html resource.
 * <p>
 * BTW, this class is also the default html parameter handler.
 * it's used to handle parameter that in url like 'http:\\aa.com?a=1' and take these parameter
 * into html contents.
 *
 * @author SidneyXu
 */

public class WebViewActivity extends AppCompatActivity implements HtmlHandler, BridgeHandler {
    private static final String TAG = WebViewActivity.class.getSimpleName();
    public static final String EXTRA_URL = "_url";

    private static final String END_HTML = "</html>";
    private static final String SCRIPT =
            "<script type=\"text/javascript\">" +
                    "var router_params = " +
                    (BuildConfig.DEBUG ? "'" : "") +
                    "{" +
                    "%s" +
                    "}" +
                    (BuildConfig.DEBUG ? "'" : "") +
                    ";" +
                    "</script>\n";


    private ProgressWebView webView;
    private Router router;
    //    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        webView = getWebView();
        setContentView(R.layout.activity_web);
        webView = (ProgressWebView) findViewById(R.id.web);
        getWebView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("这不是标题");

        handleIntent();
    }

    /**
     * Override this if you want to customize webview.
     *
     * @return an instance of com.eju.router.sdk.ProgressWebView
     */
    protected ProgressWebView getWebView() {
//        webView = new ProgressWebView(this);
        webView.disableSideEffect();
        webView.getRemoteInterceptor().setParamHandler(this);
        webView.getLocalInterceptor().setParamHandler(this);
        webView.setDefaultHandler(this);
        webView.registerHandler("ToolBarJs", new BridgeHandler() {
            @Override
            public void handler(BridgeWebView webView, String url, CallBackFunction function) {
                if (TextUtils.equals("toggleToolBar", url)) {
                    toolbar.setVisibility(toolbar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
        });
        webView.send("Hello ，Kugou");
        return webView;
    }

    private void load(String url) {
        if (null == url) {
            return;
        }

        webView.loadUrl(url);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String url = null;
        if (null != intent && intent.getExtras() != null) {
            url = intent.getStringExtra(EXTRA_URL);
        }
        load(url);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
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

        Bundle bundle = getIntent().getExtras();
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
    public void handler(BridgeWebView webView, String url, CallBackFunction function) {
        Log.e(TAG, "handler: " + url);
        EjuLog.d("shouldOverrideUrlLoading");
        if (function != null) {
            function.onCallBack("DefaultHandler response data:" + url);
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            webView.loadUrl(url);
            return;
        }
        if (null == router) {
            router = Router.getInstance();
        }
        if (router.isNativeRouteSchema(url)) {
            try {
                URI uri = new URI(url);
                router.internalRoute(this, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
            }
        }
    }
}
