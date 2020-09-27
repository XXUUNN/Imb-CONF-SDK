package com.imb.sdk.manager;

import android.content.Context;
import android.media.AudioManager;

import com.imb.sdk.call.CallUtils;
import com.imb.sdk.data.PocConstant;

/**
 * @author - gongxun;
 * created on 2020/9/25-10:33;
 * description -
 */
public class CallManager extends BaseManager {
    private AudioManager audioManager;

    /**
     * 当前麦克风是是否静音
     * true 静音
     */
    private boolean isCurMicMute = false;
    /**
     * 是否扬声器播放
     */
    private boolean isCurSpeakerphoneOn = false;

    @Override
    public void init(Context context) {
        PocAudioManager.init(context);

        audioManager = PocAudioManager.getAudioManager();
        isCurMicMute = audioManager.isMicrophoneMute();
        isCurSpeakerphoneOn = audioManager.isSpeakerphoneOn();
    }

    synchronized public void switchMicMute(boolean isMute) {
        audioManager.setMicrophoneMute(isMute);
        isCurMicMute = isMute;
    }

    public boolean isCurMicMute() {
        return isCurMicMute;
    }

    synchronized public void switchSpeakerphoneOn(boolean isSpeakerphoneOn) {
        audioManager.setSpeakerphoneOn(isSpeakerphoneOn);
        isCurSpeakerphoneOn = isSpeakerphoneOn;
    }

    public boolean isSpeakerphoneOn() {
        return isCurSpeakerphoneOn;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * 打电话
     *
     * @param callType 电话类型
     * @param num      号码
     * @return <=0 失败 >0 通道号
     */
    public static int makeCall(@PocConstant.CallType int callType, String num) {
        return CallUtils.makeCall(callType, num);
    }

    /**
     * 接电话
     *
     * @param callType 电话类型
     * @param channel  通道号
     * @return true 成功
     */
    public static boolean acceptCall(@PocConstant.CallType int callType, int channel) {
        return CallUtils.acceptCall(callType, channel);
    }

    /**
     * 拒接电话
     *
     * @return true 成功
     */
    public static boolean hangUpCall(int channel) {
        return CallUtils.hangUpCall(channel);
    }

    /**
     * ptt抢权
     *
     * @param channel 通道号
     * @return true 成功
     */
    public static boolean tbcpRequest(int channel) {
        return CallUtils.tbcpRequest(channel);
    }

    /**
     * ptt释放权利
     *
     * @param channel 通道号
     * @return true 成功
     */
    public static boolean tbcpRelease(int channel) {
        return CallUtils.tbcpRelease(channel);
    }

    /**
     * 点名 主持人有权利 让其强权
     */
    public static boolean tbcpForceOneRequest(int channel, String num) {
        return CallUtils.tbcpForceOneRequest(channel, num);
    }

    /**
     * 停止某人的强权 主持人能释放他人的强权
     */
    public static boolean tbcpForceOneRelease(int channel, String num) {
        return CallUtils.tbcpForceOneRelease(channel, num);
    }
}
