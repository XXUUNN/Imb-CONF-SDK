package com.microsys.poc.jni.entity.type;

/**
 * 
 * @author Qiudq
 *
 * 2014-11-13
 */
public enum CameraFormat {
	NV21,		
	NV12,		
	YV12,
	UNKNOWN,	
	;
	
	public static CameraFormat of(int value) {
		CameraFormat type;
		
		switch(value) {
			case 0: type = CameraFormat.NV21;
					break;
			case 1: type = CameraFormat.NV12;
					break;
			case 2: type = CameraFormat.YV12;
			        break;
			default: type = CameraFormat.NV21;
					break;
		}
		
		return type;
	}
	
	public static int getTypeof(CameraFormat pocCallType) {
		int type;
		
		switch(pocCallType) {
			case NV21: type = 0;
					break;
			case NV12: type = 1;
					break;
			default: type = 0;
					break;
		}
		
		return type;
	}
}
