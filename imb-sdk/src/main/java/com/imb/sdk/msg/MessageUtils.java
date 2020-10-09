package com.imb.sdk.msg;

import com.imb.sdk.Poc;
import com.imb.sdk.listener.PocMessageListener;
import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.type.PocCallType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author - gongxun;
 * created on 2019/4/29-16:15;
 * description - 发送消息
 */
public class MessageUtils extends PocMessageListener {
    public static MessageUtils instance;

    public static MessageUtils getInstance() {
        if (instance == null) {
            instance = new MessageUtils();
            instance.registerListener();
        }
        return instance;
    }

    private ConcurrentHashMap<Integer, MessageCallback> callbackMap = new ConcurrentHashMap<>();

    private void registerListener() {
        Poc.registerListener(this);
    }

    private void unregisterListener() {
        Poc.unregisterListener(this);
    }

    public void sendTxtMessage(String targetNum, String content, MessageCallback callback) {
        // 发送信息
        int channel = JniUtils.getInstance().PocSendMsg(targetNum, content, content.length(),
                PocCallType.getTypeof(PocCallType.MESSAGE));
        if (channel <= 0) {
            //失败
            if (callback != null) {
                callback.onError();
            }
        } else {
            //等待回调
            callbackMap.put(channel, callback);
        }
    }

    @Override
    protected void notifyRecvTxtMsg(String numA, String numB, String text) {
    }

    @Override
    protected void notifyRecvTxtPic(String numA, String numB, String text) {
    }

    @Override
    protected void notifyRecvTxtVideo(String numA, String numB, String text) {
    }

    @Override
    protected void notifyRecvTxtAudio(String numA, String numB, String text) {
    }

    @Override
    protected void notifyRecvTxtFile(String numA, String numB, String text) {
    }

    @Override
    protected void notifySendMessageSuc(int channel) {
        MessageCallback messageCallback = callbackMap.get(channel);
        if (messageCallback != null) {
            messageCallback.onSuccess();
            callbackMap.remove(channel);
        }
    }

    @Override
    protected void notifySendMessageFail(int channel) {
        MessageCallback messageCallback = callbackMap.get(channel);
        if (messageCallback != null) {
            messageCallback.onError();
            callbackMap.remove(channel);
        }
    }

    public interface MessageCallback {
        /**
         * 成功
         */
        void onSuccess();

        /**
         * 失败
         */
        void onError();
    }
}
