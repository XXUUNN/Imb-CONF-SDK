package com.imb.sdk.listener;

import com.microsys.poc.jni.base.BaseSipMsgListener;

/**
 * @author - gongxun;
 * created on 2020/9/23-17:43;
 * description -
 */
public abstract class PocCallListener extends BaseSipMsgListener {
    @Override
    protected void onRegisterResult(String num, int result) {
    }
}
