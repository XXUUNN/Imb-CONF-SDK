package com.imb.sdk.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.imb.sdk.Poc;
import com.imb.sdk.addressbook.AddressBookSyncByHttp;
import com.imb.sdk.addressbook.AddressBookSyncUtils;
import com.imb.sdk.addressbook.SftpUtils;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.data.entity.AccountInfo;
import com.imb.sdk.data.entity.AppFunctionConfig;
import com.imb.sdk.data.entity.PocLoginResult;
import com.imb.sdk.listener.PocRegisterListener;
import com.imb.sdk.listener.PocSyncAddressBookListener;
import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.SyncAddressBookResult;
import com.microsys.poc.jni.utils.AvcDecoderAsync;
import com.microsys.poc.jni.utils.AvcEncoder;
import com.microsys.poc.jni.utils.LogUtil;
import com.microsys.poc.jni.utils.TimeUtils;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.core.app.ActivityCompat;
import okhttp3.Call;

/**
 * @author - gongxun;
 * created on 2020/9/25-12:07;
 * description - 登录
 */
public class PocLoginUtils {
    public static final String TAG = PocLoginUtils.class.getSimpleName();

    private static String singleId;

    /**
     * 是否正在登陆
     * 防止多线程执行了
     */
    private volatile static boolean isOnLogin = false;

    private static LoginCallableImpl1 loginCallable;

    public static void getAndroidId(Context context) {
        singleId = getSingleKey(context);
    }

    /**
     * 退出poc的登录状态
     */
    public static void logout() {
        JniUtils.getInstance().destroy();
    }

    /**
     * 取消登录
     */
    public static void stopLogin() {
        if (isOnLogin) {
            if (loginCallable != null) {
                loginCallable.stopLogin();
                loginCallable = null;
            }
            isOnLogin = false;
        }
    }

