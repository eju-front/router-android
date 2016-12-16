package com.ejurouter.router_sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eju.router.sdk.Router;
import com.eju.router.sdk.ViewMapInfo;
import com.ejurouter.router_sdk.R;
import com.ejurouter.router_sdk.model.Address;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sidney on 2016/11/25.
 */

public class TargetFragment extends Fragment {

    public static final String TAG = TargetFragment.class.getSimpleName();

    public static final int REQUEST_PARAM = 20000;

    public static TargetFragment newInstance() {
        return new TargetFragment();
    }

    private Router router;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_target, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvArgs = (TextView) view.findViewById(R.id.tvArgs);
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("\n");
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            for (String key : bundle.keySet()) {
                builder.append(key);
                builder.append("=");
                builder.append(bundle.get(key));
            }
        }
        tvArgs.setText(builder.toString());

        router = Router.getInstance();

        final Map<String, Object> param = new HashMap<>();
        param.put("name", "Jane");
        param.put("age", 20);
        param.put("male", false);
        param.put("address", new Address("广中路10086号", 2121));
        view.findViewById(R.id.btnToActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                router.route(TargetFragment.this, "target", ViewMapInfo.TYPE_NATIVE, param, REQUEST_PARAM);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, String.format("onActivityResult() with requestCode=%s", requestCode));
        super.onActivityResult(requestCode, resultCode, data);
    }
}
