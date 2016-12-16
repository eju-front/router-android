package com.eju.router.sdk;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;

/**
 * Created by SidneyXu on 2016/12/01.
 */

public class BaseTestRunner extends RobolectricTestRunner {

    public BaseTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        AndroidManifest manifest = super.getAppManifest(config);
        FileFsFile assetFile = FileFsFile.from("src/test/assets");
        return new AndroidManifest(manifest.getAndroidManifestFile(),
                manifest.getResDirectory(),
                assetFile,
                manifest.getPackageName());
    }
}
