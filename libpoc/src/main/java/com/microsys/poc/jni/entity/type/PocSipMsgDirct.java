package com.microsys.poc.jni.entity.type;

/**
 * 呼叫来源
 * @author Qiudq
 *
 * 2015-3-27
 */
public enum PocSipMsgDirct {
	UNKNOW,
	CALLIN,     /*呼入*/
	CALLOUT,    /*呼出*/
	CALLNOM,    /*无呼入呼出区别*/
	;
	
	public static PocSipMsgDirct of(int value) {
		PocSipMsgDirct dirct;
		
		switch(value) {
			case 0: dirct = PocSipMsgDirct.UNKNOW;
					break;
			case 1: dirct = PocSipMsgDirct.CALLIN;
					break;
			case 2: dirct = PocSipMsgDirct.CALLOUT;
					break;
			case 3: dirct = PocSipMsgDirct.CALLNOM;
					break;
			default: dirct = PocSipMsgDirct.UNKNOW;
					break;
		}
		
		return dirct;
	}
	
	public static int getTypeof(PocSipMsgDirct sipMsgDirc) {
		int type;
		
		switch(sipMsgDirc) {
			case UNKNOW: type = 0;
					break;
			case CALLIN: type = 1;
					break;
			case CALLOUT: type = 2;
					break;
			case CALLNOM: type = 3;
					break;
			default: type = 0;
					break;
		}
		
		return type;
	}
}
