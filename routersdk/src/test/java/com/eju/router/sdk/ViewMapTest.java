package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;
import com.eju.router.sdk.exception.EjuTimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@RunWith(BaseTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE)
public class ViewMapTest extends BaseTest {

    //更新的URL
    private static final String URL = "https://raw.githubusercontent.com/lovejjfg/screenshort/master/myjson.json";
    private ViewMapManager mapManager;
    private ViewMapManager.DownLoadCallBack<ViewMap> viewMapCallBack;
    private ViewMapManager.DownLoadCallBack<File> fileCallBack;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        mapManager = Mockito.mock(ViewMapManager.class);
        viewMapCallBack = new ViewMapManager.DownLoadCallBack<ViewMap>() {
            @Override
            public void onError(EjuException e) {
                assertNotNull(e);
            }

            @Override
            public void onSuccess(ViewMap viewMap) {
                assertNotNull(viewMap);
                assertNotNull(viewMap.downloadUrl);
            }
        };

        fileCallBack = new ViewMapManager.DownLoadCallBack<File>() {
            @Override
            public void onError(EjuException e) {
                fail("fail::", e);
            }

            @Override
            public void onSuccess(File file) {
                assertThat(file.exists() && file.length() > 0).isTrue();
            }
        };
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testDownload() throws Exception {
        mapManager.downloadViewMap(URL, EjuRequest.METHOD_GET, new HashMap<String, String>(), null, System.getProperty("user.dir"), fileCallBack);
        fileCallBack.onSuccess(new File("src/test/assets/viewmap.json"));

    }

    @Test
    public void testLoadLocal() throws Exception {
        Map<String, ViewMapInfo> viewMapLocal = mapManager.getViewMapLocal(System.getProperty("user.dir"));
        assertNotNull(viewMapLocal);
        System.out.println(viewMapLocal);

    }

    @Test
    public void testPutVersion() throws Exception {
        PrefUtil.putString(application, "ViewMapVersion", "V1.0.1");
        String version = PrefUtil.getString(application, "ViewMapVersion", "V1.0.0");
        assertEquals("V1.0.1", version);
    }


    @Test
    public void testCheckVersionWithRequest() throws Exception {
        EjuRequest request = new EjuRequest("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android", EjuRequest.METHOD_GET, null, null);
        mapManager.checkViewMapVersion(request, viewMapCallBack);
        viewMapCallBack.onSuccess(new ViewMap("http://172.29.32.215:10086/app/downloadViewMap/584f915bb66877152c77ecf8", "ef7bcd33d199af2aa79b36ddbc88f065", "V1.0.4"));
    }

    @Test
    public void testCheckVersionErrorWithRequest() throws Exception {
        EjuRequest request = new EjuRequest("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android", EjuRequest.METHOD_GET, null, null);
        mapManager.checkViewMapVersion(request, viewMapCallBack);
        viewMapCallBack.onError(new EjuTimeoutException());
    }

    @Test
    public void testCheckFileMD5() throws Exception {
        InputStream inputStream = application.getAssets().open("viewmap.json");
        File dst = new File(System.getProperty("user.dir"), "test11.json");
        File dst2 = new File(System.getProperty("user.dir"), "test222.json");
        IOUtil.copy(inputStream, dst);
        inputStream = application.getAssets().open("viewmap.json");
        IOUtil.copy(inputStream, dst2);
        String fileMD51 = IOUtil.getFileMD5(dst);
        String fileMD52 = IOUtil.getFileMD5(dst2);
        assertEquals(fileMD51, fileMD52);
        System.out.println(fileMD51);
        System.out.println(fileMD52);
        assertTrue(dst.delete());
        assertTrue(dst2.delete());
    }

    @Test
    public void testCompareVersion() throws Exception {
        String defaultStr = "V110";//100;
        String updateStr = "v100";//101
        int i = VersionUtil.versionCompare(defaultStr, updateStr, true);
        if (i < 0) {
            System.out.println(updateStr);
        } else if (i > 0) {
            System.out.println(defaultStr);
        } else {
            System.out.println("一样大！");
        }

    }


}
