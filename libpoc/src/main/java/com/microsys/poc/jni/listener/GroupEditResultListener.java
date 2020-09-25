package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.GroupEditResult;


public interface GroupEditResultListener extends BaseJniListener {

    void onResultCallback(GroupEditResult group);

}
