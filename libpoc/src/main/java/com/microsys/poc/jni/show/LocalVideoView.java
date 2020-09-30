package com.microsys.poc.jni.show;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.EncodecData;
import com.microsys.poc.jni.utils.AvcEncoder;
import com.microsys.poc.jni.utils.CameraFactory;
import com.microsys.poc.jni.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalVideoView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder = null;
    public AvcEncoder avcEncoder = null;
    int mPreviewWidth = 352;//视频分辨率：
    int mPreviewHeight = 288;//视频分辨率：
    int mPreviewRate = 20;//视频：每秒显示的帧数
    int mBitRate = 200;//视频比特率
    int mIDRIntervalTime = 4;//视频关键帧输出间隔
    int mCameraId = 0;
    boolean isAutoFocus;
    public Camera mCamera;
    //不明参数
    boolean bIfPreview = false;
    double defaultStamp = 4500;
    static double timestamp = 0;
    long currCount = 0;//当前帧数
    long beginTime = 0;
    long lastTime = 0;
    private int m_tsPerS = 4500;
    byte[] mPreBuffer = null;//摄像头缓存大小
    private Context mContext;

    /**
     * 是否传输视频流
     */
    private boolean whetherSendVideo = true;

    public static int currentLevel = 0;
    public static boolean isZoomChanged = false;
    public static int zoomLevel = 0;//放大缩小的等级

    private boolean isFirstTimeStopSend = true;

    private float maxW;
    private float maxH;

    private volatile boolean isDisabled = true;

    private int screenOrientation = VideoDirection.SCREEN_PORTRAIT;

    /**
     * 发出的视频的方向
     */
    private int videoDirection;


    @SuppressWarnings("deprecation")
    public LocalVideoView(Context context, int iPreferWidth, int iPreferHeight,
                          int iPreferRate, int iBitRate, int iFrameTime, int iCameraId, int screenOrientation, boolean isAutoFocus) {
        super(context);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFixedSize(iPreferWidth, iPreferHeight); // 预览大小設置
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mContext = context;
        this.setVisibility(View.VISIBLE);
        mPreviewWidth = iPreferWidth;
        mPreviewHeight = iPreferHeight;
        mPreviewRate = iPreferRate;
        mBitRate = iBitRate * 1024;
        mIDRIntervalTime = iFrameTime;
        mCameraId = iCameraId;
        this.screenOrientation = screenOrientation;
        videoDirection = getVideoDirection();

        if (mPreviewRate != 0) {
            m_tsPerS = 90000 / mPreviewRate;
        }
        this.isAutoFocus = isAutoFocus;
    }

    public LocalVideoView setMaxSize(int maxW, int maxH, int screenOrientation) {
        if (screenOrientation == VideoDirection.SCREEN_PORTRAIT) {
            this.maxH = maxH;
            this.maxW = maxW;
        } else {
            this.maxH = maxW;
            this.maxW = maxH;
        }
        return this;
    }

    public void adjustAspectRatio(int cameraRotation, int screenOrientation) {
        int sourceW;
        int sourceH;
        if (cameraRotation == 0 || cameraRotation == 180) {
            if (screenOrientation == VideoDirection.SCREEN_PORTRAIT) {
                sourceW = mPreviewHeight;
                sourceH = mPreviewWidth;
            } else {
                sourceW = mPreviewWidth;
                sourceH = mPreviewHeight;
            }

        } else {
            if (screenOrientation == VideoDirection.SCREEN_PORTRAIT) {
                sourceW = mPreviewWidth;
                sourceH = mPreviewHeight;
            } else {
                sourceW = mPreviewHeight;
                sourceH = mPreviewWidth;
            }
        }

        int mViewWidth, mViewHeight;
        int margin;
        float ratio = maxW / maxH;
        float ratio1 = 1.0F * sourceW / sourceH;
        if (sourceH * ratio < sourceW) {
            mViewWidth = (int) maxW;
            mViewHeight = (int) (mViewWidth / ratio1);
            margin = (int) (-(maxH - mViewHeight) / 2);
        } else {
            mViewHeight = (int) maxH;
            mViewWidth = (int) (mViewHeight * ratio1);
            margin = (int) ((maxW - mViewWidth) / 2);
        }

        margin = 0;
        mViewHeight = (int) maxH;
        mViewWidth = (int) maxW;
        Log.i("qqqqqqqqq", "localView:"+mViewWidth+"_"+mViewHeight);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(mViewWidth, mViewHeight);
        } else {
            layoutParams.width = mViewWidth;
            layoutParams.height = mViewHeight;
        }

        if (margin > 0) {
            layoutParams.setMargins(margin, 0, margin, 0);
        } else if (margin < 0) {
            margin = -margin;
            layoutParams.setMargins(0, margin, 0, margin);
        } else {
            layoutParams.setMargins(0, 0, 0, 0);
        }
        setLayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int mRatioWidth;
        int mRatioHeight;
        if (screenOrientation == VideoDirection.SCREEN_PORTRAIT) {
            //宽高 视频1280 720  预览720 1280
            mRatioWidth = mPreviewHeight;
            mRatioHeight = mPreviewWidth;
        } else {
            mRatioWidth = mPreviewWidth;
            mRatioHeight = mPreviewHeight;
        }

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width > height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

    /**
     * 在不重新生成该类对象的前提下改变参数
     *
     * @param iPreferWidth
     * @param iPreferHeight
     * @param iPreferRate
     * @param iBitRate
     * @param iFrameTime
     * @param iCameraId
     * @param isAutoFocus
     */
    public void setLocalVideoParam(int iPreferWidth, int iPreferHeight,
                                   int iPreferRate, int iBitRate, int iFrameTime, int iCameraId, boolean isAutoFocus) {

        mPreviewWidth = iPreferWidth;
        mPreviewHeight = iPreferHeight;
        mPreviewRate = iPreferRate;
        mBitRate = iBitRate * 1024;
        mIDRIntervalTime = iFrameTime;
        mCameraId = iCameraId;
        this.isAutoFocus = isAutoFocus;

    }

    public void setVideoFrameDir(int videoFrameDir) {
        this.videoFrameDir = videoFrameDir;
    }

    private LinkedBlockingQueue<byte[]> encodeCacheQueue = new LinkedBlockingQueue<>();

    public boolean isLocalVideoEnabled() {
        return isDisabled;
    }

    public void enableLocalVideo(boolean enable) {
        isDisabled = !enable;
    }

    PreviewCallback myPreviewCallBack = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (null == data) {
                System.out.println("========myPreviewCallBack=======data==" + data);
                return;
            }

            if (isDisabled) {
                if (!encodeCacheQueue.isEmpty()) {
                    encodeCacheQueue.clear();
                }
                return;
            }

