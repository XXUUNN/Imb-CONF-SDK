package com.microsys.poc.jni.utils;


import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.microsys.poc.jni.entity.EncodecData;
import com.microsys.poc.jni.entity.TimeUsForwardId;
import com.microsys.poc.jni.entity.TimeUsTs;
import com.microsys.poc.jni.entity.type.CameraFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class AvcEncoder {

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private static final String TAG = "AvcEncoder";

    /**
     * 记录上一次可用的编码器
     */
    private static String AVAILABLE_CODEC_NAME = null;
    private static int AVAILABLE_COLOR_FORMAT = -1;

    // init encoder object
    private MediaCodec enCodec;

    private long currCount = 0;

    private int m_width;
    private int m_height;
    private int m_frameRate;
    private int m_tsPerS = 4500;
    //摄像头方向
    public int m_cameraTowards;
    //发出去的流的方向
    public int videoDirection;

    private int colorFormat;
    //是否支持硬件编码
    private boolean hardWareIsSupport = true;
    //记录关键帧的数据
    private byte[] spsPpsBuf;
    //标记是否是第一次获取关键帧
    private boolean isFirstFrame = true;
    //记录presentationTimeUs值和Ts之间的关系列表
    private LinkedBlockingQueue<TimeUsTs> timeList = new LinkedBlockingQueue<TimeUsTs>();
    private LinkedBlockingQueue<TimeUsForwardId> camerIdList = new LinkedBlockingQueue<TimeUsForwardId>();

    //编码器容器初始化
    private byte[] encodeOutputData = null;
    //是否硬编码
    public static int enCodecMode = 1; //0 不是 1 是
    //摄像头输出视频格式
    private static CameraFormat cameraFormat = null;
    private String phoneType = "";
    //是否第一次进入
    private boolean isFirstTime = true;
    // 用于记录第一帧的时戳
    private long beginTime = 0;
    public long lastTime = 0;
    private long encodeBeginTime = 0;
    static long timestamp = 0;

    //0 是普通的画面 1 是调度来的 需要旋转 让调度方向正确
    private int videoFrameDir = 0;
    private ByteBuffer[] inputBuffers;

    private Bundle params;

    long preEncodeTime = 0;
    int index = 0;

    boolean keyFirstFlag = true;

    public static AvcEncoder CreateEncoder(int width, int height,
                                           int framerate, int bitrate, int iframe_interval, int cameraTowards, int videoFrameDirection, int videoDirection) {


        AvcEncoder avcEncoder = new AvcEncoder(true, width, height, framerate,
                bitrate, iframe_interval, cameraTowards, videoFrameDirection, videoDirection);

        if (avcEncoder.hardWareIsSupport) {
            return avcEncoder;
        } else {
            return null;
        }
    }

    @SuppressLint("NewApi")
    public AvcEncoder(int width, int height, int framerate, int bitrate,
                      int iframe_interval, int cameraTowards, int videoFrameDirection) {

        //特殊处理
        phoneType = Build.MODEL;

        if (phoneType.contains("UG802TD")) {
            hardWareIsSupport = false;
            return;
        }
        //小于16的手机都不能用
        if (Build.VERSION.SDK_INT < 16) {
            hardWareIsSupport = false;
            return;
        }
        m_width = width;
        m_height = height;
        m_frameRate = framerate;
        m_cameraTowards = cameraTowards;

        if (m_frameRate != 0) {
            m_tsPerS = 90000 / m_frameRate;
        }

        MediaCodecInfo codecInfo = selectCodec(MIME_TYPE);
        if (codecInfo == null) {

            System.out.println("Unable to find an appropriate codec for "
                    + MIME_TYPE);
            hardWareIsSupport = false;
            return;
        }
        colorFormat = selectColorFormat(codecInfo, MIME_TYPE);
        LogUtil.getInstance().logWithMethod(new Exception(), "encode colorFormat = " + colorFormat, "Zhaolg");
        if (colorFormat == 0) {
            System.out.println("found colorFormat: " + colorFormat);

            hardWareIsSupport = false;
            return;
        }

        videoFrameDir = videoFrameDirection;

        try {
            MediaFormat format = null;
            if (videoFrameDir == 1) {
                format = MediaFormat.createVideoFormat(MIME_TYPE, m_height,
                        m_width);
                System.out.println("=====MediaFormat=====m_height====" + m_height + "==m_width===" + m_width);
            } else {
                format = MediaFormat.createVideoFormat(MIME_TYPE, m_width,
                        m_height);
                System.out.println("=====MediaFormat=====m_width====" + m_width + "==m_height===" + m_height);

            }


            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframe_interval);
//        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 90000);
//        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            System.out.println("format: " + format);
            enCodec = MediaCodec.createByCodecName(codecInfo.getName());
            MediaCodecInfo.CodecCapabilities caps = enCodec.getCodecInfo().getCapabilitiesForType(
                    MediaFormat.MIMETYPE_VIDEO_AVC);
            setEncoderMode(format, caps);

            System.out.println("========AvcEncoder11111111111111=============");
            enCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            System.out.println("========AvcEncoder2222222222222=============");

            enCodec.start();
            System.out.println("========AvcEncoder3333333333333333=============");

        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.out.println("====MediaCodec start failed ,try again=======");
            hardWareIsSupport = false;

        } catch (Exception e) {
            hardWareIsSupport = false;
            System.out.println("=========catch you my lover=============");
        }

        timeList.clear();
        camerIdList.clear();
        AvcEncoder.cameraFormat = CameraFormat.of(CameraFormat.getTypeof(CameraFormat.NV21));

        hardWareIsSupport = true;

    }

    private void setEncoderMode(MediaFormat codecFormat, MediaCodecInfo.CodecCapabilities caps) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecInfo.EncoderCapabilities encoderCaps;
            encoderCaps = caps.getEncoderCapabilities();
