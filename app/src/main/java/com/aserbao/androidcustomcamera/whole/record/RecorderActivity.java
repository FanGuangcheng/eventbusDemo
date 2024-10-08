package com.aserbao.androidcustomcamera.whole.record;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fgc.eventbusdemo.R;

//import com.aserbao.androidcustomcamera.WelcomeActivity;
import com.aserbao.androidcustomcamera.base.MyApplication;
import com.aserbao.androidcustomcamera.base.activity.BaseActivity;
import com.aserbao.androidcustomcamera.base.pop.PopupManager;
import com.aserbao.androidcustomcamera.base.utils.FileUtils;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.LocalVideoActivity;
import com.aserbao.androidcustomcamera.whole.pickvideo.VideoPickActivity;
import com.aserbao.androidcustomcamera.whole.pickvideo.beans.VideoFile;
import com.aserbao.androidcustomcamera.whole.record.beans.MediaObject;
import com.aserbao.androidcustomcamera.whole.record.other.MagicFilterType;
import com.aserbao.androidcustomcamera.whole.record.ui.CameraView;
import com.aserbao.androidcustomcamera.whole.record.ui.CustomRecordImageView;
import com.aserbao.androidcustomcamera.whole.record.ui.FocusImageView;
import com.aserbao.androidcustomcamera.whole.record.ui.ProgressView;
import com.aserbao.androidcustomcamera.whole.record.ui.SlideGpuFilterGroup;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoPlayerActivity2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.CHANGE_IMAGE;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.DELAY_DETAL;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.MAX_NUMBER;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.OVER_CLICK;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.RECORD_MIN_TIME;
import static com.aserbao.androidcustomcamera.whole.pickvideo.BaseActivity.IS_NEED_FOLDER_LIST;
import static com.aserbao.androidcustomcamera.whole.pickvideo.VideoPickActivity.IS_NEED_CAMERA;

import androidx.annotation.Nullable;

public class RecorderActivity extends BaseActivity implements View.OnTouchListener, SlideGpuFilterGroup.OnFilterChangeListener {
    private static final int VIDEO_MAX_TIME = 30 * 1000;
    @Nullable
    @BindView(R.id.record_camera_view)
    CameraView mRecordCameraView;
    @Nullable
    @BindView(R.id.video_record_progress_view)
    ProgressView mVideoRecordProgressView;
    @Nullable
    @BindView(R.id.matching_back)
    LinearLayout mMatchingBack;
    @Nullable
    @BindView(R.id.video_record_finish_iv)
    Button mVideoRecordFinishIv;
    @Nullable
    @BindView(R.id.switch_camera)
    ImageView mMeetCamera;
    @Nullable
    @BindView(R.id.index_delete)
    LinearLayout mIndexDelete;
    @Nullable
    @BindView(R.id.index_album)
    TextView mIndexAlbum;
    @Nullable
    @BindView(R.id.custom_record_image_view)
    CustomRecordImageView mCustomRecordImageView;
    @Nullable
    @BindView(R.id.count_down_tv)
    TextView mCountDownTv;
    @Nullable
    @BindView(R.id.record_btn_ll)
    FrameLayout mRecordBtnLl;
    @Nullable
    @BindView(R.id.meet_mask)
    ImageView mMeetMask;
    @Nullable
    @BindView(R.id.video_filter)
    ImageView mVideoFilter;
    @Nullable
    @BindView(R.id.recorder_focus_iv)
    FocusImageView mRecorderFocusIv;
    @Nullable
    @BindView(R.id.count_time_down_iv)
    ImageView mCountTimeDownIv;
    public int mNum = 0;
    private long mLastTime = 0;
    public float mRecordTimeInterval;
    ExecutorService executorService;
    private MediaObject mMediaObject;

