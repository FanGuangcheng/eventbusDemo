package com.fgc.eventbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aserbao.androidcustomcamera.WelcomeActivity;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.simple.SimpleLocalVideoActivity;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.LocalVideoActivity;
import com.aserbao.androidcustomcamera.whole.record.RecorderActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


public class MainActivity extends AppCompatActivity {
    @Nullable
    @BindView(R.id.btn_click)
    Button btnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);////
        ButterKnife.bind(this);
//        btnClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "dada", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, WelcomeActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Optional
    @OnClick({R.id.btn_click})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_click:
                gotoSimpleLocalVideoActivity();
                break;
        }
    }

    private void gotoWelcomeActivity() {
        Toast.makeText(MainActivity.this, "dada11", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void gotoSimpleLocalVideoActivity() {
        Intent intent = new Intent(MainActivity.this, SimpleLocalVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(StaticFinalValues.MISNOTCOMELOCAL, 0);
        intent.putExtra(StaticFinalValues.BUNDLE, bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
