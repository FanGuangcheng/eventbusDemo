package com.aserbao.androidcustomcamera.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aserbao.androidcustomcamera.utils.PreviewImageUtils;
import com.fgc.eventbusdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 参考文档：引入 FFmpegMediaMetadataRetriever第三库，可以快速浏览帧数据
 * https://github.com/wseemann/FFmpegMediaMetadataRetriever
 *
 * https://blog.csdn.net/heshuangqiang/article/details/126909002
 */

public class VideoFrameActivity extends AppCompatActivity {
//    @Nullable
//    @BindView(R.id.iv_video_frame)
    ImageView mIvVideoFrame;
//    @Nullable
//    @BindView(R.id.tv_video_time)
    TextView mTvVideoTime;
//    @Nullable
//    @BindView(R.id.seek_progress)
    SeekBar mSeekProgress;
    // huawei 手机video
//    private final static String inputVideoPath = "/storage/emulated/0/DCIM/Camera/VID_20240503_161251.mp4";
//    private final static String inputVideoPath = "/storage/emulated/0/DCIM/Camera/VID_20240728_193832.mp4";

    // 小米手机video
    private final static String inputVideoPath = "/storage/emulated/0/aserbaoCamera/1723032638188.mp4";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_frame);
        ButterKnife.bind(this);

        mIvVideoFrame = findViewById(R.id.iv_video_frame);
        mTvVideoTime = findViewById(R.id.tv_video_time);
        mSeekProgress = findViewById(R.id.seek_progress);

        PreviewImageUtils.getInstance().setStop(true);
        PreviewImageUtils.getInstance().startDecode(null, inputVideoPath);

        init();
    }
    private void init() {
        final int duration = PreviewImageUtils.getInstance().getVideoDuration(inputVideoPath);

        mSeekProgress.setMax(100);
        mTvVideoTime.setText(":" + duration);
        mSeekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float prosssss = progress * 1.0f / 100;
                Log.d("fgcfgc", "sonProgressChanged start:");

                mTvVideoTime.setText(":" + progress);
                Bitmap bitmap = PreviewImageUtils.getInstance().getFrameImgAtTime(inputVideoPath, (long) (prosssss * duration * 1000));
                if (bitmap != null) {
                    Log.d("fgcfgc", "set bitmap success:");

                    mIvVideoFrame.setImageBitmap(bitmap);
                } else {
                    Log.d("fgcfgc", "set bitmap null:");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
