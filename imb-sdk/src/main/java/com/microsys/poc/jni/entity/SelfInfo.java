package com.microsys.poc.jni.entity;

/**
 * 自身信息
 * @author zhangcd
 *
 * 2014-11-19
 */
public class SelfInfo {
	private String name;
	private String gruopTel;
	
	
	public SelfInfo(String name,String gruopTel){
		this.name     = name;
		this.gruopTel = gruopTel;
	}
	
	
	@Override
	public String toString() {
		return "[name]     = " + name     + "\n" +
			   "[gruopTel] = " + gruopTel;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGruopTel() {
		return gruopTel;
	}
	
	public void setGruopTel(String gruopTel) {
		this.gruopTel = gruopTel;
	}
}
