package com.microsys.poc.jni.show;

import android.os.SystemClock;
import android.util.Log;

import com.microsys.poc.jni.entity.VideoRecvData;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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


    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition = reentrantLock.newCondition();
    private volatile boolean isShowThreadStarted = false;
    private static final int CONDITION_SHOW_THREAD_START = 5;
    private static final int CONDITION_SHOW_THREAD_BLOCK = 2;
    /**
     * 用于保证清除缓存时，已经被take出来但是没有丢到显示方法的数据能主动丢弃掉
     */
    private AtomicBoolean clearVideoDataFlag = new AtomicBoolean();

    private long lastInputTime = -1;

    public void putVideoData(VideoRecvData data) {

//        Log.e(TAG, "putVideoData: " + (data.getDirection()));
        reentrantLock.lock();
        try {
            //先放入数据
            if (!videoDataList.offer(data)) {
                Log.i(TAG, "videoDataList: 满了");
                videoDataList.poll();
                videoDataList.offer(data);
            }
            if (!isShowThreadStarted) {
                //显示线程还没开始 那么就判断到条件 要唤醒 他
                if (videoDataList.size() == CONDITION_SHOW_THREAD_START) {
                    //达到条件那一刻 唤醒
                    Log.w(TAG, remoteVideoViewId + " putVideoData: 唤醒启动显示线程");
                    condition.signal();
                }
            } else {
                //显示线程开始了
                if (videoDataList.size() == CONDITION_SHOW_THREAD_BLOCK + 1) {
                    //显示线程 从 阻塞的条件到了不需要阻塞的条件
//                    Log.w(TAG, remoteVideoViewId + " putVideoData: 唤醒因包太少阻塞的显示线程");
                    condition.signal();
                }
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    private VideoRecvData takeVideoData() throws InterruptedException {
        if (!isShowThreadStarted) {
            //没开启的状态 阻塞
            reentrantLock.lock();
            try {
                Log.w(TAG, remoteVideoViewId + " takeVideoData: 显示线程正在等待启动");
                condition.await();
                //warn 这里wait即使被打断 异常处理也【在外面处理】 也会重新进入这个等待状态 除非是唤醒

                Log.w(TAG, remoteVideoViewId + " takeVideoData: 显示线程等待启动---已启动");

                //被唤醒了 说明 放入的到一定条件了
                isShowThreadStarted = true;

                //要执行取缓存的逻辑了
                if (videoDataList.size() > CONDITION_SHOW_THREAD_BLOCK) {
                    //不需要阻塞
                    return realTakeData();
                } else {
                    //warn 1.配置的预先启动的缓存个数比后续要求的缓存个数大
                    //warn 2.线程打断异常也会抛在外面
                    //warn 因此这里不会执行的。除非【1】中配置的个数不对

                    Log.e(TAG, "takeVideoData: videoShow condition config error");
                    throw new RuntimeException("videoShow condition config error");
                }

            } finally {
                reentrantLock.unlock();
            }

        } else {
            //如果已经开始了 那么就只要判断 是不是缓存数量少到了 需要阻塞的情况
            reentrantLock.lock();
            try {
                while (isRunning) {
                    if (videoDataList.size() <= CONDITION_SHOW_THREAD_BLOCK) {
                        //阻塞 等待放入唤醒
//                        Log.w(TAG, remoteVideoViewId + " takeVideoData: 包太少了 显示线程阻塞");
                        condition.await();
//                        Log.w(TAG, remoteVideoViewId + " takeVideoData: 包够了 显示线程阻塞---没了");
                        //被唤醒 就可以取了(就只有这一个线程在取，不用再次判断缓存个数了)
                        //warn 有清空数据clear 这里也要判断 直到线程要停止或者达到条件

                    } else {
                        break;
                    }
                }
                //直接取
                return realTakeData();
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    /**
     * 根据时戳增量判断了帧的显示间隔时间（写死了一秒90000）
     * 必须保证缓存里有两个以上
     *
     * @return 根据时间取出的数据
     */
    private VideoRecvData realTakeData() throws InterruptedException {
        VideoRecvData take;
        VideoRecvData peek;
        take = videoDataList.take();
        //取出了 最新的
        peek = videoDataList.peek();
        long space = peek.getCpTime() - take.getCpTime();
        if (space > 0) {
            //说明时戳递增 计算多少时间后显示下一个
            long ms = 1000 * space / 90000;
//            Log.e(TAG, space + "takeVideoData: " + ms);
            if (ms > 90) {
                //可能是不同的流 直接显示吧 不sleep了
            } else {
                //查看上一次
                if (lastInputTime < 0) {
                    //第一次 不延迟直接出
                } else {
                    long curInputTime = lastInputTime + ms;
                    if (ms - 2 > 0) {
                        ms = ms - 2;
                        try {
                            Thread.sleep(ms);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "realTakeData: sleep 被打断 1清理缓存2线程结束");
                            if (clearVideoDataFlag.compareAndSet(true, false)) {
                                //说明执行了clear了 那么这个已经take出来的就失效了
                                Log.e(TAG, "realTakeData:【1】 has cleared ,so error");
                                return null;
                            }
                            if (!isRunning) {
                                return null;
                            }
                        }
                    }
                    //稍微快毫秒
                    curInputTime -= 0;
                    //还有最多2 毫秒 就到了时间点 直接while循环判断时间
                    for (; isRunning; ) {
                        long curTime = getCurTime();
//                        Log.w(TAG, "takeVideoData: " + curTime + "_" + curInputTime);
                        if (curTime >= curInputTime) {
                            break;
                        }
                    }
                }
            }
        } else {
            //异常 或者不同的流 也直接显示吧
        }
        //记录当前放入数据的时间
        lastInputTime = getCurTime();

        if (clearVideoDataFlag.compareAndSet(true, false)) {
            //说明执行了clear了 那么这个已经take出来的就失效了
            Log.e(TAG, "realTakeData:【2】 has cleared ,so error");
            return null;
        }

        Log.e(TAG, "realTakeData: "+space);
        return take;
    }

    public void clearVideoData() {
        //进行了清除缓存操作
        clearVideoDataFlag.set(true);
        //打断sleep
        showVideoTask.cancel(true);
        videoDataList.clear();
        if (memPool != null) {
            memPool.resetMemery();
        }
    }

    private long getCurTime() {
        return SystemClock.elapsedRealtime();
    }

//    public void putVideoData(VideoRecvData data) {
//        if (!videoDataList.offer(data)) {
//            Log.i(TAG, "videoDataList: 满了");
//            videoDataList.poll();
//            videoDataList.offer(data);
//        }
//    }
//
//    private VideoRecvData takeVideoData() throws InterruptedException {
//        return videoDataList.take();
//    }
//
//    public void clearVideoData() {
//        videoDataList.clear();
//
//        if (memPool != null) {
//            memPool.resetMemery();
//        }
//    }

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


}
