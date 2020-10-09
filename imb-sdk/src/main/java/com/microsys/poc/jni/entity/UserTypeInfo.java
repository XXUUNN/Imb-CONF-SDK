package com.microsys.poc.jni.entity;

public class UserTypeInfo {
	
	private int chn;
	private int userType;
	
	public UserTypeInfo(int chn, int userType) {
		this.chn = chn;
		this.userType = userType;
	}

	public int getChn() {
		return chn;
	}

	public void setChn(int chn) {
		this.chn = chn;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}



}
