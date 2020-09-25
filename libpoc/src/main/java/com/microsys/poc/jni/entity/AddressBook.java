package com.microsys.poc.jni.entity;

/**
 * 通讯录
 * @author zhangcd
 *
 * 2014-11-19
 */
public class AddressBook {
	private String creator;//组的创建者
	private int tag;
	private int id;
	private int did;
	private int opt;
	private String tel;
	private String name;
	private String member;
	private int st;
	private int endFlag;

	public AddressBook(int tag, int did, int id, String tel, String name, String member, int opt, int st, int endFlag, String creator) {
		this.tag = tag;
		this.did = did;
		this.id = id;
		this.tel = tel;
		this.name = name;
		this.member = member;
		this.opt = opt;
		this.st = st;
		this.endFlag = endFlag;
		this.creator = creator;
	}

	@Override
	public String toString() {
		return "AddressBook{" +
				"creator='" + creator + '\'' +
				", tag=" + tag +
				", id=" + id +
				", did=" + did +
				", opt=" + opt +
				", tel='" + tel + '\'' +
				", name='" + name + '\'' +
				", member='" + member + '\'' +
				", st=" + st +
				", endFlag=" + endFlag +
				'}';
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public int getOpt() {
		return opt;
	}

	public void setOpt(int opt) {
		this.opt = opt;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public int getSt() {
		return st;
	}

	public void setSt(int st) {
		this.st = st;
	}


	public int getEndFlag() {
		return endFlag;
	}


	public void setEndFlag(int endFlag) {
		this.endFlag = endFlag;
	}

	public String getCreator() {
		return creator;
	}
	
}
