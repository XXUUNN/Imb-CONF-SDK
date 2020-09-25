package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.LocalPower;


public interface LocalMediaPowerListener extends BaseJniListener {
	
	public void onRecvLocalMediaPower(LocalPower local);
	
}
