package com.imb.sdk.center;

import android.os.Looper;
import android.util.Log;

import com.imb.sdk.data.response.LoginAccontInfo;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;

/**
 * @author - gongxun;
 * created on 2019/7/10-18:31;
 * description - 同步账户状态 账户的最新信息 需要在app登录期间处在运行状态 for center
 * 1.账户的状态 2.token是否有效 3.最新的预约会议信息
 * 保证智能中心显示账户是在线的
 */
public class SyncAccountStateUtils {
    private static final String TAG = "SyncAccountStateUtils";
    private static Future<?> task;
    private static Call call;

    private static volatile boolean isRunning;

    private static int capacity;
    private static int accountType;

    /**
     * 开始同步账户的信息
     * 登录成功后
     */
    public static void start() {
        if (isRunning) {
            return;
        }
        //复用了线程后 导致了 Loop退出后在loop()失效，改用每次新建线程池
        task = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    return;
                }
                isRunning = true;

//                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
                Log.i(TAG, "start " + Thread.currentThread().getId());
                Looper looper = Looper.myLooper();
                if (looper == null) {
                    Looper.prepare();
                }
                Log.i(TAG, "loop");
                call = ImbHttpClient.getAccountInfo(new ImbHttpClient.Callback<LoginAccontInfo>(LoginAccontInfo.class, Looper.myLooper()) {
                    @Override
                    public void onFailure(int code, String message) {
                        Log.i(TAG, "onFailure: ");

                        if (isRunning) {
                            //是否token、失效
                            if (code == ImbHttpClient.RESPONSE_CODE_BUSINESS_TOKEN_ERROR) {
                                if (accountStateChangeListener != null) {
                                    accountStateChangeListener.onTokenInvalid();
                                }
                            } else {
                                call = ImbHttpClient.getAccountInfo(this, false);
                            }
                        } else {
                            Looper.myLooper().quit();
                            Log.i(TAG, "onFailure: exit");
                        }
                    }

                    @Override
                    public void onSuccess(LoginAccontInfo result) {
                        Log.i(TAG, "onSuccess: ");
                        if (isRunning) {
                            //解析数据
                            parseLoginInfo(result);
                            call = ImbHttpClient.getAccountInfo(this, false);
                        } else {
                            Log.i(TAG, "onSuccess: exit");
                            Looper.myLooper().quit();
                        }
                    }
                }, false);

                Looper.loop();
                Log.i(TAG, "exit Looper 线程结束");
            }
        });

    }

    /**
     * 停止同步账户的信息
     * 注销登录时 app结束时
     */
    public static void stop() {
        Log.i(TAG, "stop: ");
        isRunning = false;
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        if (call != null) {
            call.cancel();
        }
    }

    /**
     * 立即获取最新的账户最新信息
     */
    public static void syncNow() {
        ImbHttpClient.Callback<LoginAccontInfo> callback = new ImbHttpClient.Callback<LoginAccontInfo>(LoginAccontInfo.class, Looper.myLooper()) {
            @Override
            public void onFailure(int code, String message) {
                //是否token、失效
                if (code == ImbHttpClient.RESPONSE_CODE_BUSINESS_TOKEN_ERROR) {
                    if (accountStateChangeListener != null) {
                        accountStateChangeListener.onTokenInvalid();
                    }
                }
            }

            @Override
            public void onSuccess(LoginAccontInfo result) {
                Log.i(TAG, "onSuccess: syncNow");
                //解析数据
                parseLoginInfo(result);
            }
        };
        ImbHttpClient.getAccountInfo(callback, true);
    }

    /**
     * 获取只能中心传过来的登录数据
     */
    private static void parseLoginInfo(LoginAccontInfo info) {
        LoginAccontInfo.DataBean data = info.getData();
        if (data == null) {
            return;
        }
        List<LoginAccontInfo.DataBean.InstanceDetailVOSBean> instanceDetailVOS = data.getInstanceDetailVOS();
        if (instanceDetailVOS == null || instanceDetailVOS.size() == 0) {
            return;
        }
        LoginAccontInfo.DataBean.InstanceDetailVOSBean result = instanceDetailVOS.get(0);
        if (result == null) {
            return;
        }

        //账户类型和容量
        int localCapacity = result.getCapacity();
        if (capacity != localCapacity) {
            //容量变化了 通知
            if (accountStateChangeListener != null) {
                accountStateChangeListener.onCapacityChanged(localCapacity);
            }
            capacity = localCapacity;
        }
        //账户类型
        if (result.getExpireType() == 0) {
            //正常 没过期
            if (data.getUserType() == 0) {
                //只可以查看
                if (accountType != AccountState.STATE_ONLY_LOOK) {
                    //发出通知 类型变更
                    if (accountStateChangeListener != null) {
                        accountStateChangeListener.onAccountTypeChanged(AccountState.STATE_ONLY_LOOK);
                    }
                    accountType = AccountState.STATE_ONLY_LOOK;
                }
            } else if (data.getUserType() == 1) {
                //正常可以发起电话的账户
                if (accountType != AccountState.STATE_NORMAL) {
                    //发出通知 类型变更
                    if (accountStateChangeListener != null) {
                        accountStateChangeListener.onAccountTypeChanged(AccountState.STATE_NORMAL);
                    }
                    accountType = AccountState.STATE_NORMAL;
                }
            }
        } else {
            //过期
            if (accountType != AccountState.STATE_EXPIRED) {
                //发出通知 类型变更
                if (accountStateChangeListener != null) {
                    accountStateChangeListener.onAccountTypeChanged(AccountState.STATE_EXPIRED);
                }
                accountType = AccountState.STATE_EXPIRED;
            }
        }

//        //解析预约会议信息
//        int notifyCount = data.getNotifyCount();
//        ReservedMeetingUtils.checkReservedMeeting(notifyCount, data.getMeetingSubject(), data.getMeetingTime());
    }

    private static AccountStateChangeListener accountStateChangeListener;

    public static void setAccountStateChangeListener(AccountStateChangeListener accountStateChangeListener) {
        SyncAccountStateUtils.accountStateChangeListener = accountStateChangeListener;
    }

    /**
     * 账户状态变化
     */
    public interface AccountStateChangeListener {
        /**
         * 容量变了
         *
         * @param capacity 新的容量
         */
        void onCapacityChanged(int capacity);

        /**
         * 账户类型遍历
         *
         * @param type 新的类型
         * @see AccountState
         */
        void onAccountTypeChanged(@AccountState int type);

        /**
         * 登录token失效 此时需要重登
         */
        void onTokenInvalid();
    }
}
