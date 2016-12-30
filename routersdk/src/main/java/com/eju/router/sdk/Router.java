package com.eju.router.sdk;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.eju.router.sdk.exception.EjuException;
import com.eju.router.sdk.exception.EjuParamException;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The {@code Router} is used to manage the application router.
 *
 * @author SidneyXu
 */
public class Router {
    private static final String TAG = Router.class.getSimpleName();

    private static Router sInstance;

    private final ConcurrentHashMap<ExceptionHandler, ExceptionHandler> handlers = new ConcurrentHashMap<>();

    private ViewMapManager mapManager;
    private ViewMapInfo info404;
    private RouterHandler routerHandler;
    private ArrayList<String> nativeSchema;
    private ArrayList<RouterUrl> urlList;
    private boolean initialized;
    private EjuRequest updateRequest;

    /**
     * Constructs a new {@code Router}.
     */
    public Router() {
        routerHandler = new RouterHandler();
        nativeSchema = new ArrayList<>();
        nativeSchema.add("eju");

        urlList = new ArrayList<>();
    }

    /**
     * Return the singleton instance.
     */
    public static Router getInstance() {
        if (null == sInstance) {
            sInstance = new Router();
        }
        return sInstance;
    }

    /**
     * Initialize this object with a given context.
     *
     * @param context the android context
     */
    public void initialize(Context context) {
        initialize(context, null);
    }

    /**
     * Initialize this object with a given context and option.
     *
     * @param context the android context
     * @param option  the specified option
     */
    public void initialize(Context context, Option option) {
        if (null == context) {
            throw new NullPointerException("context is required!");
        }
        if (initialized) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        if (null != option) {
            if (null != option.nativeRouteSchema) {
                nativeSchema = new ArrayList<>(option.nativeRouteSchema);
            }
            if (null != option.request) {
                updateRequest = option.request;
            }
            if (!TextUtils.isEmpty(option.defaultVersion)) {
                ViewMapManager.DEFAULT_VERSION = option.defaultVersion;
            }
        }

        prepareRemoteData(applicationContext);
        initialized = true;
    }

    /* package for test */ void setMapManager(ViewMapManager mapManager) {
        this.mapManager = mapManager;
    }

    /**
     * Set the 404 resource to this object. Default is null.
     *
     * @param resource the 404 resource
     */
    public void set404ViewMap(String resource) {
        if (null == resource) {
            info404 = null;
        } else {
            info404 = new ViewMapInfo(null, ViewMapInfo.TYPE_NATIVE, resource, null);
        }
    }

    /**
     * add a handler to specific url when processing it
     *
     * @param   pattern regex url pattern, e.g. ".+\.baidu\.com", "cy\.eju\..+"
     * @param   handler handler.
     */
    public void addHtmlHandlerWithUrl(String pattern, HtmlHandler handler) {
        RouterUrl routerUrl = getUrlFromListById(pattern);
        if(null == routerUrl) {
            routerUrl = new RouterUrl(pattern);
            urlList.add(routerUrl);
        }
        routerUrl.setHandler(handler);
        routerUrl.setShouldBeIntercept(true);
    }

    /**
     * remove a handler of specific url
     *
     * @param pattern regex url pattern, e.g. ".+\.baidu\.com", "cy\.eju\..+"
     */
    public void removeHtmlHandlerWithUrl(String pattern) {
        RouterUrl routerUrl = getUrlFromListById(pattern);
        if(null != routerUrl) {
            urlList.remove(routerUrl);
        }
    }

    /**
     * register a page that need parameter in native
     *
     * @param pattern regex url pattern, e.g. ".+\.baidu\.com", "cy\.eju\..+"
     */
    public void registerPageNeedNativeParameter(String pattern) {
        RouterUrl routerUrl = getUrlFromListById(pattern);
        if(null == routerUrl) {
            routerUrl = new RouterUrl(pattern);
            urlList.add(routerUrl);
        }
        routerUrl.setNeedParameter(true);
        routerUrl.setShouldBeIntercept(true);

    }

    /**
     * unregister page in current list
     *
     * @param pattern regex url pattern, e.g. ".+\.baidu\.com", "cy\.eju\..+"
     */
    public void unregisterPageNeedNativeParameter(String pattern) {
        RouterUrl routerUrl = getUrlFromListById(pattern);
        if(null == routerUrl) {
            routerUrl = new RouterUrl(pattern);
            urlList.add(routerUrl);
        }
        routerUrl.setNeedParameter(false);
        if(null == routerUrl.getHandler()) {
            routerUrl.setShouldBeIntercept(false);
        } else {
            routerUrl.setShouldBeIntercept(true);
        }
    }

