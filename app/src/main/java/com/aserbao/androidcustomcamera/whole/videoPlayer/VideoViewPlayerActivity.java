package com.aserbao.androidcustomcamera.whole.videoPlayer;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.VideoView;

import com.fgc.eventbusdemo.R;

import com.aserbao.androidcustomcamera.base.activity.BaseActivity;

import butterknife.BindView;

import static com.aserbao.androidcustomcamera.blocks.mediaCodec.recordCamera.utils.FileUtils.VIDEO_PATH;

import androidx.annotation.Nullable;

public class VideoViewPlayerActivity extends BaseActivity {


    @Nullable
    @BindView(R.id.video_player_vv)
    VideoView mVideoPlayerVv;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_video_view_player;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayerVv.resume();
        mVideoPlayerVv = null;
    }

    public void initView(){
        String stringExtra = getIntent().getStringExtra(VIDEO_PATH);
        if (TextUtils.isEmpty(stringExtra)) {
            Toast.makeText(this, "文本路径错误", Toast.LENGTH_SHORT).show();
        }else {
            mVideoPlayerVv.setVideoPath(stringExtra);
            mVideoPlayerVv.start();
        }
    }

    public static void launch(Activity activity, String videoPath) {
        Intent intent = new Intent(activity, VideoViewPlayerActivity.class);
        intent.putExtra(VIDEO_PATH, videoPath);
        activity.startActivity(intent);
    }
}