package com.microsys.poc.jni.entity;

public class Node {
	private int did;
	private String name;

	public Node(int did, String name) {
		this.did = did;
		this.name = name;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public int getDid() {
		return did;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}
}
