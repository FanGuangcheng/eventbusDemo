package com.fgc.eventbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aserbao.androidcustomcamera.WelcomeActivity;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.simple.SimpleLocalVideoActivity;
import com.aserbao.androidcustomcamera.simple.VideoFrameActivity;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.LocalVideoActivity;
import com.aserbao.androidcustomcamera.whole.record.RecorderActivity;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
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
//                gotoWelcomeActivity();
                gotoSimpleLocalVideoActivity();
//                exportVideo();
                break;
        }
    }

    // ffmpeg导出视频，一直失败
    private void exportVideo() {


        //            ffmpeg -i /storage/emulated/0/aserbaoCamera/1723032638188.mp4 -ss 00:00:05.000 -to 00:00:08.000 -c copy /storage/emulated/0/aserbaoCamera/clip_1723032638188.mp4

//        -y -ss 0 -t 5 -accurate_seek -i /storage/emulated/0/DCIM/Camera/VID_20240807_190411.mp4 -filter_complex vflip,hflip -preset ultrafast /storage/emulated/0/aserbaoCamera/1726305386001.mp4
//        -y -ss -ss 00:00:05.000 -to 00:00:08.000 -accurate_seek -i /storage/emulated/0/aserbaoCamera/1723032638188.mp4 -filter_complex vflip,hflip -preset ultrafast /storage/emulated/0/aserbaoCamera/clip_1723032638188.mp4
//        -y -ss -ss 5 -t 8 -accurate_seek -i /storage/emulated/0/aserbaoCamera/1723032638188.mp4 -filter_complex vflip,hflip -preset ultrafast /storage/emulated/0/aserbaoCamera/clip_1723032638188.mp4

        EpEditor.execCmd("-y -ss 0 -t 5 -accurate_seek -i /storage/emulated/0/DCIM/Camera/VID_20240807_190411.mp4 -filter_complex vflip,hflip -preset ultrafast /storage/emulated/0/aserbaoCamera/1726305386001.mp4", 0, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.e("fgcfgc", "onSuccess");
            }

            @Override
            public void onFailure() {
                Log.e("fgcfgc", "onFailure");

            }

            @Override
            public void onProgress(float v) {
                Log.e("fgcfgc", "onProgress:" + v);

            }
        });
    }

    private void gotoWelcomeActivity() {
        Toast.makeText(MainActivity.this, "dada11", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void gotoSimpleLocalVideoActivity() {
        //  视频帧快速浏览
//        Intent intent = new Intent(MainActivity.this, VideoFrameActivity.class);
        // 使用ffmpeg方法生成帧图片
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
