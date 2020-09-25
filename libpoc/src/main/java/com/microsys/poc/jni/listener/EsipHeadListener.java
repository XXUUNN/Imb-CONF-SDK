package com.microsys.poc.jni.listener;

import com.microsys.
		poc.jni.entity.EsipHeadMsg;

public interface EsipHeadListener extends BaseJniListener{
	
	public void onNotifyEsipHeadNum(EsipHeadMsg esipHeadMsg);

}
