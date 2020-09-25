package com.imb.sdk.manager;

import android.content.Context;
import android.media.AudioManager;

/**
 * @author - gongxun;
 * created on 2019/9/25-16:02;
 * description - 统一的audioManager
 */
class PocAudioManager {
    private static AudioManager manager;

    public static void init(Context context) {
        if (manager == null) {
            manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
    }

    public static AudioManager getAudioManager() {
        return manager;
    }
}
