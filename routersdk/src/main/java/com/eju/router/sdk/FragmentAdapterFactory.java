package com.eju.router.sdk;

import android.app.Fragment;

/**
 * the Factory to create FragmentAdapter
 *
 * @author SidneyXu
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