    /**
     * block mode. must run in a new thread
     *
     * @param appFunctionConfig 功能的配置
     * @param accountInfo       登录的账户的配置
     * @return 登录结果
     */
    public static PocLoginResult login(AppFunctionConfig appFunctionConfig, AccountInfo accountInfo) {
        if (isMainThread()) {
            throw new RuntimeException("can not run in main thread");
        }
        if (isOnLogin) {
            Log.e(TAG, "login: ing");
            return null;
        }
        isOnLogin = true;
        //检查配置
        if (!checkConfig(appFunctionConfig)) {
            isOnLogin = false;
            return null;
        }
        if (!checkAccount(accountInfo)) {
            isOnLogin = false;
            return null;
        }
        //应用配置
        applyConfig(appFunctionConfig);
        //执行登录
        loginCallable = new LoginCallableImpl1(appFunctionConfig, accountInfo);
        Future<PocLoginResult> task = Executors.newSingleThreadExecutor().submit(loginCallable);
        try {
            PocLoginResult result = task.get();
            isOnLogin = false;
            loginCallable = null;
            return result;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isOnLogin = false;
        loginCallable = null;
        return new PocLoginResult(PocConstant.RegisterResult.RESULT_UNKOWN,
                ResponseTranslateUtils.loginResultToDesc(PocConstant.RegisterResult.RESULT_UNKOWN));
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static boolean checkAccount(AccountInfo accountInfo) {
        if (accountInfo == null) {
            return false;
        }
        if (TextUtils.isEmpty(accountInfo.num)) {
            Log.i(TAG, "checkAccount: num error");
            return false;
        }
        if (TextUtils.isEmpty(accountInfo.name)) {
            accountInfo.name = accountInfo.num;
        }
        return true;
    }


    /**
     * 主要检测登录参数
     */
    private static boolean checkConfig(AppFunctionConfig appFunctionConfig) {
        if (appFunctionConfig == null) {
            return false;
        }
        AppFunctionConfig.LoginConfig loginConfig = appFunctionConfig.loginConfig;
        if (TextUtils.isEmpty(loginConfig.localIp)
                || TextUtils.isEmpty(loginConfig.serverIp)
                || loginConfig.serverPort == 0
                || loginConfig.registerExpireTime <= 0) {
            Log.e(TAG, "checkConfig: loginConfig error");
            return false;
        }
        if (loginConfig.loginTimeOut <= 0) {
            Log.e(TAG, "checkConfig: loginConfig loginTimeOut error");
            return false;
        }
        if (loginConfig.isEnableSyncAddressBook()) {
            if (loginConfig.getSftpPort() == 0 || TextUtils.isEmpty(loginConfig.getSftpUserName())
                    || TextUtils.isEmpty(loginConfig.getSftpUserPwd())
                    || TextUtils.isEmpty(loginConfig.getDirOnServer())
                    || TextUtils.isEmpty(loginConfig.getTempFileParentDir())) {
                Log.e(TAG, "checkConfig: loginConfig sftp error");
                return false;
            }
        }
        if (TextUtils.isEmpty(loginConfig.versionName)) {
            Log.e(TAG, "checkConfig: loginConfig versionName error. such as 1.1.1");
            return false;
        }
        return true;
    }

    private static void applyConfig(AppFunctionConfig appFunctionConfig) {
        AppFunctionConfig.LogConfig logConfig = appFunctionConfig.logConfig;
        if (logConfig.isFileLogEnable) {
            if (!TextUtils.isEmpty(logConfig.logDir)) {
                final File file = new File(logConfig.logDir);
                if (!file.exists()) {
                    Log.e(TAG, "applyConfig: file logConfig error.dir not exists");
                } else {
                    //日志
                    final File dirFile = file;
                    final int maxSize = logConfig.maxSizeBytes;
                    if (maxSize > 0) {
                        new Thread("logDirCheck") {
                            @Override
                            public void run() {
                                Log.i(TAG, "deleteFileIfSizeMax: start " + dirFile.getPath());
                                FileUtils.deleteFileIfSizeMax(dirFile, maxSize);
                                Log.i(TAG, "deleteFileIfSizeMax: end " + dirFile.getPath());
                            }
                        }.start();
                    }

                    LogUtil.getInstance().start(logConfig.logDir + File.separator + TimeUtils.currTime() + ".txt");
                    Log.i(TAG, "applyConfig: logConfig error dir not exists");
                }
            }
        }
        //声音配置
        AppFunctionConfig.VoiceConfig voiceConfig = appFunctionConfig.voiceConfig;
        JniUtils.getInstance().PocSetSendFrame(voiceConfig.samplePerChn, voiceConfig.sampleRateHz,
                voiceConfig.channelNum, voiceConfig.volumeSent);

        JniUtils.getInstance().PocSetRecvFrame(voiceConfig.samplePerChn, voiceConfig.sampleRateHz,
                voiceConfig.channelNum);
        JniUtils.getInstance().PocSetStreamArg(voiceConfig.streamDelay, voiceConfig.streamAnalogLevel);
        JniUtils.getInstance().PocSetAgcArg(voiceConfig.levelDB, voiceConfig.gainDB);
        JniUtils.getInstance().PocInitAecm(voiceConfig.aecmType, voiceConfig.aecmEnable, voiceConfig.aecmMode);
        JniUtils.getInstance().PocInitNs(voiceConfig.nsEnable, voiceConfig.nsMode);
        JniUtils.getInstance().PocInitAgc(voiceConfig.agcEnable, voiceConfig.agcMode);
        //视频配置
        AppFunctionConfig.VideoConfig videoConfig = appFunctionConfig.videoConfig;
        JniUtils.getInstance().PocSetQosArg(videoConfig.maxBitRate, videoConfig.sendCount,
                videoConfig.loopTime);
        JniUtils.getInstance().PocSetFecArg(videoConfig.rate, videoConfig.useuepprot,
                videoConfig.maxFrames, videoConfig.maskType);
        JniUtils.getInstance().PocSetVideoEnc(videoConfig.multipleThreadId,
                videoConfig.frameSkipEnable, videoConfig.sliceMode, videoConfig.mtu);
    }


    private static void configAvInfo(int height, int width, boolean isHardEnc,
                                     boolean isHardDec, int frameRate, int bitRate, int iFrameTime) {
        if (isHardDec) {
            AvcDecoderAsync.deCodecMode = 1;
        } else {
            AvcDecoderAsync.deCodecMode = 0;
        }
        if (isHardEnc) {
            AvcEncoder.enCodecMode = 1;
        } else {
            AvcEncoder.enCodecMode = 0;
        }
        int mode;
        if (AvcEncoder.enCodecMode == 1) {
            //硬编码
            if (AvcDecoderAsync.deCodecMode == 0) {
                //软解
                mode = 2;
            } else {
                mode = 6;
            }
        } else {
            //软编码
            if (AvcDecoderAsync.deCodecMode == 0) {
                //软解
                mode = 0;
            } else {
                mode = 4;
            }
        }
        Log.i("appUtils.configAvInfo", "configAvInfo: w" + width + "_h" + height);
        JniUtils.getInstance().PocSetVideoInfo("1", 1, 1,
                height, width,
                frameRate, bitRate, 0, iFrameTime, "176*144;320*240;352*288;480*320;640*480;720*480;800*480;800*600;864*480;960*540;960*720;1280*720;1536*1152;1920*1080;1920*1088;1920*1152;", "10;15;20;25;30;", mode);
    }

    private static boolean syncConfig(AccountInfo accountInfo, AppFunctionConfig appFunctionConfig) {
        //当前服务器的sip协议依赖
        JniUtils.getInstance().PocSetSipDealMode(appFunctionConfig.loginConfig.sipProtocolDependency);
        LogUtil.getInstance().logWithMethod(new Exception(), "num_" + accountInfo.num + "_name_" + accountInfo.name
                + "_ip_" + appFunctionConfig.loginConfig.serverIp
                + "_version_R" + appFunctionConfig.loginConfig.versionName + "/sftp"
                + "_key_" + singleId
                + "_port_" + appFunctionConfig.loginConfig.serverPort
                + "_localip_" + appFunctionConfig.loginConfig.localIp, "x");
        String phoneTelAes = AESCBCEncrypt.getInstance().encrypt("");
        int result = JniUtils.getInstance().PocClientInitConfig(appFunctionConfig.loginConfig.localIp,
                accountInfo.num, accountInfo.name, accountInfo.password,
                appFunctionConfig.loginConfig.serverIp,
                "R" + appFunctionConfig.loginConfig.versionName + "/sftp", singleId, appFunctionConfig.loginConfig.serverPort, phoneTelAes);
        LogUtil.getInstance().logWithMethod(new Exception(), "result" + result, "x");
        AppFunctionConfig.VideoConfig videoConfig = appFunctionConfig.videoConfig;
        configAvInfo(videoConfig.height, videoConfig.width,
                videoConfig.isHardVideoEnc,
                videoConfig.isHardVideoDec, videoConfig.frameRate, videoConfig.bitRate, videoConfig.iFrameTime);
        return result == 0;
    }

    /**
     * 登录
     */
    private static void syncLogin(AppFunctionConfig appFunctionConfig) {
        //发起注册
        JniUtils.getInstance().PocRegister(appFunctionConfig.loginConfig.registerExpireTime, null, null, "");
        LogUtil.getInstance().logWithMethod(new Exception(), "PocRegister()", "Zhaolg");
    }

    @SuppressLint("HardwareIds")
    private static String getSingleKey(Context context) {

        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(androidId)) {
            return "androidId_" + androidId;
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (tm != null) {
                String deviceId = null;
                try {
                    deviceId = tm.getDeviceId();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (!TextUtils.isEmpty(deviceId)) {
                        return "devid_" + deviceId;
                    }
                }

                String subscriberId = tm.getSubscriberId();
                if (!TextUtils.isEmpty(subscriberId)) {
                    return "imsi_" + subscriberId;
                }
            }
        }

        String serial;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = Build.getSerial();
        } else {
            serial = Build.SERIAL;
        }
        if (!TextUtils.isEmpty(serial)) {
            return "serialno_" + serial;
        }
        return null;
    }

