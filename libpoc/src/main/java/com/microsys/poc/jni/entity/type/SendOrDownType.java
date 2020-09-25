package com.microsys.poc.jni.entity.type;

/**
 * 发送/接收图片/视频 通知类型
 */
public enum SendOrDownType {
	UNKNOW,
	SENDING,     	/*发送中*/
	SENDSUC,  		/*发送成功*/
	RECEIVING,   	/*接收中*/
	RECEIVESUC,   	/*接收成功*/
	SENDFAIL,    	/*发送失败*/
	RECEIVEFAIL,    /*接收失败*/
}
