package com.eju.router.sdk;

import android.app.Activity;
import android.content.Intent;

/**
 * @author SidneyXu
 */
/*package*/ class SupportFragmentAdapter implements FragmentAdapter {

    private android.support.v4.app.Fragment fragment;

    SupportFragmentAdapter(android.support.v4.app.Fragment fragment) {
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