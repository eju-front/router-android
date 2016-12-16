package com.ejurouter.router_sdk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ejurouter.router_sdk.R;

/**
 * Created by Sidney on 2016/11/25.
 */

public class SupportTargetFragment extends Fragment {

    public static SupportTargetFragment newInstance() {
        return new SupportTargetFragment();
    }

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