//            Log.i("LocalVideoView", "onPreviewFrame: " + mPreviewWidth + "_" + mPreviewHeight);

            if (!whetherSendVideo) {
                if (mPreBuffer != null) {
                    synchronized (mPreBuffer) {
                        data = mPreBuffer;
                    }
                }
            }

            if (!encodeCacheQueue.offer(data)) {
                //添加失败 满了就去除一个
                Log.i("ffffffff", "onPreviewFrame: ");
                encodeCacheQueue.poll();
                encodeCacheQueue.offer(data);
            }
        }
    };

    private void getH264File(byte[] data) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/AvcEncoderSend.264");
        try {
            if (null != file) {
                if (!file.exists()) {
                    file.createNewFile();
                }

                if (file.length() >= 100 * 1024 * 1024) {//如果文件大于100M，删
                    file.delete();
                }

                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (!bIfPreview) {
            LogUtil.getInstance().logWithMethod(new Exception(), "surfaceChanged" + "width ==" + width + "height ==" + height + "format ==" + format, "Zhaolg");
            mPreviewHeight = height;
            mPreviewWidth = width;
            initCamera();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        LogUtil.getInstance().logWithMethod(new Exception(), "Zhaolg");
        try {
            mCamera = CameraFactory.openCamera(mCameraId, mContext);
            if (null != mCamera) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setPreviewCallback(myPreviewCallBack);
            }
        } catch (Exception ex) {
            System.out.println("=====mCamera======" + mCamera + "  " + ex.getStackTrace());
            ex.printStackTrace();
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.getInstance().logWithMethod(new Exception(), "Zhaolg");
        if (null != mCamera) {
            mCamera.setPreviewCallback(null); // ！！这个必须在前，不然�?出出�?
            mCamera.stopPreview();
            bIfPreview = false;
            mCamera.release();
            mCamera = null;

        }
        encodeCacheQueue.clear();
        stopEncodeCachePool();
    }

    /**
     * 切换前后摄像头
     */
    public void switchCamera() {
        if (mCamera == null) {
            return;
        }

        if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
            bIfPreview = false;
            if (null != mCamera) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            encodeCacheQueue.clear();

            mCameraId = CameraInfo.CAMERA_FACING_FRONT;
            videoDirection = getVideoDirection();

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            destroyVideoEncoder();
            reStartVideoEncoder();
            mCamera = CameraFactory.openCamera(CameraInfo.CAMERA_FACING_FRONT, mContext);
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);// set the surface to be
                mCamera.setPreviewCallback(myPreviewCallBack);
            } catch (Exception ex) {
                if (null != mCamera) {
                    mCamera.release();
                    mCamera = null;
                }
                return;
            }

            initCamera();

        } else {
            bIfPreview = false;
            mCamera.setPreviewCallback(null);
            encodeCacheQueue.clear();

            mCamera.stopPreview();//停止预览
            mCamera.release();// 释放该摄像头
            mCamera = null;//
            encodeCacheQueue.clear();

            mCameraId = CameraInfo.CAMERA_FACING_BACK;
            videoDirection = getVideoDirection();

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            destroyVideoEncoder();
            reStartVideoEncoder();
            mCamera = CameraFactory.openCamera(CameraInfo.CAMERA_FACING_BACK, mContext);//重新打开
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);// set the surface to be
                mCamera.setPreviewCallback(myPreviewCallBack);
            } catch (Exception e) {
                if (null != mCamera) {
                    mCamera.release();
                    mCamera = null;
                }
                return;
            }

            initCamera();

        }
        if (avcEncoder != null) {
            avcEncoder.m_cameraTowards = mCameraId;
            avcEncoder.videoDirection = videoDirection;
        }
    }

    private void initCamera() {
        if (mCamera == null) {
            return;
        }
        if (bIfPreview) {
            mCamera.stopPreview();// stopCamera();
        }
        if (null != mCamera) {
            try {
                /* Camera Service settings */
                Camera.Parameters parameters = mCamera.getParameters();
                // parameters.setFlashMode("off"); // 无闪光灯
                // Sets the image format for picture 设定相片格式为JPEG，默认为NV21
                if (isAutoFocus) {
                    //支持自动对焦的摄像头设为自动对焦
                    List<String> modeList = parameters.getSupportedFocusModes();
                    int modeSize = modeList.size();
                    for (int i = 0; i < modeSize; i++) {
                        if (modeList.get(i).equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }
                    }
                }

                List<Size> previewSizes = mCamera.getParameters()
                        .getSupportedPreviewSizes();
                Size psize = null;
                boolean isSupportedSize = false;
                Size size = null;
                int size1 = previewSizes.size();
                for (int i = 0; i < size1; i++) {
                    psize = previewSizes.get(i);
                    if (psize.width == mPreviewWidth && psize.height == mPreviewHeight) {
                        isSupportedSize = true;
                    }
                    if (i == size1 - 1) {
                        size = psize;
                    }
                }
                if (isSupportedSize) {
                    parameters.setPreviewSize(mPreviewWidth, mPreviewHeight); // 指定preview的大�?
                } else {
                    if (size == null) {
                        return;
                    }
                    parameters.setPreviewSize(size.width, size.height);//默认cif
                    if (avcEncoder != null) {
                        avcEncoder.close();
                        avcEncoder = null;
                    }
                    mPreviewWidth = size.width;
                    mPreviewHeight = size.height;
                    avcEncoder = AvcEncoder.CreateEncoder(mPreviewWidth, mPreviewHeight,
                            mPreviewRate, mBitRate, mIDRIntervalTime, mCameraId, videoFrameDir, videoDirection);
                }

                setCameraDisplayOrientation(screenOrientation);

                // 设定配置参数并开启预�?
                mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera
                //mCamera.cancelAutoFocus();
                mCamera.startPreview(); // 打开预览画面
                bIfPreview = true;

                encodeFromCachePool();
                //whetherSendVideo = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Future<?> task;
    private boolean running = false;

    private void encodeFromCachePool() {
        encodeCacheQueue.clear();
        running = true;
        task = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                while (running) {

                    try {
                        if (isDisabled) {
                            //是否禁用了发送自己的视频
                            Thread.sleep(20);
                            continue;
                        }
                        byte[] data = encodeCacheQueue.take();
                        if (data == null) {
                            Thread.sleep(100);
                            continue;
                        }
                        //取慢点
//                        Thread.sleep(10);

                        if (AvcEncoder.enCodecMode == 1 && null != avcEncoder) {
                            int ts = GetStampTime();

                            if (ts == -1) {
                                continue;
                            }

                            synchronized (obj) {
//                                Log.i("dddddddd", "run000: " + videoDirection);
                                if (!isDisabled) {
                                    avcEncoder.inputCameraData(data, videoDirection);
                                } else {
                                    continue;
                                }
                                EncodecData encodecData = avcEncoder.getOutputEncodecData();

                                while (null != encodecData) {

                                    byte[] h264 = encodecData.getData();
                                    int videoTs = (int) encodecData.getTs();
                                    int cameraId = encodecData.getCameraId();

//                                    Log.i("dddddddd", "run111: " + cameraId);
                                    SendVideo(h264, cameraId, videoTs);

                                    if (!isDisabled) {
                                        encodecData = avcEncoder.getOutputEncodecData();
                                    } else {
                                        encodecData = null;
                                    }
                                }
                            }

                        } else {
                            //用软件编码
                            dealWithStampTime(data, true);
                        }
                    } catch (Exception e) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void stopEncodeCachePool() {
        running = false;
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    public void stopSendVideo() {
        whetherSendVideo = false;
        if (isFirstTimeStopSend) {
            //在停止的时候才申请黑屏流
            mPreBuffer = new byte[mPreviewWidth * mPreviewHeight * 3 / 2];
            int pos = mPreviewWidth * mPreviewHeight;
            int len = mPreviewWidth * mPreviewHeight * 3 / 2;
            for (int i = pos; i < len; i++) {
                mPreBuffer[i] = 127;
            }
            isFirstTimeStopSend = false;
        }

    }

    public void resumeSendVideo(boolean isFinishCall) {
        whetherSendVideo = true;
        //avcEncoder.lastTime = System.currentTimeMillis();
        if (isFinishCall) {//关闭编码器
            synchronized (obj) {
                if (null != avcEncoder) {
                    avcEncoder.close();
                    avcEncoder = null;
                }
            }

            if (null != mPreBuffer) {
                synchronized (mPreBuffer) {
                    mPreBuffer = null;
                }
            }
        }
    }


    /**
     * 用于同步
     */
    private Object obj = new Object();

    private int videoFrameDir = 0;

    public void startCreateAvcEncoder(int videoFrameDir) {
        synchronized (obj) {
            if (AvcEncoder.enCodecMode == 1) {

                if (avcEncoder != null) {
                    avcEncoder.close();
                    avcEncoder = null;
                }
                this.videoFrameDir = videoFrameDir;
                avcEncoder = AvcEncoder.CreateEncoder(mPreviewWidth, mPreviewHeight, mPreviewRate, mBitRate, mIDRIntervalTime, mCameraId, videoFrameDir, videoDirection);
                AvcEncoder.enCodecMode = 1;
            }
        }
    }

    //重启编码器
    public void reStartVideoEncoder() {
        if (avcEncoder == null) {
            avcEncoder = AvcEncoder.CreateEncoder(mPreviewWidth, mPreviewHeight, mPreviewRate, mBitRate, mIDRIntervalTime, mCameraId, videoFrameDir, videoDirection);
        }
    }

    //销毁编码器
    public void destroyVideoEncoder() {
        if (avcEncoder != null) {
            avcEncoder.close();
            avcEncoder = null;
        }
    }

    /**
     * 处理时间戳
     */
    private void dealWithStampTime(byte[] data, boolean isOver) {


        if (timestamp > Integer.MAX_VALUE) {
            currCount = 0;
        }

        int ret_ts;
        if (0 == currCount) {
            beginTime = System.currentTimeMillis();
            JniUtils.getInstance().setVideoData(data, 0, videoDirection);
            lastTime = beginTime;
            timestamp = 0;
            ret_ts = 0;
            currCount++;
        } else {
            long now = System.currentTimeMillis();
            if (now > lastTime) {
                //先计算动态时戳
                long temptimestamp = (90000 / 1000) * (now - lastTime);
                if (now - beginTime >= currCount * 1000 / mPreviewRate) {
                    timestamp = timestamp + temptimestamp;

                    int frameIndex = (int) timestamp / m_tsPerS;
                    if (frameIndex > currCount) {
                        ret_ts = frameIndex * m_tsPerS;
                        currCount = frameIndex;
                    } else {
                        ret_ts = ((int) currCount + 1) * m_tsPerS;
                        currCount++;
                    }

                    //System.out.println("===============ret_ts========"+ret_ts);
                    JniUtils.getInstance().setVideoData(data, ret_ts, videoDirection);

                    lastTime = now;
                }
            } else {
                lastTime = now;
            }
        }

    }

    private void SendVideo(byte[] data, int cameraId, int ts) {
        //System.out.println("=============sss ts============"+ts);
        JniUtils.getInstance().setVideoData(data, ts, cameraId);
    }

    /**
     * 处理时间戳：-1表示该图片不处理。
     */
    private int GetStampTime() {
        int ret_ts = 0;

        //这里要进行丢图片的操作。
        if (timestamp > Integer.MAX_VALUE) {
            currCount = 0;
        }

        if (0 == currCount) {
            beginTime = System.currentTimeMillis();
            ret_ts = 0;
            timestamp = 0;
            lastTime = beginTime;
            currCount++;
        } else {
            long now = System.currentTimeMillis();
            if (now > lastTime) {
                //先计算动态时戳
                long temptimestamp = (90000 / 1000) * (now - lastTime);

                //超过制定的时间，才进行处理图片，未超过时间的图片直接丢弃，返回-1
                if (now - beginTime >= currCount * 1000 / mPreviewRate) {

                    //currCount = (now - beginTime) * mPreviewRate/1000;
                    timestamp = timestamp + temptimestamp;
                    //去除后两位
                    ret_ts = (int) timestamp / 100 * 100;
                    currCount++;
                    lastTime = now;
                } else {
                    ret_ts = -1;
                }
            } else {
                lastTime = now;
                ret_ts = -1;
            }
        }
        return ret_ts;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (avcEncoder != null) {
            avcEncoder.close();
        }
        super.onDetachedFromWindow();
    }

    public void setScreenOrientationWithoutFreshUI(int screenOrientation) {
        //改变视频方向
        this.screenOrientation = screenOrientation;
        videoDirection = getVideoDirection();
        //编码器的方向
        if (avcEncoder != null) {
            avcEncoder.videoDirection = videoDirection;
        }
    }

    public void changeScreenOrientation(int screenOrientation) {
        //可能camera还未初始化 所以初始化的时候 需要把方向设置一下
        setScreenOrientationWithoutFreshUI(screenOrientation);
        if (mCamera != null) {
            setCameraDisplayOrientation(screenOrientation);
        }
    }

    private void setCameraDisplayOrientation(int screenOrientation) {
        if (screenOrientation == VideoDirection.SCREEN_LANDSCAPE_RIGHT) {
            mCamera.setDisplayOrientation(180);
        } else if (screenOrientation == VideoDirection.SCREEN_PORTRAIT) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }
    }

    private int getVideoDirection() {
        int cameraDirection;
        if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
            cameraDirection = VideoDirection.CAMERA_BACK;
        } else {
            cameraDirection = VideoDirection.CAMERA_FRONT;
        }
        int videoDirection = VideoDirection.getDirection(cameraDirection, screenOrientation);
        return videoDirection;
    }
}
