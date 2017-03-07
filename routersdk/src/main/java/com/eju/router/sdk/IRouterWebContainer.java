package com.eju.router.sdk;

import android.content.Intent;
import android.os.Bundle;


/**
 * class description
 *
 * @author tangqianwei
 */
public interface IRouterWebContainer {

    void onCreate(Bundle onSavedInstanceState);

    void onResume();

    void onNewIntent(Intent intent);

    void onBackPressed();

    void loadUrl(String url);
}
