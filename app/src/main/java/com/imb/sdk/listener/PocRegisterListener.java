package com.imb.sdk.listener;

import com.microsys.poc.jni.base.BaseSipMsgListener;

/**
 * @author - gongxun;
 * created on 2020/9/23-17:23;
 * description -
 */
public abstract class PocRegisterListener extends BaseSipMsgListener {

    @Override
    protected void onCallHangUp() {
    }

    @Override
    protected void onCallOutSucess() {
    }

    @Override
    protected void onStopPlayRing() {
    }

    @Override
    protected void onPlayRing() {
    }

    @Override
    protected void onCallFail(int callChannel, boolean isCallOut) {
    }

    @Override
    protected void onReceivedHalfVideoCall(String callTel, int callChannel) {

    }

    @Override
    protected void onReceivedFullVideoCall(String callTel, int callChannel) {
    }

    @Override
    protected void onReceivedFullVoiceCall(String callTel, int callChannel) {
    }

    @Override
    protected void onReceivedHalfVoiceCall(String callTel, int callChannel) {

    }
}
