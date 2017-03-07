package com.eju.router.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebResourceResponse;
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
 * @author SidneyXu (create)
 * @author tangqianwei (edit)
 */
@SuppressWarnings("deprecation")
public class RouterWebViewClient extends WebViewClient implements HtmlHandler {

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

    private Context mContext;

    private final Router router;
    private final Interceptor mRemoteInterceptor;
    private final Interceptor mLocalInterceptor;

    public RouterWebViewClient(WebView webView) {
        mContext = webView.getContext();
        router = Router.getInstance();
        mRemoteInterceptor = new RemoteInterceptor(new RemoteHtmlLoader());
        mLocalInterceptor = new LocalInterceptor(new NativeHtmlLoader());
        mRemoteInterceptor.setParamHandler(this);
        mLocalInterceptor.setParamHandler(this);
    }

    @Override
    public final boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        boolean ret = false;

        if(onBeforeOverrideUrlLoading(view, url)) {
            return true;
        }

        if (router.isNativeRouteSchema(url)) {
            try {
                URI uri = new URI(url);
                router.internalRoute(mContext, uri);
            } catch (URISyntaxException e) {
//                e.printStackTrace();
                router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
            }
            ret = true;
        }

        if(!ret) {
            ret = super.shouldOverrideUrlLoading(view, url);
        }

        onAfterOverrideUrlLoading(view, url);
        return ret;
    }

    @SuppressWarnings("deprecation")
    @Override
    public final WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        EjuLog.d("[ROUTER][LOAD] " + url);

        url = onBeforeInterceptRequest(view, url);

        final Interceptor interceptor;
        if(router.isNativeRouteSchema(url)) {
            interceptor = mLocalInterceptor;
        } else {
            interceptor = mRemoteInterceptor;
        }

        WebResourceResponse wrr = interceptor.intercept(url);
        onAfterInterceptRequest(view, url, wrr);
        return wrr;
    }

    protected boolean onBeforeOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    protected void onAfterOverrideUrlLoading(WebView view, String url) {

    }

    protected String onBeforeInterceptRequest(WebView view, String url) {
        return url;
    }

    protected void onAfterInterceptRequest(WebView view, String url, WebResourceResponse wrr) {

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

        Bundle bundle = ((Activity)mContext).getIntent().getExtras();
        if(null != bundle) {
            StringBuilder builder = new StringBuilder();
            for (String key : bundle.keySet()) {
                // ignore '_url' parameter
                if (key.equalsIgnoreCase(Router.EXTRA_URL)) {
                    continue;
                }
                builder.append(key).append(':').append(parseObjectOfJS(bundle.get(key)))
                        .append(',');
            }
            String params = String.format(SCRIPT, builder.toString());

            i = html.indexOf("</head>");
            html = html.substring(0, i).concat(params).concat(html.substring(i));
        }

        return html.getBytes();
    }

    @NonNull
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


    private class RemoteInterceptor extends Interceptor {

        RemoteInterceptor(HtmlLoader loader) {
            super(loader);
        }

        @Override
        WebResourceResponse intercept(String url) {
            if(url.startsWith("blob:")) {
                return null;
            }

            RouterUrl routerUrl = router.getRouterUrlMatchUrl(url);
            if(null == routerUrl) {
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
    }

    private class LocalInterceptor extends Interceptor {

        LocalInterceptor(HtmlLoader loader) {
            super(loader);
        }

        @Override
        WebResourceResponse intercept(String url) {
            byte[] data;
            try {
                data = load0("file://".concat(
                        url.substring((router.getFirstNativeSchema() + "://").length())));
            } catch (EjuException e) {
//                return new WebResourceResponse("text/html", "utf-8", null);
                return null;
            }

            RouterUrl routerUrl = router.getRouterUrlMatchUrl(url);
            if(null == routerUrl) {
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
                if (BuildConfig.DEBUG) EjuLog.e(e.getMessage());
            }


            return new WebResourceResponse(
                    "text/html", "utf-8", new ByteArrayInputStream(data));
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

            return 200 == response.getStatusCode() ? response.getBody() : null;
        }
    }
}
