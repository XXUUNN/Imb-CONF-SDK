package com.microsys.poc.jni.show;

import android.util.Log;

import com.microsys.poc.jni.entity.VideoRecvData;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author - gongxun;
 * created on 2020/4/20-19:22;
 * description - 统一管理显示视频
 */
public class VideoShowManager {
    private static final String TAG = VideoShowManager.class.getSimpleName();

    private int remoteVideoViewId;

    private MemPool memPool;
    private LinkedBlockingQueue<VideoRecvData> videoDataList = new LinkedBlockingQueue<>(15);

    private Future<?> showVideoTask;
    private volatile boolean isRunning = false;

    public VideoShowManager(int remoteVideoViewId) {
        this.remoteVideoViewId = remoteVideoViewId;
    }

    public RemoteVideoView createRemoteVideoView(int w, int h, int screenOrientation) {
        RemoteVideoView instance = RemoteViewManager.createInstance(remoteVideoViewId, w, h, screenOrientation);
        return instance;
    }


    private void startVideoShowTask() {
        isRunning = true;
        showVideoTask = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "showVideoTask " + remoteVideoViewId + " start: ");
                while (isRunning) {

                    VideoRecvData vData;
                    byte[] dataTmp;
                    int width, height;

                    try {
                        vData = takeVideoData();
                        dataTmp = vData.getData();
                        width = vData.getWidth();
                        height = vData.getHeight();

                        if (dataTmp != null) {
                            showVideo(vData, dataTmp, width, height);

                            if (null != memPool) {
                                // 用完后重新放回队列
                                releaseSpaceToMemory(dataTmp);
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "showVideoTask " + remoteVideoViewId + " end: ");
            }
        });
    }

    private void showVideo(VideoRecvData vData, byte[] dataTmp, int width, int height) {
        RemoteVideoView remoteVideoView = RemoteViewManager.adaptiveGet(remoteVideoViewId);
        if (remoteVideoView != null) {
            remoteVideoView.showVideo(
                    dataTmp, vData.getDataLen(), width,
                    height, vData.getDirection());
        }
    }

    private void stopVideoShowTask() {
        isRunning = false;
        if (showVideoTask != null) {
            showVideoTask.cancel(true);
            showVideoTask = null;
        }
    }

    private void releaseSpaceToMemory(byte[] data) {
        // 用完后重新放回队列
        memPool.releaseMemery(data);
    }

    public byte[] getSpaceFromMemory(int size) {
        if (memPool != null) {
            byte[] data = memPool.allocMemory(size);
            return data;
        } else {
            return null;
        }
    }

    public void putVideoData(VideoRecvData data) {
        if (!videoDataList.offer(data)) {
            Log.i(TAG, "videoDataList: 满了");
            videoDataList.poll();
            videoDataList.offer(data);
        }
    }

    private VideoRecvData takeVideoData() throws InterruptedException {
        return videoDataList.take();
    }

    //开始相关资源
    public void start() {
        memPool = new MemPool();

        startVideoShowTask();
    }

    public void stop() {
        memPool = null;

        stopVideoShowTask();

        RemoteViewManager.destroyInstance(remoteVideoViewId);
    }

    public void clearVideoData() {
        videoDataList.clear();

        if (memPool != null) {
            memPool.resetMemery();
        }
    }

}
