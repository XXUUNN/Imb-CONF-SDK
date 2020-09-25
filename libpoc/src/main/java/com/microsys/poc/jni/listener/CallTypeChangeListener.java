package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.CallTypeChange;

public interface CallTypeChangeListener extends BaseJniListener{

	public void notifyCallTypeChange(CallTypeChange callTypeChange);
}
