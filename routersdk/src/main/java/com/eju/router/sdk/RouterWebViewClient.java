package com.eju.router.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eju.router.sdk.exception.EjuException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;


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
    private final AbstractInterceptor mRemoteInterceptor;
    private final AbstractInterceptor mLocalInterceptor;

    public RouterWebViewClient(WebView webView, HttpClient client) {
        mContext = webView.getContext();
        router = Router.getInstance();
        mRemoteInterceptor = new AbstractInterceptor(new RemoteHtmlLoader(client)) {};
        mLocalInterceptor = new AbstractInterceptor(new NativeHtmlLoader()) {};
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

        final WebResourceResponse wrr;
        if(router.isNativeRouteSchema(url)) {
            wrr = mLocalInterceptor.intercept(url);
        } else {
            wrr = mRemoteInterceptor.intercept(url);
        }

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
                // no '_url' parameter more.
                // ignore '_url' parameter
//                if (Router.EXTRA_URL.equalsIgnoreCase(key)) {
//                    continue;
//                }
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

    private class RemoteHtmlLoader implements HtmlLoader {

        private HttpClient mClient;

        RemoteHtmlLoader(HttpClient client) {
            mClient = client;
        }

        @Override
        @Nullable public HttpClient.Response load(String url) throws IOException {
            return mClient.execute(url);
        }
    }

    private class NativeHtmlLoader implements HtmlLoader {

        private final String ASSETS_BASE = "file:///android_asset/";

        @Override
        public HttpClient.Response load(String url) throws IOException {
            url = "file".concat(url.substring(url.indexOf(':')));
            if(!url.startsWith(ASSETS_BASE)) {
                throw new IOException("invalid url");
            }

            final String path = url;
            return new HttpClient.Response() {
                @Override
                public InputStream getBody() {
                    Resources resources = mContext.getResources();
                    try {
                        return resources.getAssets().open(path.substring(ASSETS_BASE.length()));
                    } catch (IOException e) {
                        return new ByteArrayInputStream(new byte[0]);
                    }
                }

                @Override
                public String getMimeType() {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                    switch (extension) {
                        case "js":
                            return "text/javascript";
                        default:
                            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    }
                }

                @Override
                public String getEncoding() {
                    return "utf-8";
                }

                @Override
                public Map<String, String> getHeaders() {
                    return null;
                }

                @Override
                public int getStatusCode() {
                    return 200;
                }

                @Override
                public String getReasonPhrase() {
                    return "OK";
                }
            };
        }
    }
}