    private static class LoginCallableImpl extends PocRegisterListener implements Callable<PocLoginResult> {
        private volatile boolean isRunning;

        /**
         * 是否正在同步通讯录
         */
        private volatile boolean isOnSyncAddressBook;

        private volatile boolean isRegisterOk;

        //记录poc登录失败的值 用于返回 另一边只读
        private volatile int pocResultCode;

        private AppFunctionConfig appFunctionConfig;
        private AccountInfo accountInfo;
        //记录是否超时
        private long loginStartTime = -1;
        private long timeOut;

        private SftpUtils sftp;
        private boolean isSftpConnected;
        /**
         * 自己上传的存放在服务器上的位置 删除的时候要用到
         */
        private String addressBookPathLocalOnServer;
        /**
         * 服务端通知的同步通讯录的结果
         * null 未收到服务端通知
         */
        private volatile SyncAddressBookResult syncAddressBookResult;
        private PocSyncAddressBookListener pocSyncAddressBookListener;
        private Thread curThread;


        public LoginCallableImpl(AppFunctionConfig appFunctionConfig, AccountInfo accountInfo) {
            this.appFunctionConfig = appFunctionConfig;
            this.accountInfo = accountInfo;

            timeOut = appFunctionConfig.loginConfig.loginTimeOut;

            isRunning = true;
        }

