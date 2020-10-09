package com.imb.sdk.listener;

import com.microsys.poc.jni.base.BaseTextMsgListener;

/**
 * @author - gongxun;
 * created on 2020/9/23-17:54;
 * description - 聊天消息
 */
public abstract class PocMessageListener extends BaseTextMsgListener {
    @Override
    protected void notifySendMessageFail(int channel) {
    }

    @Override
    protected void notifySendMessageSuc(int channel) {
    }
}
