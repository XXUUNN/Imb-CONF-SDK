package com.imb.sdk.manager;

import android.content.Context;

import com.imb.sdk.msg.MessageUtils;

/**
 * @author - gongxun;
 * created on 2020/9/25-10:23;
 * description - 处理消息
 */
public class MsgManager extends BaseManager {
    @Override
    public void init(Context context) {
        super.init(context);
        MessageUtils.getInstance();
    }

    public void sendTxtMessage(String targetNum, String content, MessageUtils.MessageCallback callback) {
        MessageUtils.getInstance().sendTxtMessage(targetNum, content, callback);
    }
}
