package com.microsys.poc.jni.entity;

public class LocalPower {
	private int msgPwd = 0;
	private int picPwd = 0;
	private int vidPwd = 0;
	private int monPwd = 0;
	private int mapPwd = 0;
	private int vlcPwd = 0;
	private int videoPwd =0;
	private int hbtime;//心跳时间
	private int orientation;//定位权限
	private int location_rate = 30;//定位上报频率 默认30s
	
	public LocalPower(int msg, int pic, int vid, int mon, int map, int vlc, int video, int hbt, int orientation, int locationRate) {
		this.msgPwd = msg;
		this.picPwd = pic;
		this.vidPwd = vid;
		this.monPwd = mon;
		this.mapPwd = map;
		this.vlcPwd = vlc;
		this.videoPwd = video;
		this.hbtime = hbt;
		this.orientation = orientation;
		this.location_rate = locationRate;
		
	}
	public int getMsgPwd() {
		return msgPwd;
	}
	public void setMsgPwd(int msgPwd) {
		this.msgPwd = msgPwd;
	}
	public int getPicPwd() {
		return picPwd;
	}
	public void setPicPwd(int picPwd) {
		this.picPwd = picPwd;
	}
	public int getVidPwd() {
		return vidPwd;
	}
	public void setVidPwd(int vidPwd) {
		this.vidPwd = vidPwd;
	}
	public int getMonPwd() {
		return monPwd;
	}
	public void setMonPwd(int monPwd) {
		this.monPwd = monPwd;
	}
    public int getHbtime() {
        return hbtime;
    }
    public void setHbtime(int hbtime) {
        this.hbtime = hbtime;
    }
	public int getOrientation() {
		return orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	public int getMapPwd() {
		return mapPwd;
	}
	public void setMapPwd(int mapPwd) {
		this.mapPwd = mapPwd;
	}
	public int getVlcPwd() {
		return vlcPwd;
	}
	public void setVlcPwd(int vlcPwd) {
		this.vlcPwd = vlcPwd;
	}
	public int getLocation_rate() {
		return location_rate;
	}
	public void setLocation_rate(int location_rate) {
		this.location_rate = location_rate;
	}
	public int getVideoPwd() {
		return videoPwd;
	}
	public void setVideoPwd(int videoPwd) {
		this.videoPwd = videoPwd;
	}
	
	
}
