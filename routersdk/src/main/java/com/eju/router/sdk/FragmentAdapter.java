package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by SidneyXu on 2016/11/29.
 */

/* package */ interface FragmentAdapter {

    Activity getActivity();

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);

}
