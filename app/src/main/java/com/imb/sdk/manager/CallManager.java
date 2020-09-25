package com.imb.sdk.manager;

import android.content.Context;
import android.media.AudioManager;

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
}