    /**
     * Register a {@code ExceptionHandler} to this object.
     *
     * @param handler the handler to registered
     */
    public void register(ExceptionHandler handler) {
        handlers.put(handler, handler);
    }

    /**
     * Unregister the {@code ExceptionHandler} from this object.
     *
     * @param handler the handler to registered
     */
    public void unregister(ExceptionHandler handler) {
        handlers.remove(handler);
    }

    /**
     * Unregister all registered {@code ExceptionHandler}.
     */
    public void unregisterAll() {
        handlers.clear();
    }

    /**
     * Route to the specified resource by id.
     *
     * @param context     the android context
     * @param id          the specified id
     * @param type        the specified type, see {@link ViewMapInfo}
     * @param paramMap    the passed parameters as Map
     * @param requestCode the request code
     */
    public void route(Context context, String id, int type, Map<String, Object> paramMap, Integer requestCode) {
        try {
            ViewMapInfo info = checkResource(context, id, type, paramMap);
            if (null != info) {
                goToResource(context, null, info, paramMap, requestCode);
            }
        } catch (EjuException e) {
            broadcastException(e);
        }
    }

    /**
     * Route to the specified resource by id.
     *
     * @param context the android context
     * @param id      the specified id
     * @param type    the specified type, see {@link ViewMapInfo}
     */
    public void route(Context context, String id, int type) {
        route(context, id, type, null, null);
    }

    /**
     * Route to the specified resource by id.
     *
     * @param context  the android context
     * @param id       the specified id
     * @param type     the specified type, see {@link ViewMapInfo}
     * @param paramMap the passed parameters as Map
     */
    public void route(Context context, String id, int type, Map<String, Object> paramMap) {
        route(context, id, type, paramMap, null);
    }

    /**
     * Route to the specified resource by id.
     *
     * @param fragment    the android fragment
     * @param id          the specified id
     * @param type        the specified type, see {@link ViewMapInfo}
     * @param paramMap    the passed parameters as Map
     * @param requestCode the request code
     */
    public void route(Fragment fragment, String id, int type, Map<String, Object> paramMap, Integer requestCode) {
        FragmentAdapter fragmentAdapter = FragmentAdapterFactory.create(fragment);
        internalRoute(fragmentAdapter, id, type, paramMap, requestCode);
    }

    /**
     * Route to the specified resource by id.
     *
     * @param fragment the android fragment
     * @param id       the specified id
     * @param type     the specified type, see {@link ViewMapInfo}
     * @param paramMap the passed parameters as Map
     */
    public void route(Fragment fragment, String id, int type, Map<String, Object> paramMap) {
        route(fragment, id, type, paramMap, null);
    }

    /**
     * Route to the specified resource by id.
     *
     * @param fragment    the support.v4 fragment
     * @param id          the specified id
     * @param type        the specified type, see {@link ViewMapInfo}
     * @param paramMap    the passed parameters as Map
     * @param requestCode the request code
     */
    public void route(android.support.v4.app.Fragment fragment, String id, int type, Map<String, Object> paramMap, Integer requestCode) {
        FragmentAdapter fragmentAdapter = FragmentAdapterFactory.create(fragment);
        internalRoute(fragmentAdapter, id, type, paramMap, requestCode);
    }

    /**
     * Route to the specified resource by id.
     *
     * @param fragment the support.v4 fragment
     * @param id       the specified id
     * @param type     the specified type, see {@link ViewMapInfo}
     * @param paramMap the passed parameters as Map
     */
    public void route(android.support.v4.app.Fragment fragment, String id, int type, Map<String, Object> paramMap) {
        route(fragment, id, type, paramMap, null);
    }

    /* package */ void internalRoute(Context context, URI uri) {
        ParamAdapter paramAdapter = null;
        Bundle bundle;
        try {
            String id = null;
            String query = uri.getQuery();
            if (!TextUtils.isEmpty(query)) {
                paramAdapter = new ParamAdapter();
                paramAdapter.setParam(
                        bundle = paramAdapter.fromUrl(query));
                id = bundle.getString("router_id");
            }

            if(TextUtils.isEmpty(id)) {
                throw new EjuException("no id in native scheme 'eju://'");
            }

            ViewMapInfo info = checkResource(context, id, ViewMapInfo.TYPE_UNSPECIFIED, null);
            if (null != info) {
                String resource = info.getResource();
                if (info.getType() == ViewMapInfo.TYPE_NATIVE) {
                    goToNative(context, null, resource, paramAdapter, null);
                } else {
//                    if (uri.getQuery() != null) {
//                        resource = resource + "?" + query;
//                    }
                    goToWeb(context, resource, paramAdapter);
                }
            }
        } catch (EjuException e) {
            broadcastException(e);
        }
    }

