package com.ejurouter.router_sdk.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.FrameLayout;

import com.ejurouter.router_sdk.fragment.TargetFragment;

public class TargetFragmentActivity extends Activity {

    public static final String TAG_FRAGMENT = "fragment";

    @IdRes
    private int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(id);
        setContentView(frameLayout);
        FragmentManager fragmentManager = getFragmentManager();
        TargetFragment fragment = (TargetFragment) fragmentManager.findFragmentByTag(TAG_FRAGMENT);
        if (null == fragment) {
            fragment = TargetFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(id, fragment, TAG_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
