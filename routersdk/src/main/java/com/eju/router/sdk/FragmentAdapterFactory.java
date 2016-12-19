package com.eju.router.sdk;

import android.app.Fragment;

/**
 * Created by SidneyXu on 2016/11/29.
 */

/**
 * the Factory to create FragmentAdapter
 */
/* package */class FragmentAdapterFactory {
    /**
     * create FragmentAdapter
     * @param fragment fragment
     * @return FragmentAdapter
     */
    public static FragmentAdapter create(Fragment fragment) {
        return new DefaultFragmentAdapter(fragment);
    }

    /**
     * create support FragmentAdapter
     * @param fragment supportFragment
     * @return FragmentAdapter
     */
    public static FragmentAdapter create(android.support.v4.app.Fragment fragment) {
        return new SupportFragmentAdapter(fragment);
    }
}
