package com.imb.sdk.manager;

import android.content.Context;

import com.imb.sdk.login.LoginUtils;

/**
 * @author - gongxun;
 * created on 2020/9/25-10:23;
 * description - 登陆管理
 */
public class LoginManager extends BaseManager {
    @Override
    public void init(Context context) {
        super.init(context);
        LoginUtils.getAndroidId(context);
    }
}
