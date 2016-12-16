package com.eju.router.sdk;

import android.app.Fragment;

/**
 * Created by SidneyXu on 2016/11/29.
 */

/* package */class FragmentAdapterFactory {

    public static FragmentAdapter create(Fragment fragment) {
        return new DefaultFragmentAdapter(fragment);
    }

    public static FragmentAdapter create(android.support.v4.app.Fragment fragment) {
        return new SupportFragmentAdapter(fragment);
    }
}
