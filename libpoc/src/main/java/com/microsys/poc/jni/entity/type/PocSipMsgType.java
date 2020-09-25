package com.microsys.poc.jni.entity.type;

/**
 * 呼叫消息标志
 * @author zhangcd
 *
 * 2014-11-13
 */
public enum PocSipMsgType {
	UNKNOWN,
	REGISTER,
	REGISTER_OK,
	INVITE,		//（呼入）收到此消息时，需要打开接听界面
	TRY,		// 可不做任何处理
	RING,		//（呼出）收到此消息时，放振铃音
	RING_183,	//（呼出）收到此消息时，播放失败音
	OK,			//（呼出）建立通话
	ACK,		//（呼入）建立通话
	BYE,		//（呼如）收到挂机
	BYE_OK,		//（呼入）主动挂机 10
	ERROR403,	//收到错误，挂机
	ERROR480,	//收到错误，挂机
	ERROR408,	//收到错误，挂机
	ERROR404,	//收到错误，挂机
	MESSAGE,
	MESSAGE_OK,//17
	NOTIFY,
	;
	
	
	public static PocSipMsgType of(int value) {
		PocSipMsgType type;
		
		switch(value) {
			case 0: type = PocSipMsgType.UNKNOWN;
					break;
			case 1: type = PocSipMsgType.REGISTER;
					break;
			case 2: type = PocSipMsgType.REGISTER_OK;
					break;
			case 3: type = PocSipMsgType.INVITE;
					break;
			case 4: type = PocSipMsgType.TRY;
					break;
			case 5: type = PocSipMsgType.RING;
					break;
			case 6: type = PocSipMsgType.RING_183;
				    break;
			case 7: type = PocSipMsgType.OK;
					break;
			case 8: type = PocSipMsgType.ACK;
					break;
			case 9: type = PocSipMsgType.BYE;
					break;
			case 10: type = PocSipMsgType.BYE_OK;
					break;
			case 11: type = PocSipMsgType.ERROR403;
					break;
			case 12: type = PocSipMsgType.ERROR480;
					break;
			case 13: type = PocSipMsgType.ERROR408;
					break;
			case 14: type = PocSipMsgType.ERROR404;
					break;
			case 15: type = PocSipMsgType.MESSAGE;
					break;
			case 16: type = PocSipMsgType.MESSAGE_OK;
					break;
			case 17: type = PocSipMsgType.NOTIFY;
					break;
			default: type = PocSipMsgType.UNKNOWN;
					break;
		}
		
		return type;
	}
	
	
	
}
