package com.microsys.poc.jni.entity;

public class RtspData {

	String Rid = "";
	String Pid = "";//父节点id
	String Rname = "";
	String rtspContent = "";
	
	public RtspData(String Pid, String Rid, String Rname, String rtspContent) {
		this.Rid = Rid;
		this.Rname = Rname;
		this.rtspContent = rtspContent;
		this.Pid = Pid;
	}

	public String getRid() {
		return Rid;
	}

	public void setRid(String rid) {
		Rid = rid;
	}

	public String getRname() {
		return Rname;
	}

	public void setRname(String rname) {
		Rname = rname;
	}

	public String getRtspContent() {
		return rtspContent;
	}

	public void setRtspContent(String rtspContent) {
		this.rtspContent = rtspContent;
	}

	public String getPid() {
		return Pid;
	}

	public void setPid(String pid) {
		Pid = pid;
	}
	
	
	
}
