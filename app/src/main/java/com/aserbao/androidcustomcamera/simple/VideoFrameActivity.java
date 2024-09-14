package com.aserbao.androidcustomcamera.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aserbao.androidcustomcamera.utils.PreviewImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.fgc.eventbusdemo.R;

import java.io.File;

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
                mTvVideoTime.setText(":" + progress);

                int durationSecond = PreviewImageUtils.getInstance().mDuration <= 0 ? 1 : PreviewImageUtils.getInstance().mDuration;
                float count = 100 / (float)durationSecond;

                int index = (int) (progress / count);
                if (index == durationSecond) {
                    index = durationSecond - 1;
                }

                Log.d("fgcfgc", "index:" + index + "，progress：" + progress +"，count：" + count);

//                File file = new File("/storage/emulated/0/aserbaoFrames/frames/saveImg/frame_/" + index + ".jpg");
                File file = new File("/storage/emulated/0/Android/data/com.chinablue.tv/cache/video_frame_cache/1723032638188.mp4/" + index + ".jpg");

//                Glide.with(VideoFrameActivity.this)
//                        .load(file)
//                        .into(mIvVideoFrame);


                Glide.with(VideoFrameActivity.this)
                        .load(file)
                        .asBitmap()  // 如果需要加载为 Bitmap
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                // 加载成功时的回调
                                mIvVideoFrame.setImageBitmap(resource);
                                Log.d("fgcfgc", "Image load successful");
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                // 加载失败时的回调
                                Log.e("fgcfgc", "Image load failed", e);
                            }
                        });
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