        public void stopLogin() {
            //先改变标志
            isRunning = false;
            //打断sleep 立即结束流程
            if (curThread.isAlive()) {
                curThread.interrupt();
            }
        }


        @Override
        public PocLoginResult call() {
            curThread = Thread.currentThread();
            if (!isRunning) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            //注册PoC
            registerRegisterListener();
            while (isRunning && !isLoginTimeOver() && !isRegisterOk) {
                boolean result = syncConfig(accountInfo, appFunctionConfig);
                if (result) {
                    //配置成功
                    JniUtils.getInstance().startProcessCallBack();
                    JniUtils.getInstance().startPocMainSipThread();

                    LogUtil.getInstance().logWithMethod(new Exception(), "Login() recvSipAndTbcpThread.start()", "Zhaolg");

                    try {
                        Thread.sleep(1000);//停顿1s等待线程启动
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //循环注册包
                    while (isRunning && !isLoginTimeOver() && !isRegisterOk) {
                        syncLogin(appFunctionConfig);

                        LogUtil.getInstance().logWithMethod(new Exception(), "2.5s循环poc register", "x");
                        //2.5s轮询
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                } else {
                    //1s轮询
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LogUtil.getInstance().logWithMethod(new Exception(), "1s循环poc config", "x");
            }
            unregisterRegisterListener();
            if (!isRunning) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            if (!isRegisterOk) {
                //如果登录失败直接返回
                return getLoginResult(pocResultCode);
            }
            //是否还需要看通讯录同步结果
            if (!isOnSyncAddressBook) {
                //成功返回
                return getLoginResult(PocConstant.RegisterResult.RESULT_SUCCESS);
            }
            //接着同步通讯录
            while (isRunning && !isLoginTimeOver() && !isSftpConnected) {
                connectSftp();
                //300ms一判断 这里没用连接成功的回调 就判断快一点
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //判断是取消了
            if (!isRunning) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            //判断是不没连接成功
            if (!isSftpConnected) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CONNECT_SFTP_ERROR);
            }
            //连接成功了 就继续同步 注册通讯录监听
            pocSyncAddressBookListener = new PocSyncAddressBookListener() {
                @Override
                protected void onReceivedAddressBookPath(SyncAddressBookResult result) {
                    syncAddressBookResult = result;
                    //立即向下执行
                    curThread.interrupt();
                }
            };
            registerSyncAddressBookListener();
            //发送消息同步 这个操作没必要循环执行 就等待结果就可以了
            addressBookPathLocalOnServer = AddressBookSyncUtils.syncAddressBookRequest(sftp, accountInfo.num,
                    appFunctionConfig.loginConfig.getDirOnServer(),
                    appFunctionConfig.loginConfig.getTempFileParentDir());
            //等待回调结果
            while (isRunning && !isLoginTimeOver() && syncAddressBookResult == null) {
                //100ms判断一下 到了后面步骤 剩余超时时间少了，最好判断细一些
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            unregisterSyncAddressBookListener();
            //判断是取消了
            if (!isRunning) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            //判断是不没收到服务端的通知结果
            if (syncAddressBookResult == null) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_SYNC_ADDRESS_BOOK_ERROR);
            }
            //处理通知的结果
            if (syncAddressBookResult.isSuccessful
                    && !TextUtils.isEmpty(syncAddressBookResult.diffAddressBookFilePathOnServer)) {
                //成功了 就处理通讯录
                String content = AddressBookSyncUtils.handleResponse(syncAddressBookResult.diffAddressBookFilePathOnServer,
                        addressBookPathLocalOnServer, appFunctionConfig.loginConfig.getTempFileParentDir(),
                        sftp, accountInfo.num);
                //返回结果
                return getLoginResult(PocConstant.RegisterResult.RESULT_SUCCESS, content);
            } else {
                //失败返回
                return getLoginResult(PocConstant.RegisterResult.RESULT_SYNC_ADDRESS_BOOK_ERROR,
                        syncAddressBookResult.reason);
            }
        }

        private PocLoginResult getLoginResult(int code) {
            return getLoginResult(code, ResponseTranslateUtils.loginResultToDesc(code));
        }

        private PocLoginResult getLoginResult(int code, String msg) {
            //关闭sftp
            if (sftp != null) {
                sftp.close();
            }
            return new PocLoginResult(code, msg);
        }

        private boolean isLoginTimeOver() {
            if (loginStartTime == -1) {
                loginStartTime = System.currentTimeMillis();
                return false;
            } else {
                return System.currentTimeMillis() - loginStartTime > timeOut;
            }
        }

        private void unregisterRegisterListener() {
            Poc.unregisterListener(this);
        }

        private void registerRegisterListener() {
            Poc.registerListener(this);
        }

        private void unregisterSyncAddressBookListener() {
            if (pocSyncAddressBookListener != null) {
                Poc.unregisterListener(pocSyncAddressBookListener);
            }
        }

        private void registerSyncAddressBookListener() {
            if (pocSyncAddressBookListener != null) {
                Poc.registerListener(pocSyncAddressBookListener);
            }
        }


        @Override
        protected void onRegisterResult(String num, int result) {
            if (!TextUtils.equals(num, accountInfo.num)) {
                //不是当前登录账户的名字
                return;
            }

            if (isRegisterOk) {
                return;
            }

            if (result == PocConstant.RegisterResult.RESULT_SUCCESS) {
                LogUtil.getInstance().logWithMethod(new Exception(), "PoC login success num=" + num, "x");

                //判断是否需要同步通讯录
                if (appFunctionConfig.loginConfig.isEnableSyncAddressBook()) {
                    isOnSyncAddressBook = true;
                }
                isRegisterOk = true;
                //标志更新完了 打断sleep 登录程序继续往下走
                curThread.interrupt();

            } else {
                isRegisterOk = false;

                pocResultCode = result;
            }
        }

        /**
         * 同步通讯录
         */
        private void connectSftp() {
            //连接sftp服务器
            if (sftp != null) {
                int state = sftp.getConnectState();
                if (state == 0) {
                    //改变标志
                    isSftpConnected = true;
                } else if (state == -2) {
                    //连接失败 重新new尝试连接
                    sftp = new SftpUtils(appFunctionConfig.loginConfig.serverIp, appFunctionConfig.loginConfig.getSftpPort(),
                            appFunctionConfig.loginConfig.getSftpUserName(), appFunctionConfig.loginConfig.getSftpUserPwd(),
                            null);
                } else if (state == -1) {
                    //未成功 sftp工具还在自动重连 继续循环判断
                }
            } else {
                sftp = new SftpUtils(appFunctionConfig.loginConfig.serverIp, appFunctionConfig.loginConfig.getSftpPort(),
                        appFunctionConfig.loginConfig.getSftpUserName(), appFunctionConfig.loginConfig.getSftpUserPwd(),
                        null);
            }
        }
    }

