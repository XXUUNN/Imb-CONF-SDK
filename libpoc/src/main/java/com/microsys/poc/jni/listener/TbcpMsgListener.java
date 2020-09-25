package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.TbcpMsg;

/**
 * BaseTbcpMsgListener
 * @author zhangcd
 *
 * 2014-11-24
 */
public interface TbcpMsgListener extends BaseJniListener{
	public void onRecvTbcpMsg(TbcpMsg tbcpMsg);
}
