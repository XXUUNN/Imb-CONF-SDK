package com.microsys.poc.jni.entity;

/**
 * 视频呼叫分辨率和帧率，在视频呼叫协商过程中上报
 * type: local 本地
 * 		 remote 对端
 * @author Qiudq
 *
 * 2015-4-24
 */
public class Resolution {
	private int iHeight;
	private int iWidth;
	private int iFrameRate;
	private String type;
	
	public int getiHeight() {
		return iHeight;
	}

	public void setiHeight(int iHeight) {
		this.iHeight = iHeight;
	}

	public int getiWidth() {
		return iWidth;
	}

	public void setiWidth(int iWidth) {
		this.iWidth = iWidth;
	}
	
	public int getiFrameRate() {
		return iFrameRate;
	}

	public void setiFrameRate(int iFrameRate) {
		this.iFrameRate = iFrameRate;
	}

	public Resolution(int iHeight, int iWidth, int iFrameRate, String type){
		this.iHeight = iHeight;
		this.iWidth = iWidth;
		this.iFrameRate = iFrameRate;
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
