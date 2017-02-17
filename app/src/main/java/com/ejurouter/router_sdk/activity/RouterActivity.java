package com.ejurouter.router_sdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.eju.router.sdk.ExceptionHandler;
import com.eju.router.sdk.Router;
import com.eju.router.sdk.ViewMapInfo;
import com.eju.router.sdk.exception.EjuException;
import com.ejurouter.router_sdk.R;
import com.ejurouter.router_sdk.model.Address;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sidney on 2016/11/25.
 */

public class RouterActivity extends AppCompatActivity {

    public static final String TAG = RouterActivity.class.getSimpleName();

    public static final int REQUEST_PARAMETER = 1000;

    private Context context;
    private Router router;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);

        context = this;
        router = Router.getInstance();

        final Map<String, Object> param = new HashMap<>();
        param.put("name", "Peter");
        param.put("age", 18);
        param.put("male", true);
//        param.put("address", new Address("广中路111号", 1901));

        findViewById(R.id.btnToActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                router.route(context, "target", ViewMapInfo.TYPE_NATIVE, param, REQUEST_PARAMETER);
            }
        });

        findViewById(R.id.btnTo404).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                router.route(context, "Not exist page", ViewMapInfo.TYPE_NATIVE, param);
            }
        });
        findViewById(R.id.btnHandleException).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExceptionHandler exceptionHandler = new ExceptionHandler() {
                    @Override
                    public void handle(EjuException e) {
                        alert("发生异常", e.toString());
                    }
                };
                router.register(exceptionHandler);
                router.set404ViewMap(null);
                router.route(context, "Not exist page", ViewMapInfo.TYPE_NATIVE);
                router.set404ViewMap(DefaultActivity.class.getName());
                router.unregister(exceptionHandler);
            }
        });
        findViewById(R.id.btnToFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = router.findFragmentById(context, "fragment", param);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "fragment")
                        .commit();
            }
        });
        findViewById(R.id.btnToSupportFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.Fragment fragment = router.findSupportFragmentById(context, "supportFragment", param);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "supportFragment")
                        .commit();
            }
        });
        findViewById(R.id.btnToRemoteH5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                router.route(context, "remote", ViewMapInfo.TYPE_REMOTE_HTML, param);
            }
        });
        findViewById(R.id.btnToLocalH5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                router.route(context, "local1", ViewMapInfo.TYPE_LOCAL_HTML, param);
            }
        });
        findViewById(R.id.btnSetResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("message", "From Activity");
                data.putExtra("x", 1);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, String.format("onActivityResult() with requestCode=%s", requestCode));
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (null != data) {
            Bundle bundle = data.getExtras();
            StringBuilder builder = new StringBuilder();
            if (null != bundle) {
                for (String key : bundle.keySet()) {
                    builder.append(key)
                            .append("=")
                            .append(bundle.get(key))
                            .append(", ");
                }
                System.out.println(builder.toString());
                alert("Receive Result", builder.toString());
            }
        }
    }

    private void alert(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }
}
