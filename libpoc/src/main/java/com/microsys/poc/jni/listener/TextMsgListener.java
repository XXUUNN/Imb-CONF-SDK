package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.TextMsg;

/**
 * BaseTextMsgListener
 * @author zhangcd
 *
 * 2014-11-24
 */
public interface TextMsgListener extends BaseJniListener{
	public void onRecvTextMsg(TextMsg textMsg);
}
