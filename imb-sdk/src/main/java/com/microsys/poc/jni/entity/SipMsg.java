package com.microsys.poc.jni.entity;

/**
 * Sip 消息
 * @author zhangcd
 *
 * 2014-11-19
 */
public class SipMsg {
	private String callTel;
	private String calledTel;
	private int callChannel;
	private int callType;
	private int msgType; 
	private int callDirc;
	private int ret;
	
	public SipMsg(String callTel, String calledTel, int callChannel, int callType, int msgType,
			int callDirc, int ret) {
		this.callTel = callTel;
		this.calledTel = calledTel;
		this.callChannel = callChannel;
		this.callType = callType;
		this.msgType = msgType;
		this.callDirc = callDirc;//呼叫来源 0：注册之类 1：呼入 2： 呼出
		this.ret = ret;
	}
	
	public String getCallTel() {
		return callTel;
	}

	public void setCallTel(String callTel) {
		this.callTel = callTel;
	}

	public int getCallChannel() {
		return callChannel;
	}

	public void setCallChannel(int callChannel) {
		this.callChannel = callChannel;
	}

	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}
	
	@Override
	public String toString() {
		String str = "[callTel]     = " + callTel     + "\n" +
					 "[callChannel] = " + callChannel + "\n" +
					 "[callType]    = " + callType    + "\n" +
				     "[msgType]     = " + msgType     + "\n" +
					 "[ret]         = " + ret;
		return str;
	}

	public int getCallDirc() {
		return callDirc;
	}

	public void setCallDirc(int callDirc) {
		this.callDirc = callDirc;
	}

	public String getCalledTel() {
		return calledTel;
	}

	public void setCalledTel(String calledTel) {
		this.calledTel = calledTel;
	}
}
