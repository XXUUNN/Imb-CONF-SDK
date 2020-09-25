package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.GroupListenStateInfo;

public interface GroupListenStateListener extends BaseJniListener{

	public void onRecvGroupListenState(GroupListenStateInfo groupListenStateInfo);
}
