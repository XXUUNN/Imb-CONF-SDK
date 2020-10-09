package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.PwdInfo;

public interface ResetPwdListener extends BaseJniListener{

	public void onRecvRestPwd(PwdInfo pwdInfo);
}
