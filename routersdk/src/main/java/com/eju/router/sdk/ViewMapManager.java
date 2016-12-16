package com.eju.router.sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.eju.router.sdk.exception.EjuException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe on 2016/11/30.
 * Email lovejjfg@gmail.com
 */

/**
 * 1、核对版本确认是否需要下载更新ViewMap文件
 * <p>
 * 2、下载文件到指定的路径 名称内部确定
 * <p>
 * 3、替换之前的文件
 * <p>
 * 4、解析最新的ViewMapInfo\
 */
/*package*/ class ViewMapManager {
    private static final String TAG = ViewMapManager.class.getSimpleName();
    //指定文件的名称
    private static final String VIEWMAP = "viewmap.json";
    //SP对应的版本名称
    private static final String VIEWMAP_VERSION = "ViewMapVersion";
    //对象名称
    private static final String MAPINFOS = "mapinfos";
    //id字段
    private static final String ID = "id";
    //description字段
    private static final String DESCRIPTION = "description";
    //type字段
    private static final String TYPE = "type";//TYPE_UNSPECIFIED;
    //resource字段
    private static final String RESOURCE = "resource";

    //默认版本
    static String DEFAULT_VERSION = "V1.0.0";
    //viewmap更新链接
    private static final String DOWNLOAD_URL = "downloadUrl";
    //viewmap文件md5
    private static final String MD5 = "md5";
    //viewmap更新版本
    private static final String VERSION = "version";


    private final Map<String, ViewMapInfo> viewMapInfos = new HashMap<>();
    private final Context context;


    ViewMapManager(Context context) {
        this.context = context;
    }

    @Nullable
    ViewMapInfo getViewMapInfo(String id) {
        if (viewMapInfos.containsKey(id)) {
            return viewMapInfos.get(id);
        }
        return null;
    }


    /**
     * In fact,Maybe checkVersion at first ,so you should call {@link #checkViewMapVersion(EjuRequest, DownLoadCallBack)}
     */
    void downloadViewMap(@NonNull String url, @Method int method, @NonNull Map<String, String> headers, @Nullable byte[] body, @Nullable String parentPath, @Nullable DownLoadCallBack<File> callBack) {
        checkUrlValid(url);
        System.out.println("根路径：" + parentPath);
        Log.e(TAG, "downloadViewMap: 根路径：" + parentPath);
        EjuRequest request = new EjuRequest(url, method, headers, body);
//        EjuRequest request = EjuRequest.newBuilder().url(url).addHeaders(headers).method(method).body(null).build();
        MapInfoTask task = new MapInfoTask(context, new ViewMap(url, "", "x"), request, TextUtils.isEmpty(parentPath) ? context.getFilesDir().getPath() : parentPath, callBack);
        task.execute();

    }

    void checkViewMapVersion(EjuRequest request, @Nullable DownLoadCallBack<ViewMap> callBack) {
        checkUrlValid(request.getUrl());
        if (request.getHeaders() == null) {
            request.setHeaders(new HashMap<String, String>());
        }
        CheckStateTask task = new CheckStateTask(context, request, callBack);
        task.execute();

    }

    private static void checkUrlValid(String url) {
        if (!URLUtil.isValidUrl(url)) {
            throw new IllegalArgumentException("Please enter the legitimate url!");
        }
    }

    //call this method to get the Map of viewMaps.
    Map<String, ViewMapInfo> getViewMapLocal(@Nullable String parentPath) {
        parentPath = TextUtils.isEmpty(parentPath) ? context.getFilesDir().getPath() : parentPath;
        return parseViewMap(new File(parentPath, VIEWMAP));
    }

    private static class MapInfoTask extends AsyncTask<Void, Void, File> {
        private final String parentPath;
        private final DownLoadCallBack<File> callBack;
        private final Context context;
        private final ViewMap viewmap;
        private final EjuRequest request;


        MapInfoTask(Context context, ViewMap viewMap, EjuRequest request, @Nullable String parentPath, DownLoadCallBack<File> callBack) {
            this.context = context;
            this.request = request;
            this.parentPath = parentPath;
            this.viewmap = viewMap;
            this.callBack = callBack;

        }

        @SuppressWarnings({"ResultOfMethodCallIgnored"})
        @Override
        protected final File doInBackground(Void... params) {
//            EjuRequest request = new EjuRequest(viewmap.downloadUrl, method, headers, body);
            EjuHttpClient client = EjuHttpClient.newClient(5000);
            FileOutputStream os = null;
            File file = new File(context.getCacheDir(), VIEWMAP);
            File desFile = new File(parentPath, VIEWMAP);
            if (file.exists() && file.length() > 0) {
                file.delete();
            }
            try {
                //1、下载文件
                os = execute(request, client, file, desFile);
                if (os == null) return null;
                //2、复制文件
                IOUtil.copy(file, desFile);
            } catch (EjuException e) {
                Log.e(TAG, "doInBackground: ", e);
                if (callBack != null) {
                    callBack.onError(e);
                }
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: ", e);
                if (callBack != null) {
                    callBack.onError(new EjuException(e));
                }
            } finally {
                IOUtil.close(os);
            }
            PrefUtil.putString(context, VIEWMAP_VERSION, viewmap.version);
//            System.out.println("升级完成:" + viewmap.version);
            return desFile;
        }

        @Nullable
        private FileOutputStream execute(EjuRequest request, EjuHttpClient client, File file, File desFile) throws EjuException, IOException {
            EjuResponse execute = client.execute(request);
            if (!execute.isSuccessful()) {
                return null;
            }
            byte[] body = execute.getBody();
            FileOutputStream os = new FileOutputStream(file);
            os.write(body);
            String fileMD5 = IOUtil.getFileMD5(file);
            if (!TextUtils.isEmpty(viewmap.md5) && !TextUtils.equals(fileMD5, viewmap.md5)) {
                return null;
            }
            if (desFile.exists() && desFile.length() > 0) {
                if (TextUtils.equals(IOUtil.getFileMD5(desFile), fileMD5)) {
                    return null;
                }
            }
            return os;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (callBack != null) {
                callBack.onSuccess(file);
            }
        }
    }

    @Nullable
    private Map<String, ViewMapInfo> parseViewMap(File desFile) {
        checkNull(desFile);
        try {
            String jsonFile = IOUtil.readText(new FileInputStream(desFile));
            JSONObject jsonObject = new JSONObject(jsonFile);
            JSONArray array = jsonObject.getJSONArray(MAPINFOS);
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString(ID);
                    int type = object.getInt(TYPE);
                    String resource = object.getString(RESOURCE);
                    String description = object.getString(DESCRIPTION);
                    ViewMapInfo info = new ViewMapInfo(id, type, resource, description);
                    viewMapInfos.put(id, info);
                }
                return viewMapInfos;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "parseViewMap: ", e);
        }
        return null;
    }

    private void checkNull(File desFile) {
        if (!desFile.exists() || desFile.length() == 0) {
            try {
                InputStream inputStream = context.getAssets().open(VIEWMAP);
                IOUtil.copy(inputStream, desFile);
            } catch (IOException e) {
                Log.e(TAG, "parseViewMap: ", e);
            }
        }
    }


    interface DownLoadCallBack<Result> {

        void onError(EjuException e);

        /**
         * return the file which downloaded,null current is the latest.
         */
        void onSuccess(Result result);

    }

    private static class CheckStateTask extends AsyncTask<Void, Void, ViewMap> {
        private final DownLoadCallBack<ViewMap> callBack;
        private final Context context;
        private final EjuRequest request;


        CheckStateTask(Context context, @NonNull EjuRequest request, DownLoadCallBack<ViewMap> callBack) {
            this.context = context;
            this.callBack = callBack;
            this.request = request;
        }

        @Override
        protected ViewMap doInBackground(Void... params) {
            try {
                String viewMapVersion = PrefUtil.getString(context, VIEWMAP_VERSION, DEFAULT_VERSION);
                EjuHttpClient client = EjuHttpClient.newClient(5000);
                EjuResponse execute = client.execute(request);
                if (execute.isSuccessful()) {
                    JSONObject object = new JSONObject(execute.getBodyAsString());
                    String version = object.getString(VERSION);
                    String url = object.getString(DOWNLOAD_URL);
                    String md5 = object.getString(MD5);
                    int compare = VersionUtil.versionCompare(viewMapVersion, version, true);//本地的和线上的比较
                    if (compare < 0) {//需要更新
                        return new ViewMap(url, md5, version);
                    }
                }
            } catch (EjuException e) {
                if (callBack != null) {
                    callBack.onError(e);
                }
                e.printStackTrace();
            } catch (JSONException e) {
                if (callBack != null) {
                    callBack.onError(new EjuException(e));
                }
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ViewMap viewMap) {
            callBack.onSuccess(viewMap);
        }
    }


}
