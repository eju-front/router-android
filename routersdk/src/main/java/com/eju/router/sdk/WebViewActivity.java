package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.eju.router.sdk.exception.EjuException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * The {@code WebViewActivity} is used to display html resource.
 *
 * BTW, this class is also the default html parameter handler.
 * it's used to handle parameter that in url like 'http:\\aa.com?a=1' and take these parameter
 * into html contents.
 *
 * @author SidneyXu
 */

public class WebViewActivity extends Activity implements HtmlHandler {

    public static final String EXTRA_URL = "_url";

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


    private ProgressWebView webView;
    private Router router;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        router = Router.getInstance();
        webView = getWebView();
        setContentView(webView);

        handleIntent();
    }

    /**
     * Override this if you want to customize webview.
     *
     * @return an instance of com.eju.router.sdk.ProgressWebView
     */
    protected ProgressWebView getWebView() {
        webView = new ProgressWebView(this);
        webView.disableSideEffect();
        webView.setWebViewClient(new ProgressWebViewClient(this) {

            private EjuHttpClient mHttpClient = EjuHttpClient.newClient(5000);

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                EjuLog.d("shouldOverrideUrlLoading");

                WebViewActivity.this.url = url;
                if (router.isNativeRouteSchema(url)) {
                    try {
                        URI uri = new URI(url);
                        router.internalRoute(WebViewActivity.this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
                RouterUrl routerUrl = router.getRouterUrlMatchUrl(url);
                if(null == routerUrl || !routerUrl.shouldBeIntercept()) {
                    return null;
                }

                try {
                    EjuResponse response = mHttpClient.execute(
                            new EjuRequest.Builder()
                                    .url(url)
                                    .method(EjuRequest.METHOD_GET)
                                    .build());
                    byte[] content = response.getBody();

                    // user's handler
                    HtmlHandler handler = routerUrl.getHandler();
                    if(null != handler) {
                        content = handler.handle(url, content);
                    }

                    // need parameter in native ?
                    if(routerUrl.isNeedParameter()) {
                        content = WebViewActivity.this.handle(url, content);
                    }

                    return new WebResourceResponse(
                            "text/html", "utf-8",
                            new BufferedInputStream(
                                    new ByteArrayInputStream(content)));
                } catch (EjuException e) {
                    return null;
                }
            }
        });
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
        if(-1 == i) {
            throw new EjuException(String.format("[%s] has wrong html format !", url));
        }

        int length = html.length();
        if(i + END_HTML.length() > length) {
            html = html.substring(0, i + END_HTML.length());
        }

        Bundle bundle = getIntent().getExtras();
        StringBuilder builder = new StringBuilder();
        for(String key : bundle.keySet()) {
            // ignore '_url' parameter
            if(key.equalsIgnoreCase(EXTRA_URL)) {
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
        if(null == object) {
            return "null";
        }

        Class<?> clazz = object.getClass();
        StringBuilder builder = new StringBuilder();

        if(String.class.isAssignableFrom(clazz)) {
            builder.append('"').append(object).append('"');
        } else if(ArrayList.class.isAssignableFrom(clazz)
                || clazz.isArray()) {
            builder.append('[');
            for(Object o : ((ArrayList)object)) {
                builder.append(parseObjectOfJS(o)).append(',');
            }
            builder.append(']');
        } else if(Boolean.class.isAssignableFrom(clazz)
                || Byte.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Short.class.isAssignableFrom(clazz)
                || Integer.class.isAssignableFrom(clazz)
                || Float.class.isAssignableFrom(clazz)
                || Double.class.isAssignableFrom(clazz)
                || Long.class.isAssignableFrom(clazz)) {
            builder.append(object);
        } else {
            builder.append("{");

            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                // ignore {@code this} field.
                // Is there better solution to this ?
                if(field.getDeclaringClass() != clazz
                        || field.getName().matches(".*this.*")) {
                    continue;
                }

                try {
                    builder.append(field.getName()).append(':')
                            .append(parseObjectOfJS(field.get(object)));
                    builder.append(',');
                } catch (IllegalAccessException ignored) {}
            }
            builder.append("}");
        }

        return builder.toString();
    }
}
