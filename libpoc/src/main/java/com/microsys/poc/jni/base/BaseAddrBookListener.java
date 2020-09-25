package com.microsys.poc.jni.base;

import com.microsys.poc.jni.entity.AddressBook;
import com.microsys.poc.jni.listener.AddrBookListener;

/**
 * AddrBookListener
 *
 * @author zhangcd
 * <p>
 * 2014-11-24
 */
public abstract class BaseAddrBookListener implements AddrBookListener {

	/**
	 * 原始通讯录的变动的回调
	 */
	@Override
	public void onRecvAddrBook(AddressBook addressBook){

		final int opt = addressBook.getOpt();
		if (opt != -2) {
			onUpdateAddressBook();
		}
	}

	/**
	 * 可能短时间会回调多次，建议一段时间内更新一次所有通讯录
	 */
	protected abstract void onUpdateAddressBook();
}
