package com.microsys.poc.jni.entity;

public class PwdInfo {

	private String userTel;
	
	private int ret;
	
	public PwdInfo(String groupTel ,int ret) {

		this.userTel = groupTel;
		this.ret = ret;
		
	}

	public String getGroupTel() {
		return userTel;
	}

	public void setGroupTel(String groupTel) {
		this.userTel = groupTel;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}
	
}
