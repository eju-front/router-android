package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by SidneyXu on 2016/11/29.
 */

/* package */ interface FragmentAdapter {
    /**
     * get the current Activity of the Fragment.
     * @return the Activity or null if the Fragment was detached
     */
    Activity getActivity();

    /**
     * startActivity with the intent.
     * @param intent jump intent
     */
    void startActivity(Intent intent);

    /**
     * startActivityForResult
     * @param intent jump intent
     * @param requestCode request code to callback
     */
    void startActivityForResult(Intent intent, int requestCode);

}
