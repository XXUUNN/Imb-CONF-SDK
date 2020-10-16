package com.imb.imbdemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imb.sdk.center.CenterLoginUtils;
import com.imb.sdk.center.SyncAccountStateUtils;
import com.imb.sdk.center.UrlManager;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.data.entity.AccountInfo;
import com.imb.sdk.data.entity.AppFunctionConfig;
import com.imb.sdk.data.entity.CenterLoginResult;
import com.imb.sdk.data.entity.PocLoginResult;
import com.imb.sdk.login.PocLoginHeartBeatUtils;
import com.imb.sdk.manager.LoginManager;
import com.imb.sdk.manager.ManagerService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences configSp;
    private String pocNum;
    private String pocPwd;

    private String centerMeetingNum;
    private String centerName;
    private String centerPwd;
    private String centerHost;

    private AccountInfo accountInfo;
    private AppFunctionConfig appFunctionConfig;

    private Button loginBtn;
    private Button loginCenterBtn;
    private TextView configInfoTv;
    private TextView outTv;

    private BroadcastReceiver netBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configInfoTv = (TextView) findViewById(R.id.tv_config);
        loginBtn = (Button) findViewById(R.id.loginPocBtn);
        loginCenterBtn = (Button) findViewById(R.id.loginCenterBtn);

        outTv = (TextView) findViewById(R.id.tv_out);

        appFunctionConfig = new AppFunctionConfig();

        accountInfo = new AccountInfo();

        Constant.width = appFunctionConfig.videoConfig.width;
        Constant.height = appFunctionConfig.videoConfig.height;
        Constant.frameRate = appFunctionConfig.videoConfig.frameRate;
        Constant.bitRate = appFunctionConfig.videoConfig.bitRate;
        Constant.iFrameTime = appFunctionConfig.videoConfig.iFrameTime;

        setLoginConfig();

        initConfig();

        registerNetReceiver();

        PermissionUtils.create(this, 1).checkPermission(getApplicationContext(), new PermissionUtils.PermissionRequestCallback() {
            @Override
            public void granted(boolean isCalledInActivityResult) {
                setLogConfig();
                setSyncAddressBook();
            }

            @Override
            public void denied(List<String> deniedPermissionList) {

            }

            @Override
            public void deniedForever(List<String> deniedForeverPermissionList) {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE).request();
    }

    private void registerNetReceiver() {
        netBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络
                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络
                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    }

                    //更新网络变化 使心跳立即回调
                    PocLoginHeartBeatUtils.getInstance().updateNetConnection(true);
                } else {
                    //更新网络变化 使心跳立即回调
                    PocLoginHeartBeatUtils.getInstance().updateNetConnection(false);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netBroadcastReceiver, filter);
    }

    private void unregisterNetReceiver() {
        if (netBroadcastReceiver != null) {
            unregisterReceiver(netBroadcastReceiver);
            netBroadcastReceiver = null;
        }
    }

    private void setSyncAddressBook() {
        final AppFunctionConfig.LoginConfig loginConfig = appFunctionConfig.loginConfig;
        loginConfig.setSyncAddressBookConfig(2222, "sftpuser",
                "sftpuser", "/home/poc_addrlist", getExternalFilesDir("poc").getAbsolutePath());
        loginConfig.enableSyncAddressBook(false);
    }

    private void setLoginConfig() {
        final AppFunctionConfig.LoginConfig loginConfig = appFunctionConfig.loginConfig;
        loginConfig.serverIp = "47.111.29.93";
        loginConfig.serverPort = 6689;
        loginConfig.localIp = NetUtils.getLocalIp(this);
        loginConfig.versionName = "1.0.0";
    }

    private void setLogConfig() {
        appFunctionConfig.logConfig.isFileLogEnable = true;
        appFunctionConfig.logConfig.logDir = getExternalFilesDir("log").getAbsolutePath();
    }

    private void initConfig() {
        configSp = Sp.getSp(this);
        pocNum = configSp.getString(Sp.POC_NUM, null);
        pocPwd = configSp.getString(Sp.POC_PWD, null);

        accountInfo.num = pocNum;
        accountInfo.password = pocPwd;

        centerMeetingNum = configSp.getString(Sp.CENTER_MEETING_NUM, null);
        centerName = configSp.getString(Sp.CENTER_NAME, null);
        centerPwd = configSp.getString(Sp.CENTER_PWD, null);
        centerHost = configSp.getString(Sp.CENTER_HOST, null);

        UrlManager.setRequestHost(centerHost);

//        configInfoTv.setText(
//                "poc号码：" + pocNum +
//                        "\n" + "poc密码：" + pocPwd
//        );
        configInfoTv.setText("centerHost=" + centerHost + " centerMeetingNum=" + centerMeetingNum+ " centerName=" + centerName + " centerPwd=" + centerPwd
                + "\n" + appFunctionConfig.toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        initConfig();
    }

    /**
     * 登录poc
     */
    public void onPocLoginClick(View view) {
        loginPoc();
    }

    /*****************PoC login start*****************************/

    private volatile boolean isPocLoginIng = false;
    private boolean isPocLoginOk = false;

    private void loginPoc() {
        if (!isPocLoginIng) {
            if (isPocLoginOk) {
                //登上了已经

                //退出poc心跳保持
                PocLoginHeartBeatUtils.getInstance().stopHeartBeat();
                //退出登录
                LoginManager manager = (LoginManager) ManagerService.getManager(ManagerService.LOGIN_SERVICE);
                manager.logoutPoc();
                stopObservePoc();
                isPocLoginOk = false;
                loginBtn.setText("PoC登录");
                out("已退出PoC");
                Constant.myPocNum = null;
            } else {
                isPocLoginIng = true;
                loginBtn.setText("PoC登录中。。。");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoginManager manager = (LoginManager) ManagerService.getManager(ManagerService.LOGIN_SERVICE);
                        final PocLoginResult pocLoginResult = manager.loginPoc(appFunctionConfig, accountInfo);
                        isPocLoginIng = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (pocLoginResult == null) {
                                    loginBtn.setText("PoC登录失败 ");
                                    isPocLoginOk = false;
                                    out("PoC登录失败 config error");
                                } else {
                                    if (pocLoginResult.code == PocConstant.RegisterResult.RESULT_SUCCESS) {
                                        loginBtn.setText("PoC登录成功");

                                        Constant.myPocNum = accountInfo.num;

                                        //登上了 到了主界面就开始发送心跳 保持poc在线
                                        startObservePoc();

                                        isPocLoginOk = true;

                                    } else {
                                        loginBtn.setText("PoC登录失败 " + pocLoginResult.msg);
                                        isPocLoginOk = false;
                                    }
                                    out(pocLoginResult.msg);
                                }

                            }
                        });

                    }
                }).start();
            }
        } else {
            LoginManager manager = (LoginManager) ManagerService.getManager(ManagerService.LOGIN_SERVICE);
            manager.stopLoginPoc();
        }
    }

    private void startObservePoc() {
        PocLoginHeartBeatUtils.getInstance().setConnectionListener(new PocLoginHeartBeatUtils.PocServerConnectionListener() {
            @Override
            public void pocServerConnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        out("Poc连上了");
                    }
                });
            }

            @Override
            public void pocServerDisconnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        out("Poc断开了");
                    }
                });
            }
        });
        PocLoginHeartBeatUtils.getInstance().startPocHearBeat();
    }

    private void stopObservePoc() {
        PocLoginHeartBeatUtils.getInstance().stopHeartBeat();
    }

    /*****************PoC login end*****************************/

    /*****************Center login start*****************************/

    private boolean isCenterLoginOk = false;

    public void onCenterLoginClick(View view) {
        LoginManager manager = (LoginManager) ManagerService.getManager(ManagerService.LOGIN_SERVICE);
        if (isCenterLoginOk) {
            //不关心登出回调可以传null
            manager.logoutCenter(null);
            stopObserveCenter();
            isCenterLoginOk = false;
            out("已退出center");
            loginCenterBtn.setText("登录智能中心");
        } else {
            manager.loginCenter(centerMeetingNum,centerName, centerPwd, new CenterLoginUtils.LoginCenterCallback() {
                @Override
                public void onSuccess(CenterLoginResult result) {
                    out(result.toString());
                    isCenterLoginOk = true;

                    startObserveCenter();
                    loginCenterBtn.setText("智能中心登录成功");
                }

                @Override
                public void onFailure(int code, String message) {
                    out("" + code + "_" + message);
                }
            });
        }
    }

    /**
     * 登录上智能中心后 监听账户变化
     * 每次开始监听 都需要重新设置监听
     */
    private void startObserveCenter() {
        SyncAccountStateUtils.syncNow();
        SyncAccountStateUtils.setAccountStateChangeListener(new SyncAccountStateUtils.AccountStateChangeListener() {
            @Override
            public void onCapacityChanged(final int capacity) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        out("center账户容量变化" + capacity);
                    }
                });
            }

            @Override
            public void onAccountTypeChanged(final int type) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        out("center账户类型变化 " + type);
                    }
                });

            }

            @Override
            public void onTokenInvalid() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        out("token失效 退出了登录");

                        stopObserveCenter();
                        isCenterLoginOk = false;
                        loginCenterBtn.setText("登录智能中心");
                    }
                });
            }
        });
        SyncAccountStateUtils.start();
    }

    /**
     * 停止监听 app退出登录时需要调用
     */
    private void stopObserveCenter() {
        SyncAccountStateUtils.stop();
    }

    /*****************Center login end*****************************/

    private void out(String s) {
        Log.i("imbDemo", "out: " + s);
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        outTv.setText(s);
    }

    /**
     * 修改登录信息
     */
    public void onConfigEditClick(View view) {
        startActivity(new Intent(this, ConfigEditActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onPermissionCallback(1, requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        LoginManager manager = (LoginManager) ManagerService.getManager(ManagerService.LOGIN_SERVICE);
        //必须手动停止
        PocLoginHeartBeatUtils.getInstance().stopHeartBeat();
        manager.logoutPoc();

        //必须手动停止
        SyncAccountStateUtils.stop();
        manager.logoutCenter(null);

        unregisterNetReceiver();
        super.onDestroy();
    }

    public void onToMainClick(View view) {
        if (isPocLoginOk) {
            startActivity(new Intent(MainActivity.this, FunctionActivity.class));
        }else{
            Toast.makeText(this, "请先登录PoC", Toast.LENGTH_SHORT).show();
        }
    }
}
