package com.imb.sdk.manager;

import android.content.Context;

import com.imb.sdk.login.PocLoginUtils;

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

    f
}
