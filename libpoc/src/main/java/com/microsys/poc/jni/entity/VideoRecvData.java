package com.microsys.poc.jni.entity;

public class VideoRecvData {

	private byte[] data;//硬件编码返回的数据数组
	private int dataLen;//数据的实际长度
	private int    width;//编码出来视频的宽度
	private int    height;//编码出来视频的高度
	private long    cpTime;//时间
	private int direction;//摄像头方向

	public VideoRecvData() {
	}

	public VideoRecvData(byte[] data, int dataLen, int width, int height, long cpTime, int direction) {
		this.data = data;
		this.dataLen = dataLen;
		this.width = width;
		this.height = height;
		this.cpTime = cpTime;
		this.direction = direction;
	}

	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	/**
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	public long getCpTime() {
		return cpTime;
	}
	public void setCpTime(long cpTime) {
		this.cpTime = cpTime;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}



}

