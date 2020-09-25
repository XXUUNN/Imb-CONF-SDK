package com.microsys.poc.jni.entity;

public class VideoData {
	byte[] data;
	int    timestamps;
	int    cameraId;
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public int getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(int timestamps) {
		this.timestamps = timestamps;
	}
	public int getCameraId() {
		return cameraId;
	}
	public void setCameraId(int cameraId) {
		this.cameraId = cameraId;
	}
	
	
}