    private void internalRoute(FragmentAdapter fragmentAdapter, String id, int type, Map<String, Object> paramMap, Integer requestCode) {
        try {
            ViewMapInfo info = checkResource(fragmentAdapter.getActivity(), id, type, paramMap);
            if (null != info) {
                goToResource(fragmentAdapter.getActivity(), fragmentAdapter, info, paramMap, requestCode);
            }
        } catch (EjuException e) {
            broadcastException(e);
        }
    }

    /**
     * Find the fragment by id.
     *
     * @param context  the android context
     * @param id       the specified id
     * @param paramMap the passed parameters as Map
     * @return the found fragment or null
     */
    public Fragment findFragmentById(Context context, String id, Map<String, Object> paramMap) {
        try {
            return internalFindFragmentById(context, id, paramMap);
        } catch (EjuException e) {
            return null;
        }
    }

    /**
     * Find the support.v4 fragment by id.
     *
     * @param context  the android context
     * @param id       the specified id
     * @param paramMap the passed parameters as Map
     * @return the found support.v4 fragment or null
     */
    public android.support.v4.app.Fragment findSupportFragmentById(Context context, String id, Map<String, Object> paramMap) {
        try {
            return internalFindSupportFragmentById(context, id, paramMap);
        } catch (EjuException e) {
            return null;
        }
    }

