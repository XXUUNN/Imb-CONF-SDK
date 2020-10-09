package com.microsys.poc.jni.entity;

public class GroupEditResult {
	private int type;

	private String groupTel;
	
	private int ret;
	
	public GroupEditResult(int type, String groupTel , int ret) {

		this.type = type;
		this.groupTel = groupTel;
		this.ret = ret;
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	
	

	
	
}
