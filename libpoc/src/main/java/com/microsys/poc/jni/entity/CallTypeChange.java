package com.microsys.poc.jni.entity;

public class CallTypeChange {
	
	private int chn ;
	private int changeMode;
	
	public CallTypeChange(int chn, int changeMode) {
		
		this.chn = chn;
		this.changeMode = changeMode;
	}

	public int getChn() {
		return chn;
	}

	public void setChn(int chn) {
		this.chn = chn;
	}

	public int getChangeMode() {
		return changeMode;
	}

	public void setChangeMode(int changeMode) {
		this.changeMode = changeMode;
	}
	
	
	

}
