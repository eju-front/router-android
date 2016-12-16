package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by SidneyXu on 2016/11/29.
 */
/* package */class SupportFragmentAdapter implements FragmentAdapter {

    private android.support.v4.app.Fragment fragment;

    public SupportFragmentAdapter(android.support.v4.app.Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public Activity getActivity() {
        return fragment.getActivity();
    }

    @Override
    public void startActivity(Intent intent) {
        fragment.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }
}