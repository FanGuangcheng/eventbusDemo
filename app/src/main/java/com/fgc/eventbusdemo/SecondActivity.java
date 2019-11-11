package com.fgc.eventbusdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    TextView tv_post_event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        tv_post_event = findViewById(R.id.tv_post_event);
        tv_post_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MessageEvent("这是event bus 传过来的东西~"));
                Log.d(TAG, "event bus post message~! ");
            }
        });
    }
}
