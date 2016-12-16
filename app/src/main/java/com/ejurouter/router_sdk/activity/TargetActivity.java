package com.ejurouter.router_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ejurouter.router_sdk.R;

public class TargetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getClass().getSimpleName());

        final Intent intent = getIntent();
        StringBuilder builder = new StringBuilder();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            for (String key : bundle.keySet()) {
                builder.append(key);
                builder.append("=");
                builder.append(bundle.get(key));
                builder.append("\n");
            }
        }
        TextView tvArgs = (TextView) findViewById(R.id.tvArgs);
        tvArgs.setText(builder.toString());

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("foo", "bar");
                data.putExtra("x", 10);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
