package com.microsys.poc.jni.entity;

public class User extends Node implements Comparable<User> {
	private int uid;
	private String tel;
	private int halfAndFullCount;
	/**
	 * 0:logout; 1:login;
	 */
	private int loadStatus = 0;

	/**
	 * 0:outgroup; 1:ingroup;
	 */
	private int groupStatus = 0;
	private boolean checked = false ;
	
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public User(int uid, int did, String name, String tel) {
		super(did, name);
		this.uid = uid;
		this.tel = tel;
	}
	
	public User(int uid, int did, String name, String tel,String state) {
		super(did, name);
		this.uid = uid;
		this.tel = tel;
		if("login".equals(state)){
			this.loadStatus = 1 ;
		}else if("logout".equals(state)){
			this.loadStatus = 0 ;
		}
	}
	
	public User(int state, int uid, int did, String name, String tel) {
		super(did, name);
		this.uid = uid;
		this.tel = tel;
		this.loadStatus = state;
	}
	
	public User(int uid, int did, String name, String tel, int halfAndFullCount) {
		super(did, name);
		this.uid = uid;
		this.tel = tel;
		this.halfAndFullCount = halfAndFullCount;
	}
	
	public int getHalfAndFullCount(){
		return halfAndFullCount;
	}
	public void addHalfAndFullCount(){
		halfAndFullCount+=1;
	}
	
	public String toString() {
		return String.format("User[uid=%d, did=%d, name=%s, tel=%s, status=%s]", uid, getDid(), getName(), tel, loadStatus);
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getUid() {
		return uid;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTel() {
		return tel;
	}

	public int getLoadStatus() {
		return loadStatus;
	}

	public void setLoadStatus(int loadStatu) {
		this.loadStatus = loadStatu;
	}
	
	
	public int getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(int groupStatu) {
		this.groupStatus = groupStatu;
	}

	public int compareTo(User another) {
		if(this.getGroupStatus() == another.getGroupStatus()){
			if (this.getLoadStatus() == another.getLoadStatus())
				return this.getName().compareTo(another.getName());
			else 
				return another.getLoadStatus() - this.getLoadStatus();
		}else 
			return another.getGroupStatus() - this.getGroupStatus();
	}
	
}