    private static class LoginCallableImpl1 extends PocRegisterListener implements Callable<PocLoginResult> {
        private volatile boolean isRunning;

        /**
         * 是否正在同步通讯录
         */
        private volatile boolean isOnSyncAddressBook;

        private volatile boolean isRegisterOk;

        //记录poc登录失败的值 用于返回 另一边只读
        private volatile int pocResultCode;

        private AppFunctionConfig appFunctionConfig;
        private AccountInfo accountInfo;
        //记录是否超时
        private long loginStartTime = -1;
        private long timeOut;

        /**
         * 服务端通知的同步通讯录的结果
         * -1 未收到服务端通知 请求中
         * 1 成功
         * 0 失败
         */
        private volatile int syncAddressBookResult = -1;
        private volatile String syncAddressBookMsg;
        private Thread curThread;


        public LoginCallableImpl1(AppFunctionConfig appFunctionConfig, AccountInfo accountInfo) {
            this.appFunctionConfig = appFunctionConfig;
            this.accountInfo = accountInfo;

            timeOut = appFunctionConfig.loginConfig.loginTimeOut;

            isRunning = true;
        }

        public void stopLogin() {
            //先改变标志
            isRunning = false;
            //打断sleep 立即结束流程
            if (curThread.isAlive()) {
                curThread.interrupt();
            }
        }

