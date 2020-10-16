package com.imb.sdk.manager;

import android.content.Context;

import com.imb.sdk.center.CenterLoginUtils;
import com.imb.sdk.center.ImbHttpClient;
import com.imb.sdk.data.entity.AccountInfo;
import com.imb.sdk.data.entity.AppFunctionConfig;
import com.imb.sdk.data.entity.PocLoginResult;
import com.imb.sdk.data.response.LogoutResponse;
import com.imb.sdk.login.PocLoginUtils;

import androidx.annotation.Nullable;

/**
 * @author - gongxun;
 * created on 2020/9/25-10:23;
 * description - 登陆管理
 */
public class LoginManager extends BaseManager {
    @Override
    public void init(Context context) {
        super.init(context);
        PocLoginUtils.getAndroidId(context);
    }

    public void loginCenter(String meetingNum, String name, String pwd, CenterLoginUtils.LoginCenterCallback callback) {
        CenterLoginUtils.loginCenter(meetingNum, name, pwd, callback);
    }

    public void loginCenterByToken(String token, CenterLoginUtils.LoginCenterCallback callback) {
        CenterLoginUtils.quickLogin(token, callback);
    }

    public void logoutCenter(@Nullable ImbHttpClient.Callback<LogoutResponse> callback) {
        CenterLoginUtils.logout(callback);
    }

    /**
     * 超时时间内阻塞的返回结果 需要在子线程执行
     * 可以配置是否同步通讯录
     *
     * @param appFunctionConfig 配置
     * @param accountInfo       账户信息
     * @return 登录结果
     * @see com.imb.sdk.data.entity.AppFunctionConfig.LoginConfig#enableSyncAddressBook(boolean)
     * @see com.imb.sdk.data.entity.AppFunctionConfig.LoginConfig#loginTimeOut
     */
    public PocLoginResult loginPoc(AppFunctionConfig appFunctionConfig, AccountInfo accountInfo) {
        return PocLoginUtils.login(appFunctionConfig, accountInfo);
    }

    /**
     * 在poc登录过程中可以停止登录 {@link #loginPoc(AppFunctionConfig, AccountInfo)}立即返回
     */
    public void stopLoginPoc() {
        PocLoginUtils.stopLogin();
    }

    /**
     * 登出poc
     */
    public void logoutPoc() {
        PocLoginUtils.logout();
    }
}
