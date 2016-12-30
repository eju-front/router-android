package com.eju.router.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
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
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Sidney
 */
public class ProgressWebView extends WebView {

    private static final String FILE_SCHEME = "file://";

    private final Interceptor remoteInterceptor = new Interceptor(new RemoteHtmlLoader()) {
        @Override
        WebResourceResponse intercept(String url) {
            RouterUrl routerUrl = router.getRouterUrlMatchUrl(url);
            if(null == routerUrl || !routerUrl.shouldBeIntercept()) {
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
            if(null != routerUrl && routerUrl.shouldBeIntercept()) {
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

    private Router router;

    public ProgressWebView(Context context) {
        this(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        router = Router.getInstance();

        initialize();
    }

    /**
     * If url scheme matches "file://", then this method will redirect it to "eju://".
     * See {@link WebView#loadUrl(String)} comment's for more.
     *
     * @param url the URL of the resource to load
     */
    @Override
    public void loadUrl(String url) {
        EjuLog.d(String.format("loadUrl:[%s]", url));

        if(url.startsWith(FILE_SCHEME)) {
            url = "eju://" + url.substring(FILE_SCHEME.length());
        }

        super.loadUrl(url);
    }

    public void setDebuggable(boolean debuggable) {
        if (debuggable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
    }

    public Interceptor getRemoteInterceptor() {
        return remoteInterceptor;
    }

    public Interceptor getLocalInterceptor() {
        return localInterceptor;
    }

    public void disableSideEffect() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    protected WebViewClient getWebViewClient() {

        return new ProgressWebViewClient(getContext()) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                EjuLog.d("shouldOverrideUrlLoading");

                if (router.isNativeRouteSchema(url)) {
                    try {
                        URI uri = new URI(url);
                        router.internalRoute(context, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                EjuLog.d("shouldInterceptRequest");

                final Interceptor interceptor;
                if(router.isNativeRouteSchema(url)) {
                    interceptor = localInterceptor;
                } else {
                    interceptor = remoteInterceptor;
                }

                return interceptor.intercept(url);
            }
        };
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);

        setClickable(true);
        setFocusableInTouchMode(true);
        setWebViewClient(getWebViewClient());
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
            Resources resources = getContext().getResources();
            if(url.startsWith(ASSETS_BASE)) {
                return resources.getAssets().open(url.substring(ASSETS_BASE.length()));
            } else {
                throw new IOException("invalid url");
            }
        }

        private void close(Closeable closeable) {
            if(null != closeable) { try {
                closeable.close();
            } catch (IOException e) {
                EjuLog.e(e.getMessage());
            } }
        }
    }

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
}
