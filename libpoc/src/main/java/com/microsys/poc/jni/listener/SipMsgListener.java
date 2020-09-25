package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.SipMsg;

/**
 * BaseSipMsgListener
 * @author zhangcd
 *
 * 2014-11-24
 */
public interface SipMsgListener extends BaseJniListener {
	public void onRecvSipMsg(SipMsg sipMessage);
}
