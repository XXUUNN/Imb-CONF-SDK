package com.microsys.poc.jni.entity;

/**
 * Tbcp消息
 * @author zhangcd
 *
 * 2014-11-19
 */
public class TbcpMsg {
	private int msgType;
	private String tel;//本地号码
	private int ret;
	
	public TbcpMsg(int msgType, String tel, int ret) {
		this.msgType = msgType;
		this.tel 	 = tel;
		this.ret     = ret;
	}
	
	public TbcpMsg(int msgType, int ret) {
		this.msgType = msgType;
		this.ret     = ret;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String toString() {
		String str = "[msgType] = " + getMsgType() + "\n" +
					 "[tel]     = " + getTel() + "\n" +
					 "[ret]     = " + ret;
		return str;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}
}
