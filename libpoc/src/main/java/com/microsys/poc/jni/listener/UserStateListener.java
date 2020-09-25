package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.UserState;

/**
 * BaseUserStateListener
 * @author zhangcd
 *
 * 2014-11-24
 */
public interface UserStateListener extends BaseJniListener {
	public void onRecvUserState(UserState userState);
}
