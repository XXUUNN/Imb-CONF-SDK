package com.microsys.poc.jni.entity.type;

/**
 * 会话类型
 * @author zhangcd
 *
 * 2014-11-13
 */
public enum PocCallType {
	UNKNOW,
	PRECALL,     /*预会话， 目前取消了*/
	HALFCALL,    /*半双工，当区分不出是单呼，组呼还是广播的时候，使用该枚举，这里需要根据号码来判断是单呼、广播还是组呼*/
	FULLCALL,    /*点呼*/
	MESSAGE,     /*短信*/
	PICTURE,     /*图片传输*/
	VIDEOREC,    /*录像传输*/
	AUDIOREC,    /*录音传输*/
	MONITOR,     /*监控*/
	VIDEOCALL,   /*视频通话*/
	HALFVIDEOCALL,
	VIDEOGIVEOUT,/*视频分发*/
	REFER,       /*呼叫转接*/
	HOLDON,      /*呼叫保持*/
	
	CALLFILE,    /*文件传输*/
	CALLLOCATION, /*位置信息*/
	
	UNGENT_AUDIOCALL, /*紧急语音呼叫*/
	UNGENT_VEDIOCALL,  /*紧急视频呼叫*/
	;
	
	public static PocCallType of(int value) {
		PocCallType type;
		
		switch(value) {
			case 0: type = PocCallType.UNKNOW;
					break;
			case 1: type = PocCallType.PRECALL;
					break;
			case 2: type = PocCallType.HALFCALL;
					break;
			case 3: type = PocCallType.FULLCALL;
					break;
			case 4: type = PocCallType.MESSAGE;
					break;
			case 5: type = PocCallType.PICTURE;
					break;
			case 6: type = PocCallType.VIDEOREC;
					break;
			case 7: type = PocCallType.AUDIOREC;
					break;
			case 8: type = PocCallType.MONITOR;
					break;
			case 9: type = PocCallType.VIDEOCALL;
					break;
			case 10: type = PocCallType.HALFVIDEOCALL;
					break;
			case 11: type = PocCallType.VIDEOGIVEOUT;
					break;
			case 12: type = PocCallType.REFER;
					break;
			case 13: type = PocCallType.HOLDON;
					break;
			case 14: type = PocCallType.CALLFILE;
			        break;
			case 15: type = PocCallType.CALLLOCATION;
			        break;
			case 16: type = PocCallType.UNGENT_AUDIOCALL;
			        break;
			case 17: type = PocCallType.UNGENT_VEDIOCALL;
			        break;

			default: type = PocCallType.UNKNOW;
					break;
		}
		
		return type;
	}
	
	public static int getTypeof(PocCallType pocCallType) {
		int type;
		
		switch(pocCallType) {
			case UNKNOW: type = 0;
					break;
			case PRECALL: type = 1;
					break;
			case HALFCALL: type = 2;
					break;
			case FULLCALL: type = 3;
					break;
			case MESSAGE: type = 4;
					break;
			case PICTURE: type = 5;
					break;
			case VIDEOREC: type = 6;
					break;
			case AUDIOREC : type = 7;
					break;
			
			case MONITOR: type = 8;
					break;
					
			case VIDEOCALL: type = 9;
					break;
					
			case HALFVIDEOCALL: type = 10;
					break;
					
			case VIDEOGIVEOUT: type = 11;
					break;
					
			case REFER: type = 12;
					break;
					
			case HOLDON: type = 13;
					break;
					
			case CALLFILE: type =14;
			        break;
			        
			case CALLLOCATION: type = 15;
			        break;
			 
			case UNGENT_AUDIOCALL: type = 16;
			        break;
			
			case UNGENT_VEDIOCALL: type = 17;
			        break;
			default: type = 0;
					break;
		}
		
		return type;
	}
}
