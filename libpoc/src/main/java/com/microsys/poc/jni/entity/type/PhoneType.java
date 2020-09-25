package com.microsys.poc.jni.entity.type;

/**
 * 根据不同的手机类型进行判断摄像头显示问题
 * @author Qiudq
 *
 * 2014-11-13
 */
public enum PhoneType {
	NORMAL,		//正常摄像头状态,yuv
	YVU,		//得到的数据是yvu
	UNKNOWN,	//其它
	;
	
	public static PhoneType of(int value) {
		PhoneType type;
		
		switch(value) {
			case 0: type = PhoneType.NORMAL;
					break;
			case 1: type = PhoneType.YVU;
					break;
			default: type = PhoneType.NORMAL;
					break;
		}
		
		return type;
	}
	
	public static int getTypeof(PhoneType pocCallType) {
		int type;
		
		switch(pocCallType) {
			case NORMAL: type = 0;
					break;
			case YVU: type = 1;
					break;
			default: type = 0;
					break;
		}
		
		return type;
	}
}