//            if (encoderCaps.isBitrateModeSupported(
//                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ)) {
//                Log.d(TAG, "Setting bitrate mode to BITRATE_MODE_CQ");
//                codecFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
//                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
//                Range<Integer> qualityRange;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                    qualityRange = encoderCaps.getQualityRange();
//                    Log.d(TAG, "Quality range: " + qualityRange);
//                    codecFormat.setInteger(MediaFormat.KEY_QUALITY, (int) (qualityRange.getLower() +
//                            (qualityRange.getUpper() - qualityRange.getLower()) * 80 / 100.0));
//                }
//
//            }else if (encoderCaps.isBitrateModeSupported(
//                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR)) {
//                Log.d(TAG, "Setting bitrate mode to BITRATE_MODE_CBR");
//                codecFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
//                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
//            } else if (encoderCaps.isBitrateModeSupported(
//                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR)) {
//                Log.d(TAG, "Setting bitrate mode to BITRATE_MODE_VBR");
//                codecFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
//                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
//            }
//            if (!encoderCaps.isBitrateModeSupported(
//                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ)) {
//                Log.d(TAG, "Setting bitrate mode to BITRATE_MODE_CQ");
//                codecFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
//                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
//                Range<Integer> qualityRange;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                    qualityRange = encoderCaps.getQualityRange();
//                    Log.d(TAG, "Quality range: " + qualityRange);
//                    codecFormat.setInteger(MediaFormat.KEY_QUALITY, (int) (qualityRange.getLower() +
//                            (qualityRange.getUpper() - qualityRange.getLower()) * 80 / 100.0));
//                }
//
//            } else
            if (encoderCaps.isBitrateModeSupported(
                    MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR)) {
                LogUtil.getInstance().logWithMethod(new Exception(), "Setting bitrate mode to BITRATE_MODE_VBR", "x");
                codecFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            }
        } else {
            Log.d(TAG, "BITRATE_MODE_VBR no support");
        }

    }

    @SuppressLint("NewApi")
    public AvcEncoder(boolean autoTryMoreEncoder, int width, int height, int framerate, int bitrate,
                      int iframe_interval, int cameraTowards, int videoFrameDirection, int videoDirection) {

        //特殊处理
        phoneType = Build.MODEL;

        if (phoneType.contains("UG802TD")) {
            hardWareIsSupport = false;
            return;
        }
        //小于16的手机都不能用
        if (Build.VERSION.SDK_INT < 16) {
            hardWareIsSupport = false;
            return;
        }
        m_width = width;
        m_height = height;
        m_frameRate = framerate;
        m_cameraTowards = cameraTowards;
        this.videoDirection = videoDirection;

        if (m_frameRate != 0) {
            m_tsPerS = 90000 / m_frameRate;
        }

//        if (TextUtils.isEmpty(AVAILABLE_CODEC_NAME) || AVAILABLE_COLOR_FORMAT <= 0) {
        //没有记录
        List<MediaCodecInfo> mediaCodecInfos = selectCodecList(MIME_TYPE);
        int size = mediaCodecInfos.size();
        boolean isEncodeOk = false;
        for (int i = 0; i < size; i++) {
            MediaCodecInfo mediaCodecInfo = mediaCodecInfos.get(i);
            if (configEncoder(mediaCodecInfo, framerate, bitrate, iframe_interval, videoFrameDirection)) {
                //开始成功了
                AVAILABLE_CODEC_NAME = mediaCodecInfo.getName();
                AVAILABLE_COLOR_FORMAT = colorFormat;

                hardWareIsSupport = true;
                isEncodeOk = true;
                LogUtil.getInstance().logWithMethod(new Exception(), "可用编码器" + size + "硬编码器创建成功" + AVAILABLE_CODEC_NAME, "x");
                break;
            }
        }

        if (!isEncodeOk) {
            hardWareIsSupport = false;
            LogUtil.getInstance().logWithMethod(new Exception(), "硬编码器创建---失败!", "x");
        }

        timeList.clear();
        camerIdList.clear();
        AvcEncoder.cameraFormat = CameraFormat.of(CameraFormat.getTypeof(CameraFormat.NV21));

        if (phoneType.contains("HUAWEI HN3-U01")) {
            colorFormat = YUVFormat.HUAWEI_HN3_U01_NV21;
        }

        hardWareIsSupport = true;

        params = new Bundle();
        params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
    }

    private boolean configByName(String codecName, int availableColorFormat, int framerate, int bitrate, int iframe_interval, int videoFrameDirection) {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, m_width,
                    m_height);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, availableColorFormat);
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframe_interval);

            enCodec = MediaCodec.createByCodecName(codecName);
            System.out.println("========AvcEncoder1111======");
            enCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            enCodec.start();

            System.out.println("========AvcEncoder2222==========");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean configEncoder(MediaCodecInfo codecInfo, int framerate, int bitrate, int iframe_interval, int videoFrameDirection) {
        if (codecInfo == null) {

            System.out.println("Unable to find an appropriate codec for "
                    + MIME_TYPE);
            hardWareIsSupport = false;
            return true;
        }
        colorFormat = selectColorFormat(codecInfo, MIME_TYPE);
        LogUtil.getInstance().logWithMethod(new Exception(), "encode colorFormat = " + colorFormat, "Zhaolg");
        if (colorFormat == 0) {
            System.out.println("found colorFormat: " + colorFormat);

            hardWareIsSupport = false;
            return true;
        }

        videoFrameDir = videoFrameDirection;

        try {
            MediaFormat format;
            if (videoFrameDir == 1) {
                format = MediaFormat.createVideoFormat(MIME_TYPE, m_height,
                        m_width);
                System.out.println("=====MediaFormat=====m_height====" + m_height + "==m_width===" + m_width);
            } else {
                format = MediaFormat.createVideoFormat(MIME_TYPE, m_width,
                        m_height);
                System.out.println("=====MediaFormat=====m_width====" + m_width + "==m_height===" + m_height);

            }


            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, -1);
//        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 90000);
//        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            System.out.println("format: " + format);
            enCodec = MediaCodec.createByCodecName(codecInfo.getName());

            MediaCodecInfo.CodecCapabilities caps = enCodec.getCodecInfo().getCapabilitiesForType(
                    MediaFormat.MIMETYPE_VIDEO_AVC);
            setEncoderMode(format, caps);

            System.out.println("========AvcEncoder11111111111111=============");
            enCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            System.out.println("========AvcEncoder2222222222222=============");

            enCodec.start();
            System.out.println("========AvcEncoder3333333333333333" +
                    enCodec.getName() +
                    "=============");

            return true;

        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.out.println("====MediaCodec start failed ,try again=======");
        } catch (Exception e) {
            System.out.println("=========catch you my lover=============");
        }
        return false;
    }


    int errorCount = 0;

    @SuppressLint("NewApi")
    public void inputCameraData(byte[] cameraData, int mCameraId) {

        if (encodeOutputData == null) {
            encodeOutputData = new byte[m_width * m_height * 3 / 2];
        }
        //ms
        long nowTime = System.currentTimeMillis();
        if (isFirstTime) {
            encodeBeginTime = nowTime;
            isFirstTime = false;
        }
        // 转化格式
        int length = YUVFormat.swapNV12AndNV21ToOther(cameraData, cameraData.length, cameraFormat, encodeOutputData,
                m_width, m_height, colorFormat);

        byte[] rotateData = encodeOutputData;

        try {
            if (inputBuffers == null) {
                inputBuffers = enCodec.getInputBuffers();
            }

            int inputBufferIndex = enCodec.dequeueInputBuffer(100);//超时时间设为100ms，防止在这里阻塞
            long ptsUsec = computePresentationTime(nowTime);
            //inputTimeUsTs(ptsUsec, ts);
            inputCameraId(ptsUsec, mCameraId);
            //generateIndex ++;

            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(rotateData, 0, length);
                enCodec.queueInputBuffer(inputBufferIndex, 0,
                        length, ptsUsec, 0);
            }

        } catch (Throwable t) {
            t.printStackTrace();
            LogUtil.getInstance().logWithMethod(new Exception(), "AvcEncoder inputBuffers Exception", "Zhaolg");
        }
    }

    ByteBuffer[] outputBuffers;

    @SuppressLint("NewApi")
    public EncodecData getOutputEncodecData() {

        try {
            if (outputBuffers == null) {
                outputBuffers = enCodec.getOutputBuffers();
            }
            //数据包装对象
            EncodecData encodecData = new EncodecData();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = enCodec.dequeueOutputBuffer(bufferInfo, 50);

            if (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];

                outputBuffer.position(bufferInfo.offset);
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);

                if (isFirstFrame) {
                    spsPpsBuf = new byte[bufferInfo.size];
                    System.arraycopy(outData, 0, spsPpsBuf, 0, outData.length);
                    isFirstFrame = false;
                    return null;
                }

                if ((outData[4] & 0x1f) == 5)    //表示是IDR帧，则需要在前面添加sps
                {
                    byte[] idrData = new byte[bufferInfo.size + spsPpsBuf.length];
                    System.arraycopy(spsPpsBuf, 0, idrData, 0, spsPpsBuf.length);
                    System.arraycopy(outData, 0, idrData, spsPpsBuf.length, outData.length);
                    outData = idrData;

                    Log.i(TAG, "getOutputEncodecData: KeyFrame" + outData.length);
                }
//                Log.i(TAG, "getOutputEncodecData: Frame" + outData.length);

                encodecData.setData(outData);
                int ts = GetStampTime(bufferInfo.presentationTimeUs / 1000);
//				System.out.println("===============tststs=================="+ts);
                int cameraId;
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    cameraId = videoDirection;
                } else {
                    cameraId = outputCameraId(bufferInfo.presentationTimeUs);
                }
                encodecData.setTs(ts);

                encodecData.setCameraId(cameraId);
                enCodec.releaseOutputBuffer(outputBufferIndex, false);

                errorCount = 0;

                index++;
                if (index == 20) {
                    //10此 计算一下时间
                    long curTime = System.currentTimeMillis();
                    long space = curTime - preEncodeTime;
                    if (space > 3000) {
                        requestKeyFrame();
                        preEncodeTime = curTime;
                    }
                    index = 0;
                }

