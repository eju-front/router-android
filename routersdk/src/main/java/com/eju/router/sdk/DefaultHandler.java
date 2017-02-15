package com.eju.router.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.eju.router.sdk.exception.EjuException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class DefaultHandler implements BridgeHandler {

    private final Router router;
    private final Context context;

    public DefaultHandler(Context context) {
        this.context = context;
        router = Router.getInstance();
    }

    String TAG = "DefaultHandler";

    @Override
    public void handler(String url, CallBackFunction function) {
        Log.e(TAG, "handler: " + url);
        EjuLog.d("shouldOverrideUrlLoading");
        if (router.isNativeRouteSchema(url)) {
            try {
                URI uri = new URI(url);
                router.internalRoute(context, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                router.broadcastException(new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage()));
            }
        }
        if (function != null) {
            function.onCallBack("DefaultHandler response data:" + url);
        }
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
            Resources resources = context.getResources();
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
