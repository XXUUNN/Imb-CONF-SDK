package com.microsys.poc.jni.entity.type;

public enum PocCallTypeChangeMode {
	CHANGE_UNKNOW,//未知
	CHANGE_AUDIO_TO_VIDEO_FULL,//从音频转成视频
	CHANGE_VIDEO_TO_AUDIO_FULL;//从视频转成音频
	
	public static PocCallTypeChangeMode of(int value) {
		PocCallTypeChangeMode changeMode;
		
		switch(value) {
			case 0: changeMode = PocCallTypeChangeMode.CHANGE_UNKNOW;
					break;
			case 1: changeMode = PocCallTypeChangeMode.CHANGE_AUDIO_TO_VIDEO_FULL;
					break;
			case 2: changeMode = PocCallTypeChangeMode.CHANGE_VIDEO_TO_AUDIO_FULL;
					break;

			default: changeMode = PocCallTypeChangeMode.CHANGE_UNKNOW;
					break;
		}
		
		return changeMode;
	}
	
	public static int getTypeof(PocCallTypeChangeMode typeChangeMode) {
		int type;
		
		switch(typeChangeMode) {
			case CHANGE_UNKNOW: type = 0;
					break;
			case CHANGE_AUDIO_TO_VIDEO_FULL: type = 1;
					break;
			case CHANGE_VIDEO_TO_AUDIO_FULL: type = 2;
					break;

			default: type = 0;
					break;
		}
		
		return type;
	}

}
