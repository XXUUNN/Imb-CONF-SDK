package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.AddressUrl;

public interface AddressUrlListener extends BaseJniListener{

	public void onRecAddressUrl(AddressUrl url);
}
