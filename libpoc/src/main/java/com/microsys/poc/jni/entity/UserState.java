package com.microsys.poc.jni.entity;

/**״
 *  * @author zhangcd
 *
 * 2014-11-19
 */
public class UserState {
	private int tag;
	private int id;//uid or gid
	private int state;
	
	public UserState(int tag,int id,int state){
		this.tag   = tag;
		this.id    = id;
		this.state = state;
	}
	
	
	public String toString() {
		return "[tag]   = " + tag + "\n" +
	           "[id]    = " + id  + "\n" +
			   "[state] = " + state;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
