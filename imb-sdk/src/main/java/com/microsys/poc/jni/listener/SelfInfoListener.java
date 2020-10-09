package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.SelfInfo;

/**
 * BaseSelfInfoListener
 * @author zhangcd
 *
 * 2014-11-24
 */
public interface SelfInfoListener extends BaseJniListener{
	public void onRecvSelfInfo(SelfInfo selfInfo);
}