    private MyHandler mMyHandler =new MyHandler(this);
    private boolean isRecording = false;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_recorder;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoRecordProgressView.setData(mMediaObject);
    }

    @Override
    public void initView() {
        if (mMediaObject == null) {
            mMediaObject = new MediaObject();
        }
        executorService = Executors.newSingleThreadExecutor();
        mRecordCameraView.setOnTouchListener(this);
        mRecordCameraView.setOnFilterChangeListener(this);
        mVideoRecordProgressView.setMaxDuration(VIDEO_MAX_TIME, false);
        mVideoRecordProgressView.setOverTimeClickListener(new ProgressView.OverTimeClickListener() {
            @Override
            public void overTime() {
                mCustomRecordImageView.performClick();
            }

            @Override
            public void noEnoughTime() {
                setBackAlpha(mVideoRecordFinishIv,255);
            }

            @Override
            public void isArriveCountDown() {
                mCustomRecordImageView.performClick();
            }
        });
        setBackAlpha(mVideoRecordFinishIv,127);
    }

    @Optional
    @OnClick({R.id.matching_back, R.id.video_record_finish_iv, R.id.switch_camera,  R.id.index_delete, R.id.index_album, R.id.custom_record_image_view, R.id.count_down_tv, R.id.meet_mask, R.id.video_filter})
    public void onViewClicked(View view) {
        if (System.currentTimeMillis() - mLastTime < 500) {
            return;
        }
        mLastTime = System.currentTimeMillis();
        if (view.getId() != R.id.index_delete) {
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        if (mVideoRecordProgressView != null)
                            mVideoRecordProgressView.invalidate();
                    }
                }
            }
        }
        switch (view.getId()) {
            case R.id.matching_back:
                onBackPressed();
                break;
            case R.id.video_record_finish_iv:
                onStopRecording();
                if (mMediaObject != null) {
                    videoFileName = mMediaObject.mergeVideo();
                }
                VideoPlayerActivity2.launch(RecorderActivity.this,videoFileName);
                break;
            case R.id.switch_camera:
                mRecordCameraView.switchCamera();
                if (mRecordCameraView.getCameraId() == 1){
                    mRecordCameraView.changeBeautyLevel(3);
                }else {
                    mRecordCameraView.changeBeautyLevel(0);
                }
                break;
            case R.id.index_delete:
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mMediaObject.removePart(part, true);
                        if (mMediaObject.getMedaParts().size() == 0) {
                            mIndexDelete.setVisibility(View.GONE);
                            mIndexAlbum.setVisibility(View.VISIBLE);
                        }
                        if(mMediaObject.getDuration() < RECORD_MIN_TIME){
                            setBackAlpha(mVideoRecordFinishIv,127);
                            mVideoRecordProgressView.setShowEnouchTime(false);
                        }
                    } else {
                        part.remove = true;
                    }
                }
                break;
            case R.id.index_album:
                Intent intent2 = new Intent(this, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, false);
                intent2.putExtra(MAX_NUMBER, 1);
                intent2.putExtra(IS_NEED_FOLDER_LIST, true);
                startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);
                break;
            case R.id.custom_record_image_view:
                if(!isRecording) {
                    onStartRecording();
                }else{
                    onStopRecording();
                }
                break;
            case R.id.count_down_tv:
                final int duration = mMediaObject.getDuration() / 1000;
                hideOtherView();
                new PopupManager(this).showCountDown(getResources(), duration, new PopupManager.SelTimeBackListener() {
                    @Override
                    public void selTime(String selTime, boolean isDismiss) {
                        if(!isDismiss){
                            showOtherView();
                        }else {
                            mRecordTimeInterval = (Float.parseFloat(selTime) - duration) * 1000;
                            if(mRecordTimeInterval >= 29900){
                                mRecordTimeInterval = 30350;
                            }
                            hideAllView();
                            mMyHandler.sendEmptyMessage(CHANGE_IMAGE);
                        }
                    }
                });
                break;
            case R.id.meet_mask:

                break;
            case R.id.video_filter:
                if (mRecordCameraView.getCameraId() == 0){
                    Toast.makeText(this, "后置摄像头 不使用美白磨皮功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                hideOtherView();
                new PopupManager(this).showBeautyLevel(mRecordCameraView.getBeautyLevel(), new PopupManager.SelBeautyLevel() {
                    @Override
                    public void selBeautyLevel(int level) {
                        showOtherView();
                        mRecordCameraView.changeBeautyLevel(level);
                    }
                });
                break;
        }
    }
    
    private void onStartRecording(){
        isRecording = true;
        String storageMp4 = FileUtils.getStorageMp4(String.valueOf(System.currentTimeMillis()));
        MediaObject.MediaPart mediaPart = mMediaObject.buildMediaPart(storageMp4);
        mRecordCameraView.setSavePath(storageMp4);
        mRecordCameraView.startRecord();
        mCustomRecordImageView.startRecord();
        mVideoRecordProgressView.start();
        alterStatus();
    }

    private void onStopRecording() {
            isRecording = false;
            mRecordCameraView.stopRecord();
            mVideoRecordProgressView.stop();
            //todo:录制释放有延时，稍后处理
            mMyHandler.sendEmptyMessageDelayed(DELAY_DETAL,250);
            mCustomRecordImageView.stopRecord();
            alterStatus();
    }

    private void setBackAlpha(Button view ,int alpha) {
        if(alpha > 127){
            view.setClickable(true);
        }else{
            view.setClickable(false);
        }
        view.getBackground().setAlpha(alpha);
    }

    private void showOtherView() {
        if (mMediaObject != null && mMediaObject.getMedaParts().size() == 0) {
            mIndexDelete.setVisibility(View.GONE);
            mIndexAlbum.setVisibility(View.VISIBLE);
        } else {
            mIndexDelete.setVisibility(View.VISIBLE);
            mIndexAlbum.setVisibility(View.GONE);
        }
        mMeetMask.setVisibility(View.VISIBLE);
        mVideoFilter.setVisibility(View.VISIBLE);
        mCountDownTv.setVisibility(View.VISIBLE);
        mMatchingBack.setVisibility(View.VISIBLE);
        mCustomRecordImageView.setVisibility(View.VISIBLE);
    }
    private void hideOtherView() {
        mIndexAlbum.setVisibility(View.INVISIBLE);
        mIndexDelete.setVisibility(View.INVISIBLE);
        mMeetMask.setVisibility(View.INVISIBLE);
        mVideoFilter.setVisibility(View.INVISIBLE);
        mCountDownTv.setVisibility(View.INVISIBLE);
        mMatchingBack.setVisibility(View.INVISIBLE);
        mCustomRecordImageView.setVisibility(View.INVISIBLE);
    }
    //正在录制中
    public void alterStatus(){
        if(isRecording){
            mIndexAlbum.setVisibility(View.INVISIBLE);
            mIndexDelete.setVisibility(View.INVISIBLE);
            mMeetMask.setVisibility(View.INVISIBLE);
            mVideoFilter.setVisibility(View.INVISIBLE);
            mCountDownTv.setVisibility(View.INVISIBLE);
            mMatchingBack.setVisibility(View.INVISIBLE);
        }else{
            if (mMediaObject != null && mMediaObject.getMedaParts().size() == 0) {
                mIndexDelete.setVisibility(View.GONE);
                mIndexAlbum.setVisibility(View.VISIBLE);
            } else {
                mIndexDelete.setVisibility(View.VISIBLE);
                mIndexAlbum.setVisibility(View.GONE);
            }
            mMeetMask.setVisibility(View.VISIBLE);
            mVideoFilter.setVisibility(View.VISIBLE);
            mCountDownTv.setVisibility(View.VISIBLE);
            mMatchingBack.setVisibility(View.VISIBLE);
            mMeetCamera.setVisibility(View.VISIBLE);
            mVideoRecordFinishIv.setVisibility(View.VISIBLE);
            mVideoRecordProgressView.setVisibility(View.VISIBLE);
        }
    }

    private void hideAllView() {
        hideOtherView();
        mVideoRecordFinishIv.setVisibility(View.GONE);
        mVideoRecordProgressView.setVisibility(View.GONE);
        mMeetCamera.setVisibility(View.GONE);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mRecordCameraView.onTouch(event);
        if (mRecordCameraView.getCameraId() == 1) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float sRawX = event.getRawX();
                float sRawY = event.getRawY();
                float rawY = sRawY * MyApplication.screenWidth / MyApplication.screenHeight;
                float temp = sRawX;
                float rawX = rawY;
                rawY = (MyApplication.screenWidth - temp) * MyApplication.screenHeight / MyApplication.screenWidth;

                Point point = new Point((int) rawX, (int) rawY);
                mRecordCameraView.onFocus(point, new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            mRecorderFocusIv.onFocusSuccess();
                        } else {
                            mRecorderFocusIv.onFocusFailed();
                        }
                    }
                });
                mRecorderFocusIv.startFocus(new Point((int) sRawX, (int) sRawY));
        }
        return true;
    }

    @Override
    public void onFilterChange(final MagicFilterType type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == MagicFilterType.NONE){
                    Toast.makeText(RecorderActivity.this,"当前没有设置滤镜--"+type,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RecorderActivity.this,"当前滤镜切换为--"+type,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private static class MyHandler extends Handler {

        private WeakReference<RecorderActivity> mVideoRecordActivity;

        public MyHandler(RecorderActivity videoRecordActivity) {
            mVideoRecordActivity = new WeakReference<>(videoRecordActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecorderActivity activity = mVideoRecordActivity.get();
            if (activity != null) {
                switch (msg.what){
                    case DELAY_DETAL:
                        activity.mMediaObject.stopRecord(activity,activity.mMediaObject);
                        break;
                    case CHANGE_IMAGE:
                        switch (activity.mNum){
                            case 0:
                                activity.mCountTimeDownIv.setVisibility(View.VISIBLE);
                                activity.mCountTimeDownIv.setImageResource(R.drawable.bigicon_3);
                                activity.mMyHandler.sendEmptyMessageDelayed(CHANGE_IMAGE,1000);
                                break;
                            case 1:
                                activity.mCountTimeDownIv.setImageResource(R.drawable.bigicon_2);
                                activity.mMyHandler.sendEmptyMessageDelayed(CHANGE_IMAGE,1000);
                                break;
                            case 2:
                                activity.mCountTimeDownIv.setImageResource(R.drawable.bigicon_1);
                                activity.mMyHandler.sendEmptyMessageDelayed(CHANGE_IMAGE,1000);
                                break;
                            default:
                                activity.mMyHandler.removeCallbacks(null);
                                activity.mCountTimeDownIv.setVisibility(View.GONE);
                                activity.mVideoRecordProgressView.setVisibility(View.VISIBLE);
                                activity.mCustomRecordImageView.setVisibility(View.VISIBLE);
                                activity.mCustomRecordImageView.performClick();
                                activity.mVideoRecordProgressView.setCountDownTime(activity.mRecordTimeInterval);
                                break;
                        }
                        if(activity.mNum >= 3){
                            activity.mNum = 0;
                        }else {
                            activity.mNum++;
                        }
                        break;
                    case OVER_CLICK:
                        activity.mCustomRecordImageView.performClick(); //定时结束
                        break;
                }
            }
        }
    }

    private static final String TAG = "RecorderActivity";

    String videoFileName;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case StaticFinalValues.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(StaticFinalValues.RESULT_PICK_VIDEO);
                    for (VideoFile file : list) {
                        videoFileName = file.getPath();
                    }


                    //这一段用来判断视频时间的
                    try {
                        MediaPlayer player = new MediaPlayer();
                        player.setDataSource(videoFileName);
                        player.prepare();
                        int duration = player.getDuration();
                        player.release();
                        int s = duration / 1000;
                        int hour = s / 3600;
                        int minute = s % 3600 / 60;
                        int second = s % 60;
                        Log.e(TAG, "视频文件长度,分钟: " + minute + "视频有" + s + "秒");
                        if (s >= 120) {
                            Toast.makeText(this, "视频剪辑不能超过2分钟", Toast.LENGTH_LONG).show();
                            return;
                        } else if (s < 5) {
                            Toast.makeText(this, "视频剪辑不能少于5秒", Toast.LENGTH_LONG).show();
                            return;
                        }else{
                            Intent intent = new Intent(RecorderActivity.this, LocalVideoActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(StaticFinalValues.VIDEOFILEPATH, videoFileName);
                            bundle.putInt(StaticFinalValues.MISNOTCOMELOCAL, 0);
                            intent.putExtra(StaticFinalValues.BUNDLE, bundle);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
        }
    }
}
