package com.microsys.poc.jni.entity;

public class SynGroupListenStateInfo {

	private int gid;
	private int currentSt;

	public SynGroupListenStateInfo(int gid, int currentSt) {

		this.gid = gid;
		this.currentSt = currentSt;

	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public int getCurrentSt() {
		return currentSt;
	}

	public void setCurrentSt(int currentSt) {
		this.currentSt = currentSt;
	}
	
	

}
