package com.microsys.poc.jni.entity;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class EsipHeadMsg {
	private String nums;
	
	private List<String> headList;

	
	public EsipHeadMsg(String nums) {
		this.nums = nums;
		this.headList = toStringList(nums, ":");
	}

	public static List<String> toStringList(String s, String splitStr){

		if (TextUtils.isEmpty(s)) return new ArrayList<String>();

		String[] sarray =  s.split(splitStr);
		int len = sarray.length;
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			list.add(sarray[i].trim());
		}

		return list;

	}

	public String getNums() {
		return nums;
	}

	public void setNums(String nums) {
		this.nums = nums;
	}

	public List<String> getHeadList() {
		return headList;
	}

	public void setHeadList(List<String> headList) {
		this.headList = headList;
	}


	

}
