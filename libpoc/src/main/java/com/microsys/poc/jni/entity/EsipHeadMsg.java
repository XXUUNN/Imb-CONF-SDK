package com.microsys.poc.jni.entity;

import com.microsys.poc.biz.util.StringUtils;

import java.util.List;

public class EsipHeadMsg {
	private String nums;
	
	private List<String> headList;

	
	public EsipHeadMsg(String nums) {
		this.nums = nums;
		this.headList = StringUtils.toStringList(nums, ":");
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
