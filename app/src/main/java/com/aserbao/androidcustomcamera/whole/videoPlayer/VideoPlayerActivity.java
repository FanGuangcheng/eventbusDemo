package com.aserbao.androidcustomcamera.whole.videoPlayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fgc.eventbusdemo.R;

import com.aserbao.androidcustomcamera.whole.editVideo.view.PopTopTipWindow;
import com.aserbao.androidcustomcamera.whole.videoPlayer.view.FullScreenVideoView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

public class VideoPlayerActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.picture_close)
    ImageView pictureClose;
    @Nullable
    @BindView(R.id.meet_download)
    ImageView meetDownload;
    private String TAG = VideoPlayerActivity.class.getSimpleName();
    @Nullable
    @BindView(R.id.video_view)
    public FullScreenVideoView fullScreenVideoView;

    private String videoFilePath;

    @Nullable
    @BindView(R.id.timer)
    public Chronometer timer;
    private PopTopTipWindow popTopTipWindow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);
        initData();
    }


    protected void initData() {

        /***
         * 将播放器关联上一个音频或者视频文件
         * videoView.setVideoURI(Uri uri)
         * videoView.setVideoPath(String path)
         * 以上两个方法都可以。
         */
        videoFilePath = getIntent().getStringExtra("videoFilePath");

        Log.e(TAG, "videoFilePath=" + videoFilePath);

        fullScreenVideoView.setVideoPath(videoFilePath);

        /**
         * w为其提供一个控制器，控制其暂停、播放……等功能
         */
        fullScreenVideoView.setMediaController(new MediaController(this));

//        /**
//         * 视频循环播放
//         */
//        fullScreenVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//                mp.setLooping(true);
//            }
//        });

        /**
         * 视频或者音频到结尾时触发的方法
         */
        fullScreenVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("通知", "完成");
//                Toast.makeText(VideoPlayerActivity.this,"播放结束",Toast.LENGTH_LONG).show();
//                fullScreenVideoView.setVideoPath(videoFilePath);
                timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                int xiaoshi = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
                timer.setFormat("0"+ String.valueOf(xiaoshi)+":%s");
                timer.start();
                fullScreenVideoView.start();
            }
        });


        fullScreenVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("通知", "播放中出现错误");
                return false;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int xiaoshi = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0"+ String.valueOf(xiaoshi)+":%s");
        timer.start();
        fullScreenVideoView.start();
    }

    private int floatViewState = 4;



    @Override
    protected void onPause() {
        super.onPause();
        fullScreenVideoView.pause();
    }

    private boolean hasDownload = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Optional
    @OnClick({R.id.picture_close, R.id.meet_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture_close:
                finish();
                break;
            case R.id.meet_download:
                downloadVideoFile();
                break;
        }
    }

    private void downloadVideoFile() {
        hasDownload = true;
        popTopTipWindow = new PopTopTipWindow(VideoPlayerActivity.this, "保存成功");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                popTopTipWindow.dimss();
            }
        }, 2000);
        // 把视频文件插入到系统图库
        File outFile = new File(videoFilePath);
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), outFile.getAbsolutePath(), outFile.getName(), "");
        } catch (Exception e) {
            Log.e("FileUtils", "异常:" + e);
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + outFile.getAbsolutePath())));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!hasDownload) {
            File outFile = new File(videoFilePath);
            if (!outFile.exists()) {
                Log.e(TAG, "文件不存在");
            } else {
                outFile.delete();
            }
        }
    }

}