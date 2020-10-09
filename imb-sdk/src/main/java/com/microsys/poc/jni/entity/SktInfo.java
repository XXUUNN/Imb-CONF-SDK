package com.microsys.poc.jni.entity;

public class SktInfo {
	
	private int chn;
	private String content;
	
	public SktInfo(int chn, String content) {
		this.chn = chn;
		this.content = content;
	}

	public int getChn() {
		return chn;
	}

	public void setChn(int chn) {
		this.chn = chn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

}
