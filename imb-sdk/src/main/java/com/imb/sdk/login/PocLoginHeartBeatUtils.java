package com.imb.sdk.login;

import android.util.Log;

import com.imb.sdk.Poc;
import com.imb.sdk.data.Constant;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.listener.PocRegisterListener;
import com.microsys.poc.jni.JniUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author - gongxun;
 * created on 2019/4/29-23:24;
 * description - 登录心跳 保持PoC是登录状态 登录进app后主界面启动时进行发送心跳 结束时关闭心跳
 */
public class PocLoginHeartBeatUtils extends PocRegisterListener {
    private static PocLoginHeartBeatUtils instance;

    private PocLoginHeartBeatUtils() {
    }

    synchronized
    public static PocLoginHeartBeatUtils getInstance() {
        if (instance == null) {
            instance = new PocLoginHeartBeatUtils();
        }
        return instance;
    }

    static {
        instance = new PocLoginHeartBeatUtils();
    }

    private AtomicInteger errorCount = new AtomicInteger();

    public static final int MAX_ERROR = 0;

    public static final int LONG_TIME = 60 * 1000;
    public static final int SHORT_TIME = 2 * 1000;

    private volatile PocServerConnectionListener connectionListener;

    private boolean isNetworkConnected = true;

    private volatile boolean isRunning;

    private int sleepTime;

    private Thread heartBeatTask;

    public synchronized void startPocHearBeat() {
        Log.i("loginHeartBeat", "startHeartBeat: isRunning=" + isRunning);
        if (isRunning) {
            return;
        }
        Poc.registerListener(this);
        createTask();
        run();
    }

    public synchronized void stopHeartBeat() {
        Log.i("loginHeartBeat", "stopHeartBeat: ");
        isRunning = false;
        Poc.unregisterListener(this);
        if (heartBeatTask != null) {
            heartBeatTask.interrupt();
            heartBeatTask = null;
        }
        connectionListener = null;
    }

    private void run() {
        if (heartBeatTask != null) {
            heartBeatTask.start();
        }
    }

    private void createTask() {
        heartBeatTask = new Thread(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    return;
                }
                isRunning = true;

//                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);

                int heartBeatTime = Constant.DEFAULT_POC_EXPIRE_TIME - 100;
                heartBeatTime = heartBeatTime < 20 ? 20 : heartBeatTime;
                int heartBeatMillis = heartBeatTime * 1000;
                //第一次进入是连接上的
                setSleepTime(true);
                while (isRunning) {
                    Log.i("loginHeartBeat", "run: " + sleepTime);
                    if (isNetworkConnected) {
                        JniUtils.getInstance().PocRegister(Constant.DEFAULT_POC_EXPIRE_TIME, null, null, "");
                    } else {
                        //断开
                        checkState(false);
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.i("loginHeartBeat", "InterruptedException: ");
                    }
                }
                Log.i("loginHeartBeat", "run over: ");
                isRunning = false;
            }
        });
    }

    /***
     * 注意：回调在子线程
     * @param connectionListener 连接状态回调
     */
    public void setConnectionListener(PocServerConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    private void checkState(boolean isConnected) {
        if (isConnected) {
            if (errorCount.get() != 0 || !isNormalSleepTime()) {
                setSleepTime(true);
                //成功
                errorCount.set(0);

                if (connectionListener != null) {
                    connectionListener.pocServerConnected();
                }
            }

        } else {
            if (isRunning) {
                int count = errorCount.incrementAndGet();
                if (count > MAX_ERROR) {

                    setSleepTime(false);

                    if (connectionListener != null) {
                        connectionListener.pocServerDisconnected();
                    }
                }
            }
        }
    }

    /**
     * 发送一个sip info消息到服务端，当网咯从不可用切换到可用时
     */
    public void sendMsgToServerWhenNetOk(){
        JniUtils.getInstance().PocSendSipInfoNULL();
    }

    /**
     * 立即发送快速心跳
     * 当屏幕暗变亮或者切换到有网络的状态时需要立即发送保证连接
     */
    public void heartBeatNow() {
        if (!check()) {
            return;
        }
        setSleepTime(false);
        heartBeatTask.interrupt();
    }

    /**
     * 有网络变化时更新
     *
     * @param isConnected true 网络连接
     */
    public void updateNetConnection(boolean isConnected) {
        isNetworkConnected = isConnected;
        Log.i("loginHeartBeat", "eventNetChanged: " + isConnected);
        if (!check()) {
            return;
        }
        if (isConnected) {
            //网络可用 直接请求
            heartBeatTask.interrupt();
        } else {
            setSleepTime(false);
            heartBeatTask.interrupt();
        }
    }

    private boolean check() {
        if (heartBeatTask != null) {
            if (heartBeatTask.isAlive()) {
                return true;
            }
        }
        Log.i("loginHeartBeat", "check: false");
        return false;
    }

    private void setSleepTime(boolean isPocConnected) {
        if (isPocConnected) {
            sleepTime = LONG_TIME;
        } else {
            sleepTime = SHORT_TIME;
        }
    }

    private boolean isNormalSleepTime() {
        if (sleepTime == LONG_TIME) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onRegisterResult(String num, int result) {
        checkState(result == PocConstant.RegisterResult.RESULT_SUCCESS);
    }

    public interface PocServerConnectionListener {
        /**
         * 与PoC服务器连接上
         */
        void pocServerConnected();

        /**
         * 与PoC服务器断开连接
         */
        void pocServerDisconnected();
    }
}
