package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.UserTypeInfo;

public interface UserTypeInfoListener extends BaseJniListener{
	
	public void onNotifyUserTypeInfo(UserTypeInfo userInfo);

}