        @Override
        public PocLoginResult call() {
            curThread = Thread.currentThread();
            if (!isRunning) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            //注册PoC
            registerRegisterListener();
            while (isRunning && !isLoginTimeOver() && !isRegisterOk) {
                boolean result = syncConfig(accountInfo, appFunctionConfig);
                if (result) {
                    //配置成功
                    JniUtils.getInstance().startProcessCallBack();
                    JniUtils.getInstance().startPocMainSipThread();

                    LogUtil.getInstance().logWithMethod(new Exception(), "Login() recvSipAndTbcpThread.start()", "Zhaolg");

                    try {
                        Thread.sleep(1000);//停顿1s等待线程启动
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //循环注册包
                    while (isRunning && !isLoginTimeOver() && !isRegisterOk) {
                        syncLogin(appFunctionConfig);

                        LogUtil.getInstance().logWithMethod(new Exception(), "2.5s循环poc register", "x");
                        //2.5s轮询
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                } else {
                    //1s轮询
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LogUtil.getInstance().logWithMethod(new Exception(), "1s循环poc config", "x");
            }
            unregisterRegisterListener();
            if (!isRunning) {
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            if (!isRegisterOk) {
                //如果登录失败直接返回
                return getLoginResult(pocResultCode);
            }
            //是否还需要看通讯录同步结果
            if (!isOnSyncAddressBook) {
                //成功返回
                return getLoginResult(PocConstant.RegisterResult.RESULT_SUCCESS);
            }
            //接着同步通讯录
            String syncHttpUrl = AddressBookSyncByHttp.getSyncAddressBookUrl(appFunctionConfig.loginConfig.serverIp);
            AddressBookSyncByHttp.Callback callback = new AddressBookSyncByHttp.Callback() {
                @Override
                public void callback(boolean isOk, String msg) {
                    syncAddressBookResult = isOk ? 1 : 0;
                    syncAddressBookMsg = msg;
                }
            };
            Call addressBook = AddressBookSyncByHttp.getAddressBook(accountInfo.num, syncHttpUrl, callback);
            //等待成功结果 否则一直同步请求
            while (isRunning && !isLoginTimeOver() && syncAddressBookResult != 1) {
                //100ms判断一下 到了后面步骤 剩余超时时间少了，最好判断细一些
                if (syncAddressBookResult == -1) {
                    //还在请求中 不发起请求 继续等待结果
                } else if (syncAddressBookResult == 0) {
                    //反回了失败了 重新发起请求 标志置回请求中的状态
                    syncAddressBookResult = -1;
                    addressBook = AddressBookSyncByHttp.getAddressBook(accountInfo.num, syncHttpUrl, callback);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //判断是取消了
            if (!isRunning) {
                if (syncAddressBookResult < 0) {
                    //美收到结果
                    addressBook.cancel();
                }
                return getLoginResult(PocConstant.RegisterResult.RESULT_CANCEL);
            }
            //判断是不没收到服务端的通知结果
            if (syncAddressBookResult < 0) {
                addressBook.cancel();
                return getLoginResult(PocConstant.RegisterResult.RESULT_SYNC_ADDRESS_BOOK_ERROR);
            }
            //处理通知的结果
            if (syncAddressBookResult == 1) {
                //成功了 就处理通讯录
                //返回结果
                return getLoginResult(PocConstant.RegisterResult.RESULT_SUCCESS, syncAddressBookMsg);
            } else {
                //失败返回
                return getLoginResult(PocConstant.RegisterResult.RESULT_SYNC_ADDRESS_BOOK_ERROR,
                        syncAddressBookMsg);
            }
        }

        private PocLoginResult getLoginResult(int code) {
            return getLoginResult(code, ResponseTranslateUtils.loginResultToDesc(code));
        }

        private PocLoginResult getLoginResult(int code, String msg) {
            if (code != PocConstant.RegisterResult.RESULT_SUCCESS) {
                //登录失败 就清理掉刚开始可能开始了的库里的线程 库里收报循环超时500ms 这里延迟501保证退出循环
                logout();
                try {
                    Thread.sleep(501);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return new PocLoginResult(code, msg);
        }

        private boolean isLoginTimeOver() {
            if (loginStartTime == -1) {
                loginStartTime = System.currentTimeMillis();
                return false;
            } else {
                return System.currentTimeMillis() - loginStartTime > timeOut;
            }
        }

        private void unregisterRegisterListener() {
            Poc.unregisterListener(this);
        }

        private void registerRegisterListener() {
            Poc.registerListener(this);
        }

        @Override
        protected void onRegisterResult(String num, int result) {
            if (!TextUtils.equals(num, accountInfo.num)) {
                //不是当前登录账户的名字
                return;
            }

            if (isRegisterOk) {
                return;
            }

            if (result == PocConstant.RegisterResult.RESULT_SUCCESS) {
                LogUtil.getInstance().logWithMethod(new Exception(), "PoC login success num=" + num, "x");

                //判断是否需要同步通讯录
                if (appFunctionConfig.loginConfig.isEnableSyncAddressBook()) {
                    isOnSyncAddressBook = true;
                }
                isRegisterOk = true;
                //标志更新完了 打断sleep 登录程序继续往下走
                curThread.interrupt();

            } else {
                isRegisterOk = false;

                pocResultCode = result;
            }
        }
    }


}

