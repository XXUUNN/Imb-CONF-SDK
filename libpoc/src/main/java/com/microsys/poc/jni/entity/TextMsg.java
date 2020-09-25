package com.microsys.poc.jni.entity;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 短信信息
 * @author zhangcd
 *
 * 2014-11-19
 */
public class TextMsg {
	private String text;
	private String callTel;//号码转为了String型
	private int callType;//MESSAGE代表普通短信 PICTURE代表文件传输
	private int msgType;
	private int ret;
	private String firstCallTel;//群组显示是谁发过来的号码
	private String msgId;//message 的唯一标志
	
	public TextMsg(String text, String firstCallTel, String callTel, String msgId, int callType, int msgType,int ret){
		this.text     = text;
		this.callTel = callTel;
		this.callType = callType;
		this.msgId    = msgId;
		this.msgType  = msgType;
		this.ret      = ret;
		this.firstCallTel = firstCallTel;
	}
	
	public String toString() {
		return "[text]     = " + text     + "\n"+
			   "[callTel] = " + callTel + "\n" +
			   "[callType] = " + callType + "\n" +
			   "[msgType]  = " + msgType  + "\n" +
			   "[ret]      = " + ret      + "\n";
	};
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getcallTel() {
		return callTel;
	}

	public void setcallTel(String callTel) {
		this.callTel = callTel;
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
	
	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}
	
	
	public String getFirstCallTel() {
		return firstCallTel;
	}

	public void setFirstCallTel(String firstCallTel) {
		this.firstCallTel = firstCallTel;
	}
	
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public static void main(String[] args) {
		long curr = new Date().getTime();
		Date date = new Date(curr);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);
	}
	
	

	
}
