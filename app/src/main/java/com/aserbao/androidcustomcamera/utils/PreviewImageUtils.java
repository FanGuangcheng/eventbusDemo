package com.aserbao.androidcustomcamera.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import wseemann.media.FFmpegMediaMetadataRetriever;

public class PreviewImageUtils {
    private static FFmpegMediaMetadataRetriever ffmmr = null;
//    private static final String savePath = SkyLocalContext.getContext().getCacheDir().getPath() + "/preview/";
    private static final String savePath = "/storage/emulated/0/aserbaoFrames/frames/saveImg";
    private String filePath = null;
    private String fileName = null;
    private boolean isStop = false;
    public int mDuration = 0;
    private String outDir = null;

    private static PreviewImageUtils mInstance;

    public static PreviewImageUtils getInstance() {
        if (null == mInstance) {
            synchronized (PreviewImageUtils.class) {
                if (null == mInstance) {
                    mInstance = new PreviewImageUtils();
                }
            }
        }
        return mInstance;
    }

    public PreviewImageUtils() {

    }

    private void getDuration(String url) {
        try {
            if (ffmmr != null) {
                ffmmr.release();
                ffmmr = null;
            }
            ffmmr = new FFmpegMediaMetadataRetriever();
            ffmmr.setDataSource(url);
            String durationStr = ffmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
            mDuration = Integer.parseInt(durationStr) / 1000;
            ffmmr.release();
            ffmmr = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDecode(String oldUrl, String url) {
        Log.d("fgcfgc", "PreviewImageUtils startDecode:");

        Bitmap bitmap = null;
        filePath = url;
        isStop = false;
        getDuration(url);
        File file = new File(url);
        if (file != null && file.exists()) {
            fileName = file.getName();
        } else {
            return;
        }
        deleteBitmap(oldUrl);
        try {
            getFrameAtTime(url, mDuration, 50);
            getFrameAtTime(url, mDuration, 30);
            getFrameAtTime(url, mDuration, 20);
            getFrameAtTime(url, mDuration, 10);
            getFrameAtTime(url, mDuration, 5);
            getFrameAtTime(url, mDuration, 3);
        } catch (Exception e) {
            e.printStackTrace();
            ffmmr = null;
        }

        Log.d("fgcfgc", "PreviewImageUtils end ----:");
    }

    private void getFrameAtTime(String url, int duration, int step) {
        Log.d("fgcfgc", "PreviewImageUtils getFrameAtTime step:" + step);

        Bitmap bitmap = null;
        try {
            if (ffmmr != null) {
                ffmmr.release();
                ffmmr = null;
            }
            ffmmr = new FFmpegMediaMetadataRetriever();
            ffmmr.setDataSource(url);
            for (int i = 0; i < duration; i += step) {
                int pos = i * 1000 * 1000;
                if (hasFile(i, fileName)) {
                    continue;
                }
                if (ffmmr != null) {
                    bitmap = ffmmr.getScaledFrameAtTime(pos, 640, 360);
                    if (bitmap != null)
                        saveBitmap(bitmap, i, fileName);
                }
                Thread.sleep(10);
                if (isStop) {
                    break;
                }
            }
            ffmmr.release();
            ffmmr = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("fgcfgc", "PreviewImageUtils getFrameAtTime end---- step:" + step);
    }


    private void deleteFile(String path) {
        File file = new File(path);
        if (file != null && !file.exists()) {
            return;
        }
        try {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setStop(Boolean stop) {
        isStop = stop;
    }

    private void deleteBitmap(String path) {
        try {

            File file = new File(path);
            if (file.exists()) {
                String deleteDir = savePath + file.getName();
                deleteFile(deleteDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void saveBitmap(Bitmap bitmap, int position, String name) {
        outDir = savePath + name;
        try {
            File dirFile = new File(outDir);
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
                dirFile.setWritable(true);
                dirFile.setReadable(true);
                Runtime.getRuntime().exec("chmod 777 -R " + dirFile.getAbsolutePath());
            }
            File file = new File(outDir, position + ".jpg");

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            if (file.exists()) {
                file.setWritable(true);
                file.setReadable(true);
                Runtime.getRuntime().exec("chmod 777 -R " + file.getAbsolutePath());
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void setPreview(ImageView imageView, String url, int position) {
        String previewPath = null;
        String name = null;
        File file = new File(url);
        if (file.exists()) {
            name = file.getName();
        }
        previewPath = savePath + name + "/" + position + ".jpg";

        File showPicFile = new File(previewPath);
        if (showPicFile != null && showPicFile.exists()) {
            Uri uri = Uri.parse(previewPath);
            imageView.setImageURI(uri);
        }
    }

    private boolean hasFile(int position, String name) {
        try {
            String filePath = savePath + name + "/" + position + ".jpg";
            File file = new File(filePath);
            if (file != null && file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getVideoDuration(String url) {
        Log.d("fgcfgc", "PreviewImageUtils startDecodeImgInit:");
        try {
            if (ffmmr != null) {
                ffmmr.release();
                ffmmr = null;
            }

            ffmmr = new FFmpegMediaMetadataRetriever();
            ffmmr.setDataSource(url);
            String durationStr = ffmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
            ffmmr.release();
            ffmmr = null;
            return Integer.parseInt(durationStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("fgcfgc", "PreviewImageUtils startDecodeImgInit end ----:");
        return 0;
    }

    public Bitmap getFrameImgAtTime(String url, long position){
        Log.d("fgcfgc", "PreviewImageUtils getFrameImgAtTime position:" + position);

        try {
            if (ffmmr != null) {
                ffmmr.release();
                ffmmr = null;
            }
            ffmmr = new FFmpegMediaMetadataRetriever();
            ffmmr.setDataSource(url);
            if (ffmmr != null) {
                return ffmmr.getScaledFrameAtTime(position, 1080, 720);
            }
            ffmmr.release();
            ffmmr = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("fgcfgc", "PreviewImageUtils getFrameImgAtTime end---- position:" + position);
        return null;
    }
}
