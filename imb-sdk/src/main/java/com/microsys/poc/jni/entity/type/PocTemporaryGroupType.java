package com.microsys.poc.jni.entity.type;

/**
 * 临时组
 * @author zhaolingggang
 *
 * 2015-3-27
 */
public enum PocTemporaryGroupType {
	UNKNOW,
	CREATE,     /*创建临时组*/
	CLOSE,    /*删除临时组*/
	USERADD,    /*添加用户*/
	USERDEL,    /*删除用户*/
	NAMEMOD,   /*组名修改*/
	;
	
	public static PocTemporaryGroupType of(int value) {
		PocTemporaryGroupType dirct;
		
		switch(value) {
			case 0: dirct = PocTemporaryGroupType.UNKNOW;
					break;
			case 1: dirct = PocTemporaryGroupType.CREATE;
					break;
			case 2: dirct = PocTemporaryGroupType.CLOSE;
					break;
			case 3: dirct = PocTemporaryGroupType.USERADD;
					break;
			case 4: dirct = PocTemporaryGroupType.USERDEL;
			        break;
			case 5: dirct = PocTemporaryGroupType.NAMEMOD;
	                break;
			default: dirct = PocTemporaryGroupType.UNKNOW;
					break;
		}
		
		return dirct;
	}
	
	public static int getTypeof(PocTemporaryGroupType sipMsgDirc) {
		int type;
		
		switch(sipMsgDirc) {
			case UNKNOW: type = 0;
					break;
			case CREATE: type = 1;
					break;
			case CLOSE: type = 2;
					break;
			case USERADD: type = 3;
					break;
			case USERDEL: type = 4;
			        break;
			case NAMEMOD: type = 5;
			        break;
			default: type = 0;
					break;
		}
		
		return type;
	}
}