    private Fragment internalFindFragmentById(Context context, String id, Map<String, Object> paramMap) throws EjuException {
        ViewMapInfo info = checkResource(context, id, ViewMapInfo.TYPE_NATIVE, paramMap);
        if (null == info) {
            throw new EjuException(EjuException.RESOURCE_NOT_FOUND, "Resource not found.");
        }
        try {
            Class<?> clazz = Class.forName(info.getResource());
            return (Fragment) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new EjuException(EjuException.RESOURCE_NOT_FOUND, "Resource not found.");
        } catch (ClassCastException e) {
            throw new EjuException(EjuException.ILLEGAL_PARAMETER, "resource should be an instance of android.support.v4.app.Fragment");
        } catch (Exception e) {
            throw new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage());
        }
    }

    private android.support.v4.app.Fragment internalFindSupportFragmentById(Context context, String id, Map<String, Object> paramMap) throws EjuException {
        ViewMapInfo info = checkResource(context, id, ViewMapInfo.TYPE_NATIVE, paramMap);
        if (null == info) {
            throw new EjuException(EjuException.RESOURCE_NOT_FOUND, "Resource not found.");
        }
        try {
            Class<?> clazz = Class.forName(info.getResource());
            return (android.support.v4.app.Fragment) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new EjuException(EjuException.RESOURCE_NOT_FOUND, "Resource not found.");
        } catch (ClassCastException e) {
            throw new EjuException(EjuException.ILLEGAL_PARAMETER, "resource should be an instance of android.support.v4.app.Fragment");
        } catch (Exception e) {
            throw new EjuException(EjuException.UNKNOWN_ERROR, e.getMessage());
        }
    }

    /* package for test */void prepareRemoteData(Context applicationContext) {
        mapManager = new ViewMapManager(applicationContext);
        Map<String, ViewMapInfo> mapLocal = mapManager.getViewMapLocal(applicationContext.getFilesDir().getPath());
        EjuLog.e(TAG, "读取本地数据: " + mapLocal);
        if (null != updateRequest) {
            mapManager.checkViewMapVersion(updateRequest, new ViewMapManager.DownLoadCallBack<ViewMap>() {
                @Override
                public void onError(EjuException e) {
                    broadcastException(e);
                }

                @Override
                public void onSuccess(ViewMap result) {
                    if (result != null) {
                        EjuLog.e(result.toString());
                        downloadViewMap(result);
                    } else {
                        EjuLog.e("最新版本，无需升级！");
                    }
                }
            });
        }

    }

    private void downloadViewMap(ViewMap result) {
        mapManager.downloadViewMap(result.downloadUrl, updateRequest.getMethod(), updateRequest.getHeaders(), updateRequest.getBody(), null, new ViewMapManager.DownLoadCallBack<File>() {
            @Override
            public void onError(EjuException e) {
                broadcastException(e);
            }

            @Override
            public void onSuccess(File file) {
                EjuLog.e(TAG, file == null ? "检查更新结果: 无需升级" : "检查更新结果: 更新配置文件成功");
            }
        });
    }

    private ViewMapInfo checkResource(Context context, String id, int type, Map<String, Object> paramMap) throws EjuException {
        checkNull(context, "context");
        checkNull(context, "id");

        ViewMapInfo info = mapManager.getViewMapInfo(id);
        if (null == info) {
            goTo404(context, paramMap);
            return null;
        }
        int typeFromViewMap = info.getType();
        String resource = info.getResource();

        // check from passed parameters
        boolean valid = isResourceValid(resource, type);
        int retType = type;

        // check from view map
        if (!valid) {
            valid = isResourceValid(resource, typeFromViewMap);
            retType = typeFromViewMap;
        }
        if (!valid) {
            goTo404(context, paramMap);
            return null;
        }
        return new ViewMapInfo(id, retType, resource, null);
    }

    private void goToResource(Context context,
                              FragmentAdapter fragmentAdapter,
                              ViewMapInfo info,
                              Map<String, Object> paramMap,
                              Integer requestCode) throws EjuException {
        ParamAdapter paramAdapter = null;
        if (null != paramMap && paramMap.size() > 0) {
            paramAdapter = new ParamAdapter();
            paramAdapter.setParam(paramMap);
        }
        switch (info.getType()) {
            case ViewMapInfo.TYPE_NATIVE:
                goToNative(context, fragmentAdapter, info.getResource(), paramAdapter, requestCode);
                break;
            case ViewMapInfo.TYPE_LOCAL_HTML:
            case ViewMapInfo.TYPE_REMOTE_HTML:
            case ViewMapInfo.TYPE_UNSPECIFIED:
                goToWeb(context, info.getResource(), paramAdapter);
                break;
            default:
                throw new EjuException(EjuException.ILLEGAL_PARAMETER, "type not found!");
        }
    }

    private void goToWeb(Context context, String resource, ParamAdapter paramAdapter) {
//        String url = resource;
        Bundle bundle = null;
        try {
            if (null != paramAdapter) {
//                if (url.contains("?")) {
//                    url += "&" + paramAdapter.toURL();
//                } else {
//                    url += "?" + paramAdapter.toURL();
//                }
                bundle = paramAdapter.toBundle();
            }
        } catch (EjuParamException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
//        intent.putExtra(WebViewActivity.EXTRA_URL, url);
        intent.putExtra(WebViewActivity.EXTRA_URL, resource);
        if(null != bundle) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    private boolean goToNative(Context context,
                               FragmentAdapter fragmentAdapter,
                               String className,
                               ParamAdapter paramAdapter,
                               Integer requestCode) throws EjuException {
        if (null == requestCode) {
            if (null != fragmentAdapter) {
                routerHandler.startActivityFromFragment(fragmentAdapter, className, paramAdapter);
            } else {
                routerHandler.startActivity(context, className, paramAdapter);
            }
        } else {
            if (null != fragmentAdapter) {
                routerHandler.startActivityForResultFromFragment(
                        fragmentAdapter, className, paramAdapter, requestCode);
            } else {
                routerHandler.startActivityForResult(context, className, paramAdapter, requestCode);
            }
        }
        return true;
    }

    private void goTo404(Context context, Map<String, Object> paramMap) throws EjuException {
        if (info404 != null) {
            goToNative(context, null, info404.getResource(), null, null);
            return;
        }
        throw new EjuException(EjuException.RESOURCE_NOT_FOUND, "Resource not found.");
    }

    private boolean isResourceValid(String resource, int type) {
        if (type == ViewMapInfo.TYPE_NATIVE) {
            try {
                Class.forName(resource);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return URLUtil.isValidUrl(resource);
    }

    /* package */ void broadcastException(EjuException e) {
        synchronized (handlers) {
            if (handlers.isEmpty()) {
                return;
            }
            for (Map.Entry<ExceptionHandler, ExceptionHandler> entry : handlers.entrySet()) {
                entry.getKey().handle(e);
            }
        }
    }

    /*package*/ boolean isNativeRouteSchema(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        }
        int len = nativeSchema.size();
        for (int i = 0; i < len; i++) {
            if (url.startsWith(nativeSchema.get(i) + "://")) {
                return true;
            }
        }
        return false;
    }

    /*package*/ RouterUrl getRouterUrlMatchUrl(String url) {
        if(!url.endsWith(".html")) {
            return null;
        }

        RouterUrl routerUrl = null;
        for (RouterUrl rUrl : urlList) {
            if(rUrl.isMatch(url)) {
                rUrl.setCurrentUrl(url);
                routerUrl = rUrl;
                break;
            }
        }
        return routerUrl;
    }

    private RouterUrl getUrlFromListById(String pattern) {
        RouterUrl routerUrl = null;
        for (RouterUrl url : urlList) {
            if(pattern.equalsIgnoreCase(url.getId())) {
                routerUrl = url;
                break;
            }
        }
        return routerUrl;
    }

    private void checkNull(Object object, String name) {
        if (null == object) {
            throw new RuntimeException(name + " is necessary.");
        }
    }
}
