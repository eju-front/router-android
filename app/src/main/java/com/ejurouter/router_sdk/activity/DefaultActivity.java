package com.ejurouter.router_sdk.activity;

import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.TextView;

public class DefaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getClass().getSimpleName());
        TextView textView = new TextView(this);
        textView.setText("404");
        textView.setTextSize(Dimension.SP, 32);
        textView.setGravity(Gravity.CENTER);
        setContentView(textView);
    }
}