//                if (keyFirstFlag) {
//                    requestKeyFrame();
//                    keyFirstFlag = false;
//                }

                return encodecData;

            } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                //  System.out.println("no output from encoder available");
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                outputBuffers = enCodec.getOutputBuffers();
                System.out.println("encoder output buffers changed");
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // not expected for an encoder
                MediaFormat newFormat = enCodec.getOutputFormat();
                System.out.println("encoder output format changed: " + newFormat);
            } else if (outputBufferIndex < 0) {
                System.out
                        .println("unexpected result from encoder.dequeueOutputBuffer: "
                                + outputBufferIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getInstance().logWithMethod(new Exception(), "AvcEncoder outputBuffers Exception", "Zhaolg");
            //累加
            errorCount++;
            if (errorCount > 10) {
                //不支持 切换成软编码
                errorCount = 0;
            }
        }
        return null;
    }

    public void requestKeyFrame() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (enCodec != null) {
                enCodec.setParameters(params);
            }
        }
        Log.i("oooooo", "requestKeyFrame: ");
    }

    @SuppressLint("NewApi")
    public void close() {
        try {
            //EnCodec.flush();
            enCodec.stop();
            enCodec.release();
            enCodec = null;
            encodeOutputData = null;
            inputBuffers = null;
            outputBuffers = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the first codec capable of encoding the specified MIME type, or
     * null if no match was found.
     */
    @SuppressLint("NewApi")
    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    YUVFormat.ENCODERNAME = codecInfo.getName();
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private List<MediaCodecInfo> selectCodecList(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        ArrayList<MediaCodecInfo> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    String name = codecInfo.getName();
                    if (name.startsWith("OMX.") && !name.startsWith("OMX.google.") || name.startsWith("c2")) {
                        list.add(0, codecInfo);
                    } else {
                        list.add(codecInfo);
                    }
                    stringBuilder.append(name).append(",");

                    Log.i(TAG, "selectCodecList: " + name);
                }
            }
        }
        if (!list.isEmpty()) {
            LogUtil.getInstance().logWithMethod(new Exception(), stringBuilder.toString(), "x");
        }
        return list;
    }

    /**
     * Returns a color format that is supported by the codec and by this test
     * code. If no match is found, this throws a test failure -- the set of
     * formats known to the test should be expanded for new platforms.
     */
    @SuppressLint("NewApi")
    private int selectColorFormat(MediaCodecInfo codecInfo,
                                  String mimeType) {
        try {

            MediaCodecInfo.CodecCapabilities capabilities = codecInfo
                    .getCapabilitiesForType(mimeType);

            for (int i = 0; i < capabilities.colorFormats.length; i++) {
                int colorFormat = capabilities.colorFormats[i];
                if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar) {
                    return colorFormat;
                }
            }

            for (int i = 0; i < capabilities.colorFormats.length; i++) {
                int colorFormat = capabilities.colorFormats[i];
                if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar) {
                    return colorFormat;
                }
            }

            for (int i = 0; i < capabilities.colorFormats.length; i++) {
                int colorFormat = capabilities.colorFormats[i];
                if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                    return colorFormat;
                }
            }

            for (int i = 0; i < capabilities.colorFormats.length; i++) {
                int colorFormat = capabilities.colorFormats[i];
                if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar) {
                    return colorFormat;
                }
            }


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("====getCapabilitiesForType failed ,try again=======");
        }
        return 0; // not reached
    }


    private long computePresentationTime(long nowTime) {

        return (long) ((nowTime - encodeBeginTime) * 1000);
    }

    private void inputCameraId(long ptsUsec, int cameraId) {

        TimeUsForwardId timeUsForwardId = new TimeUsForwardId();
        timeUsForwardId.setPresentationTimeUs(ptsUsec);
        timeUsForwardId.setCameraId(cameraId);

        synchronized (camerIdList) {
            try {
                camerIdList.put(timeUsForwardId);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void inputTimeUsTs(long ptsUsec, int ts) {
        TimeUsTs timeUsTs = new TimeUsTs();
        timeUsTs.setPresentationTimeUs(ptsUsec);
        timeUsTs.setTs(ts);
        synchronized (timeList) {
            try {
                timeList.put(timeUsTs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int outputCameraId(long ptsUsec) {
        synchronized (camerIdList) {
            int cameraId = videoDirection;
            if (currCount >= 3) {

                while (!camerIdList.isEmpty()) {

                    TimeUsForwardId forwardId = camerIdList.peek();//只取不删除
                    long presentationTimeUs = forwardId.getPresentationTimeUs();
                    if (ptsUsec != presentationTimeUs) {
                        if (ptsUsec == 0) {
                            break;
                        } else if (ptsUsec > presentationTimeUs) {
                            camerIdList.poll();//删除队列最前面的
                        }
                    } else {
                        cameraId = forwardId.getCameraId();
                        return cameraId;
                    }
                }
            }

            return cameraId;

        }
    }

    private int outputTs(long ptsUsec) {
        synchronized (timeList) {
            int ts = -1;
            if (currCount >= 3) {
                while (!timeList.isEmpty()) {
                    TimeUsTs timeUsTs = timeList.peek();
//            		System.out.println("======outputTs1111======"+timeUsTs.getPresentationTimeUs()+"==ptsUsec=="+ptsUsec+"==count=="+count);

                    if (ptsUsec != timeUsTs.getPresentationTimeUs()) {
                        timeList.poll();
                    } else {
                        ts = timeUsTs.getTs();
//                		System.out.println("======outputTs22222======"+count);

                        return ts;
                    }
                }
            }

            return ts;
        }

    }

    private void getYUVFile(byte[] data, String name) {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                name +
                ".yuv");
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

    private void getH264File(byte[] data) {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/AvcEncoder.264");
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
     * 处理时间戳：-1表示该图片不处理。
     */
    private int GetStampTime(long nowTime) {
        int ret_ts = (int) timestamp;

        //这里要进行丢图片的操作。
        if (timestamp > Integer.MAX_VALUE) {
            currCount = 0;
        }

        if (0 == currCount) {
            timestamp = 0;
            beginTime = nowTime;
            ret_ts = 0;
            lastTime = beginTime;
            currCount++;
        } else {

            if (nowTime > lastTime) {

                //先计算动态时戳
                long temptimestamp = (90000 / 1000) * (nowTime - lastTime);

                timestamp = timestamp + temptimestamp;
                //System.out.println("=========temptimestamp============"+temptimestamp+"======timestamp====="+timestamp+"===nowTime==="+nowTime+"==lastTime==="+lastTime);
                int frameIndex = (int) timestamp / m_tsPerS;
                //System.out.println("======frameIndex========"+frameIndex+"===currCount==="+currCount);
                if (frameIndex > currCount) {
                    ret_ts = frameIndex * m_tsPerS;
                    currCount = frameIndex;
                } else {
                    ret_ts = ((int) currCount + 1) * m_tsPerS;
                    currCount++;
                }
                //System.out.println("=timestamp===="+timestamp+"==ret_ts===="+ret_ts);
                //去除后两位
//        		ret_ts = (int) timestamp;
//        		currCount++;
                lastTime = nowTime;
            } else {
                lastTime = nowTime;
            }


        }


        return ret_ts;
    }

}