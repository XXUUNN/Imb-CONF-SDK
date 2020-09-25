package com.microsys.poc.jni.base;

import android.text.TextUtils;

import com.microsys.poc.jni.entity.AddressUrl;
import com.microsys.poc.jni.entity.SyncAddressBookResult;
import com.microsys.poc.jni.listener.AddressUrlListener;

public abstract class BaseAddressUrlListener implements AddressUrlListener {

	@Override
	public void onRecAddressUrl(AddressUrl url){
		String result = url.getResult();
		String reason = url.getReason();
		String addlistUrl = url.getUrl();

		SyncAddressBookResult syncAddressBookResult = new SyncAddressBookResult();
		if (TextUtils.equals(result,"succ")) {
			//成功
			syncAddressBookResult.isSuccessful = true;
			if (!TextUtils.isEmpty(addlistUrl)) {
				syncAddressBookResult.diffAddressBookFilePathOnServer = addlistUrl;
			}
		}else{
			syncAddressBookResult.isSuccessful = false;
			syncAddressBookResult.reason = reason;
		}
		onReceivedAddressBookPath(syncAddressBookResult);
	}

	/**
	 * 回调通讯录同步的结果
	 * @param result 同步通讯录的结果
	 */
	protected abstract void onReceivedAddressBookPath(SyncAddressBookResult result);
}
