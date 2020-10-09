package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.SktInfo;

public interface SktInfoListener extends BaseJniListener{
	
	public void onNotifySktInfo(SktInfo sktInfo);

}
