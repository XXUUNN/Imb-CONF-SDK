package com.microsys.poc.jni.utils;


import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.VideoRecvData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class AvcDecoderAsync {

    private static final int DEFAULT_H = 720;
    private static final int DEFAULT_W = 1280;

    private static final int CAPACITY = 15;

    MediaFormat decoderOutputFormat = null;

    private MediaCodec decoder = null;

    private static final String MIME_TYPE = "video/avc";

    //是否支持硬件解码
    private boolean hardWareIsSupport;
    //是否第一次进入
    private boolean isFirstTime = true;
    // 用于记录第一帧的时戳
    private long firstStampTime = 0;
    private long lastStampTime = 0;

    //本手机是否支持硬解码 0 不支持 1 支持
    public static int deCodecMode = 0;
    //解码后输入容器
    public byte[] inputData = null;

    /**
     * 区分两个解码器
     */
    private int id;

    private boolean isCreateByType = false;

    public static AvcDecoderAsync createDecoder() {
        AvcDecoderAsync avcDecoder = new AvcDecoderAsync(0, null);
        if (avcDecoder.isHardWareIsSupport()) {
            return avcDecoder;
        } else {
            return null;
        }
    }

    public static AvcDecoderAsync createDecoder(int id) {
        AvcDecoderAsync avcDecoder = new AvcDecoderAsync(id, null);
        if (avcDecoder.isHardWareIsSupport()) {
            return avcDecoder;
        } else {
            return null;
        }
    }


    public static AvcDecoderAsync createDecoder(int id, Callback callback) {
        AvcDecoderAsync avcDecoder = new AvcDecoderAsync(id, callback);
        if (avcDecoder.isHardWareIsSupport()) {
            return avcDecoder;
        } else {
            return null;
        }
    }

    @SuppressLint("NewApi")
    public AvcDecoderAsync(int id, Callback callback) {

        //小于16的手机都不能用 对应4.1
        if (Build.VERSION.SDK_INT < 16) {
            hardWareIsSupport = false;
            LogUtil.getInstance().logWithMethod(new Exception(), "the sdk is too low, must higher than 15 ", "Zhaolg");
            return;
        }

        this.id = id;

        if (callback != null) {
            videoDataList = new LinkedBlockingQueue<>(CAPACITY);
        }

        if (isCreateByType) {
            createByType(callback);
        } else {
            List<MediaCodecInfo> mediaCodecInfos = selectCodecList(MIME_TYPE);
            int size = mediaCodecInfos.size();
            if (size == 0) {
                System.out.println("Unable to find an appropriate codec for "
                        + MIME_TYPE);
                hardWareIsSupport = false;
                return;
            }
            boolean isCreateOk = false;
            for (int i = 0; i < size; i++) {
                MediaCodecInfo codecInfo = mediaCodecInfos.get(i);
                boolean isOk = createByCodecName(codecInfo, callback);
                if (isOk) {
                    //成功
                    isCreateOk = true;
                    break;
                }
            }
            if (isCreateOk) {
                hardWareIsSupport = true;
            } else {
                hardWareIsSupport = false;
            }
        }
    }

    private void setAsyncCallback(final Callback callback) {
        asyncCallback = callback;
    }

    private void createByType(Callback callback) {
        try {
            decoder = MediaCodec.createDecoderByType(MIME_TYPE);
            if (callback != null) {
                setAsyncCallback(callback);
            }
            configAndStart();
            if (asyncCallback != null) {
                startDecodecThread();
            }
            LogUtil.getInstance().logWithMethod(new Exception(), id + " createByType succeed_" + decoder.getName(), "Zhaolg");
            hardWareIsSupport = true;
        } catch (IOException e) {
            e.printStackTrace();
            hardWareIsSupport = false;
        }
    }

    private boolean createByCodecName(MediaCodecInfo codecInfo, Callback callback) {
        boolean isOk = false;
        //生成解码器
        try {
            decoder = MediaCodec.createByCodecName(codecInfo.getName());
            LogUtil.getInstance().logWithMethod(new Exception(), id + " createByCodecName succeed_" + codecInfo.getName(), "Zhaolg");

            if (callback != null) {
                setAsyncCallback(callback);
            }
            configAndStart();

            if (asyncCallback != null) {
                startDecodecThread();
            }

            isOk = true;
        } catch (Exception e) {
            LogUtil.getInstance().logWithMethod(e, "createByCodecName start failed, try to use createDecoderByType_" + codecInfo + "\n" + e.getMessage(), "Zhaolg");
            e.printStackTrace();
        }
        return isOk;
    }

    private void configAndStart() {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, DEFAULT_W, DEFAULT_H);
        decoder.configure(format, null, null, 0);
        decoder.start();
    }

    /**
     * reset config start
     */
    public void reStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            decoder.reset();
            configAndStart();
        }
    }

    public boolean isHardWareIsSupport() {
        return hardWareIsSupport;
    }

    @SuppressLint("NewApi")
    public void inputDecodecData(byte[] encodecData, int length, int width, int height, int stampTime) {
        if (isFirstTime) {
            firstStampTime = stampTime;
            lastStampTime = stampTime;
            isFirstTime = false;
        }

        try {
            ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
            int inputBufIndex = decoder.dequeueInputBuffer(-1);

            long ptsUsec = computePresentationTime(stampTime);

            if (inputBufIndex >= 0) {

                ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                inputBuf.clear();

                inputBuf.put(encodecData, 0, length);
                decoder.queueInputBuffer(inputBufIndex, 0, length,
                        ptsUsec, 0);
            }
        } catch (IllegalStateException e) {
            System.out.println("========捕获异常========");
            e.printStackTrace();
        }
    }

    /**
     * 获得解码后的输出流
     */

    @SuppressLint("NewApi")
    public VideoRecvData outputDecodecData() {

        ByteBuffer[] decoderOutputBuffers;
        try {
            decoderOutputBuffers = decoder.getOutputBuffers();
        } catch (Exception e) {
            LogUtil.getInstance().logWithMethod(new Exception(), "getOutputBuffers illegalStateException", "Zhaolg");
            e.printStackTrace();
            return null;
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        int decoderStatus = MediaCodec.INFO_TRY_AGAIN_LATER;
        try {
            decoderStatus = decoder.dequeueOutputBuffer(info, 0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (decoderStatus >= 0) {
            int colorFormat = 0;
            int width = 0;
            int height = 0;
            decoderOutputFormat = decoder.getOutputFormat();
            if (null != decoderOutputFormat) {
                colorFormat = decoderOutputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
                width = decoderOutputFormat.getInteger(MediaFormat.KEY_WIDTH);
                height = decoderOutputFormat.getInteger(MediaFormat.KEY_HEIGHT);
            }

            ByteBuffer outputFrame = decoderOutputBuffers[decoderStatus];

            outputFrame.position(info.offset);
            outputFrame.limit(info.offset + info.size);

            VideoRecvData recvData = new VideoRecvData();
            if (info.size == 0) {
                System.out.println("got empty frame");
            } else {
                if ((inputData == null) || (info.size > inputData.length)) {
                    inputData = new byte[info.size];
                }
                outputFrame.get(inputData, 0, info.size);
                //申请空间
                byte[] outputData = JniUtils.getInstance().allocMemory(id, width * height * 3 / 2);

                if (null != outputData) {
                    int length = YUVFormat.swapOtherToYUV420p(inputData, info.size, outputData, width, height, colorFormat, decoderOutputFormat);
                    recvData.setData(outputData);
                    recvData.setDataLen(length);
                } else {
                    recvData.setData(null);
                    recvData.setDataLen(0);
                }

                recvData.setWidth(width);
                recvData.setHeight(height);
                recvData.setCpTime(info.presentationTimeUs);
            }
            decoder.releaseOutputBuffer(decoderStatus, false /*render*/);

            return recvData;

        } else if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

//            System.out.println("no output from decoder available");
        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

            System.out.println("decoder output buffers changed");
            decoderOutputBuffers = decoder.getOutputBuffers();
        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

            decoderOutputFormat = decoder.getOutputFormat();
            int colorFormat = decoderOutputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);

        } else if (decoderStatus < 0) {
            System.out.println("unexpected result from deocder.dequeueOutputBuffer: " + decoderStatus);
        }

        return null;
    }


    /**
     * 变换一下时间戳  附带方向
     *
     * @param timestamp 原本的时间戳
     * @param direction 方向
     * @return 变换后的  时间戳*1000+方向(方向不超过3位数)
     */
    private long getTimestampWithDirection(long timestamp, int direction) {
        return timestamp * 1000 + direction;
    }

    /**
     * 时戳
     * 时戳的后三位
     *
     * @param timestamp 变换后的时间戳=解码器出来的数据的时间戳
     */
    private int getDirection(long timestamp) {
        int dir = (int) (timestamp % 1000);
        return dir;
    }

    private long getOriTimestamp(long timestamp) {
        return timestamp / 1000;
    }

    public void inputDecodecData1(byte[] encodecData, int length, long stampTime, int videoDirection) {
        try {
            ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
            int inputBufIndex = decoder.dequeueInputBuffer(-1);

            if (inputBufIndex >= 0) {

                ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                inputBuf.clear();
                inputBuf.put(encodecData, 0, length);

                long myTimestamp = getTimestampWithDirection(stampTime, videoDirection);

                decoder.queueInputBuffer(inputBufIndex, 0, length,
                        myTimestamp, 0);
            }
        } catch (IllegalStateException e) {
            System.out.println("========捕获异常========");
            e.printStackTrace();
        }
    }

    public VideoRecvData outputDecodecData1() {
        ByteBuffer[] decoderOutputBuffers;
        try {
            decoderOutputBuffers = decoder.getOutputBuffers();
        } catch (Exception e) {
            LogUtil.getInstance().logWithMethod(new Exception(), "getOutputBuffers illegalStateException", "Zhaolg");
            e.printStackTrace();
            return null;
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        int index = MediaCodec.INFO_TRY_AGAIN_LATER;
        try {
            index = decoder.dequeueOutputBuffer(info, 0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (index >= 0) {
            int colorFormat = 0;
            int width = 0;
            int height = 0;
            decoderOutputFormat = decoder.getOutputFormat();
            colorFormat = decoderOutputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
            width = decoderOutputFormat.getInteger(MediaFormat.KEY_WIDTH);
            height = decoderOutputFormat.getInteger(MediaFormat.KEY_HEIGHT);

            ByteBuffer outputFrame = decoderOutputBuffers[index];

            outputFrame.position(info.offset);
            outputFrame.limit(info.offset + info.size);

            long timestamp = info.presentationTimeUs;
            int direction = getDirection(timestamp);
            timestamp = getOriTimestamp(timestamp);

            VideoRecvData recvData = new VideoRecvData();
            if (info.size == 0) {
                System.out.println("got empty frame");
            } else {
                if ((inputData == null) || (info.size > inputData.length)) {
                    inputData = new byte[info.size];
                }
                outputFrame.get(inputData, 0, info.size);

                //申请空间
                byte[] outputData = JniUtils.getInstance().allocMemory(id, width * height * 3 / 2);

                if (null != outputData) {
                    int length = YUVFormat.swapOtherToYUV420p(inputData, info.size, outputData, width, height, colorFormat, decoderOutputFormat);
                    recvData.setData(outputData);
                    recvData.setDataLen(length);
                } else {
                    recvData.setData(null);
                    recvData.setDataLen(0);
                }

                recvData.setWidth(width);
                recvData.setHeight(height);
                recvData.setCpTime(timestamp);
                recvData.setDirection(direction);
            }
            if (decoder != null) {
                decoder.releaseOutputBuffer(index, false);
            }
            return recvData;

        } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
            //System.out.println("no output from decoder available");
        } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            System.out.println("decoder output buffers changed");
            decoderOutputBuffers = decoder.getOutputBuffers();
        } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            decoderOutputFormat = decoder.getOutputFormat();
            int colorFormat = decoderOutputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
        } else if (index < 0) {
            System.out.println("unexpected result from deocder.dequeueOutputBuffer: " + index);
        }

        return null;
    }

    @SuppressLint("NewApi")
    public void close() {
        if (asyncCallback != null) {
            stopDecodecThread();
        }
        try {
            decoder.stop();
            decoder.release();
            decoder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<MediaCodecInfo> selectCodecList(String mimeType) {
        ArrayList<MediaCodecInfo> list = new ArrayList<>();
        int numCodecs = MediaCodecList.getCodecCount();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    //最后选择OMX.google.的
                    String name = codecInfo.getName();
                    if (name.startsWith("OMX.") && !name.startsWith("OMX.google.")) {
                        list.add(0, codecInfo);
                    } else {
                        list.add(codecInfo);
                    }
                    stringBuilder.append(name).append("_");
                }
            }
        }
        if (!list.isEmpty()) {
            LogUtil.getInstance().logWithMethod(new Exception(), stringBuilder.toString(), "x");
        }
        return list;
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long stampTime) {

        if (stampTime < lastStampTime) {
            firstStampTime = stampTime;
        }
        lastStampTime = stampTime;

        return (long) (132 + (stampTime - firstStampTime) * 1000000 / 90000);
    }

    private void getYUVFile(byte[] data) {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + id +
                "_AvcDecoder.h264");
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

    private static final String TAG = AvcDecoderAsync.class.getSimpleName();

    private LinkedBlockingQueue<VideoRecvData> videoDataList;

    private Callback asyncCallback;

    private Future decodecFuture;
    private boolean isRunning;

    private void startDecodecThread() {
        isRunning = true;
        decodecFuture = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: decodec thread start");
                while (isRunning) {
                    //取数据
                    try {
                        VideoRecvData videoRecvDataCache = outputFromCache();
                        //进行解码
                        byte[] data = videoRecvDataCache.getData();

//                        getYUVFile(data);
                        inputDecodecData1(data, videoRecvDataCache.getDataLen(), videoRecvDataCache.getCpTime(), videoRecvDataCache.getDirection());

                        VideoRecvData recvData = outputDecodecData1();
                        while (null != recvData) {
                            if (recvData.getData() != null) {
                                try {
                                    asyncCallback.callback(recvData);
//                                    Log.i(TAG, "run: recvData " + id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            recvData = outputDecodecData1();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "run: decodec thread end");
            }
        });
    }

    private void stopDecodecThread() {
        isRunning = false;

        if (decodecFuture != null) {
            decodecFuture.cancel(true);
            decodecFuture = null;
        }
    }

    /**
     * 异步丢入数据
     * 开启线程去解码
     *
     * @param data      数据
     * @param len       长度
     * @param width     宽
     * @param height    高
     * @param stampTime 时间戳
     * @param videoDir  方向
     */
    public void asyncInput(byte[] data, int len, int width, int height, long stampTime, int videoDir) {
//        Log.i(TAG, "asyncInput: ");
        //填充入缓存
//        if (id == 1) {
//            Log.i(TAG, "asyncInput: "+ stampTime);
//        }

        byte[] temp = Arrays.copyOf(data, len);
        VideoRecvData videoRecvData = new VideoRecvData(temp, len, width, height, stampTime, videoDir);
        inputToCache(videoRecvData);
    }

    private void inputToCache(VideoRecvData videoRecvData) {
        if (!videoDataList.offer(videoRecvData)) {
            Log.i(TAG, "inputToCache fail , so poll");
            videoDataList.poll();
            videoDataList.offer(videoRecvData);
        }
    }

    private VideoRecvData outputFromCache() throws InterruptedException {
        VideoRecvData data = videoDataList.take();
//        if (id == 1) {
//            Log.i(TAG, "outputFromCache: "+ data.getCpTime());
//        }
        return data;
    }


    /**
     * 同一个解码器 可能会解码 不同的人的视频
     * 因此 切换时 会需要清理缓存
     */
    public void flush(){
        try {
            if (decoder != null) {
                decoder.flush();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        /**
         * 回调解码后的数据
         *
         * @param recvData 解码后的数据
         */
        void callback(VideoRecvData recvData);
    }
}