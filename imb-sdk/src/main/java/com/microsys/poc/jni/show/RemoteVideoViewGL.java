package com.microsys.poc.jni.show;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class RemoteVideoViewGL extends RemoteVideoView {
    private static final String TAG = RemoteVideoViewGL.class.getCanonicalName();

    private ByteBuffer mVideoFrame;
    private Context mContext;
    private RemoteVideoViewGLPreview mPreview;
    private int mWidth;
    private int mHeight;
    private int screenOrientation;
    private int videoDir = 0;
    private int id;


    public RemoteVideoViewGL(int uniqueId, int videoWidth, int videoHeight, int screenOrientation) {
        id = uniqueId;
        mWidth = videoWidth;
        mHeight = videoHeight;
        this.screenOrientation = screenOrientation;
        final int capacity = ((mWidth * mHeight * 3) >> 1) + 1;
        mVideoFrame = ByteBuffer.allocateDirect(capacity);
    }

    @Override
    public View startPreview(Context context, int previewMaxW, int previewMaxH, IRemoteDrawCalc calc,
                             TriangleVerticesCallback callback) {
        synchronized (this) {
            mContext = context;
            if (mContext != null) {
                if (mPreview == null || mPreview.isDestroyed()) {
                    mPreview = new RemoteVideoViewGLPreview(mContext,
                            mVideoFrame, mWidth, mHeight, previewMaxW, previewMaxH, id);
                    mPreview.setDrawCalc(calc);
                }
            }
            return mPreview;
        }
    }

    public void clearCanvas() {
        if (mPreview == null) {
            return;
        }
        mWidth = 1;
        mHeight = 1;

        mPreview.mBufferWidthY = mPreview.mBufferHeightY = 1;
        mPreview.preBufferW = mPreview.preBufferH = 1;

        mPreview.handler.sendEmptyMessage(0);
    }

    private boolean enable = true;

    @Override
    public void enable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public int showVideo(byte[] data, int len, int width, int height, int direction) {
        if (!enable) {
            return 0;
        }

        setVideoData(data, len, width, height, direction);
        return 0;
    }

    private void setVideoData(byte[] data, int len, int width, int height, int direction) {
        if (null == mPreview || !mPreview.mSurfaceCreated) {
//            Log.i(TAG, "showVideo: mPreview 未创建");
            return;
        }

        if (len == 0) {
            return;
        }

//        Log.i(TAG, "id=" + id + " showVideo: ---------" + width + "w_h" + height + "_direction" + direction);
        if (null == mVideoFrame || width != mWidth || height != mHeight
                || (direction != videoDir)) {
            synchronized (mVideoFrame) {
                System.out.println("====allocateDirect==showVideo=========");
                videoDir = direction;

                mWidth = width;
                mHeight = height;
                videoDir = direction;

                if (len == 1) {
                    len = 0;
                }
                mVideoFrame = ByteBuffer.allocateDirect(len + 1);
                mPreview.setBuffer(mVideoFrame, mWidth, mHeight, videoDir);
            }
            if (width == 1) {
                //说明是隐藏View时 强行放的1大小的数据 不丢这一数据
            }else{
                //现在解码出来的数据有一帧是方向不对应的 直接丢掉
                return;
            }
            return;
        }

        try {
            synchronized (mVideoFrame) {
                byte directionTmp = (byte) videoDir;
                if (mVideoFrame.limit() == 0) {
                    return;
                }
                mVideoFrame.put(directionTmp);
                mVideoFrame.position(1);
                mVideoFrame.mark();
                mVideoFrame.put(data);
                mVideoFrame.reset();
                mPreview.requestRender();
            }

        } catch (Exception e) {
            e.printStackTrace();

            mVideoFrame.clear();
        }

    }

    private static void getYUVFile(byte[] data) {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/ImbCONF/" + SystemClock.elapsedRealtime() + ".yuv");
        try {

            if (null != file) {
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data);
                fos.close();
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 只会画面清除掉一下
     */
    @Override
    public void clear() {
        if (mPreview != null) {
            mPreview.setBlackScreen(true);
        }
    }

    /**
     * 同时会控件改变大小
     */
    public void blackScreen(boolean isShowRemote) {

        if (mPreview != null) {
            mPreview.setBlackScreen(true);

            if (isShowRemote) {
                enable = false;
                clearCanvas();
                mPreview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enable = true;
                    }
                }, 100);
            } else {
                clearCanvas();
            }
        }
    }

    @Override
    public void changeScreenOrientation(int screenOrientation, boolean isNeedCalcRemoteViewSize) {
        this.screenOrientation = screenOrientation;
        //更改布局
        if (mPreview != null) {
            mPreview.setScreenOrientation(this.screenOrientation);
            if (isNeedCalcRemoteViewSize) {
                //重新计算布局
                requestCalcSize();
            }
        }
    }

    private byte[] tempData = new byte[0];

    @Override
    public void hideView() {
        //改变大小 模拟一个1宽高的数据
        if (mPreview != null) {
            setVideoData(tempData, 1, 1, 1, VideoDirection.ANDROID_BACK_PORTRAIT);
        }
    }

    @Override
    public void requestCalcSize() {
        if (mPreview != null) {
            Log.i(TAG, "getDrawCalc requestCalcSize: "+id);
            mPreview.handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void setScreenOrientationLocked(int lockedOrientation) {
        if (mPreview != null) {
            mPreview.setScreenOrientationLocked(lockedOrientation);
        }
    }
}
