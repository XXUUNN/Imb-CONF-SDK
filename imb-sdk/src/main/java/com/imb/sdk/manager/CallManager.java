package com.imb.sdk.manager;

import android.content.Context;
import android.media.AudioManager;

import com.imb.sdk.call.CallUtils;
import com.imb.sdk.data.PocConstant;
import com.microsys.poc.jni.JniUtils;

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
    public int makeCall(@PocConstant.CallType int callType, String num) {
        return CallUtils.makeCall(callType, num);
    }

    /**
     * 接电话
     *
     * @param callType 电话类型
     * @param channel  通道号
     * @return true 成功
     */
    public boolean acceptCall(@PocConstant.CallType int callType, int channel) {
        return CallUtils.acceptCall(callType, channel);
    }

    /**
     * 拒接电话
     *
     * @return true 成功
     */
    public boolean hangUpCall(int channel) {
        return CallUtils.hangUpCall(channel);
    }

    /**
     * ptt抢权
     *
     * @param channel 通道号
     * @return true 成功
     */
    public boolean tbcpRequest(int channel) {
        return CallUtils.tbcpRequest(channel);
    }

    /**
     * ptt释放权利
     *
     * @param channel 通道号
     * @return true 成功
     */
    public boolean tbcpRelease(int channel) {
        return CallUtils.tbcpRelease(channel);
    }

    /**
     * 点名 主持人有权利 让其强权
     */
    public boolean tbcpForceOneRequest(int channel, String num) {
        return CallUtils.tbcpForceOneRequest(channel, num);
    }

    /**
     * 停止某人的强权 主持人能释放他人的强权
     */
    public boolean tbcpForceOneRelease(int channel, String num) {
        return CallUtils.tbcpForceOneRelease(channel, num);
    }

    /**
     * 订阅PoC的消息
     * 调用了这个方法后，才会接受到电话和消息
     * 如果当前已经在会议中 那么调用后，服务端会把自己呼起来
     */
    public void subscribePoc() {
        JniUtils.getInstance().PocSendSubcribe("");
    }

    public void enableReadWriteAudioAndVideo(boolean enableRead, boolean enableWrite){
        JniUtils.getInstance().enableReadWriteAudioAndVideo(enableRead, enableWrite);
    }

    /**
     * 开始显示视频流
     * @param isMultiStreams 通话 只有一个流 就是false 否则true
     */
    public void startShowVideo(boolean isMultiStreams){
        JniUtils.getInstance().prepareStartVideoShow(isMultiStreams);
    }
    /**
     * 停止显示视频流
     */
    public void stopShowVideo(){
        JniUtils.getInstance().prepareStopVideoShow();
    }
}
