package com.microsys.poc.jni.entity;

import java.util.EventObject;


public class VersionUpdateEvent extends EventObject {
	
	private static final long serialVersionUID = 1L;
	private String num;
	private String msg;
	private String mode;
	private String url;
	private String time;
	
	public VersionUpdateEvent(String msg, String mode,String url,String num, String time ) {
		super(msg);
		this.msg = msg;
		this.url = url;
		this.num = num ;
		this.mode = mode;
		this.time = time;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	

}