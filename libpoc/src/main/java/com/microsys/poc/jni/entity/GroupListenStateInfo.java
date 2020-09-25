package com.microsys.poc.jni.entity;

public class GroupListenStateInfo {

	public static final int NORMAL = 0;//普通状态
	public static final int SHIELDED = 1;//屏蔽状态
	private String groupTel;

	private int ret;
	private int currentSt;

	public GroupListenStateInfo(String groupTel, int ret, int currentSt) {

		this.groupTel = groupTel;
		this.ret = ret;
		this.currentSt = currentSt;

	}

	public String getGroupTel() {
		return groupTel;
	}

	public void setGroupTel(String groupTel) {
		this.groupTel = groupTel;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}
	public int getCurrentSt() {
		return currentSt;
	}

	public void setCurrentSt(int currentSt) {
		this.currentSt = currentSt;
	}
}
