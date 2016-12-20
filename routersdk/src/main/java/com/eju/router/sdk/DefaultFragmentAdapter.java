package com.eju.router.sdk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

/**
 * @author SidneyXu
 */
/* package */ class DefaultFragmentAdapter implements FragmentAdapter {

    private Fragment fragment;

    public DefaultFragmentAdapter(Fragment fragment) {
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