package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.SynGroupListenStateInfo;

public interface SynGroupListenStateListener extends BaseJniListener {

    void onRecvSynGroupListenState(SynGroupListenStateInfo groupListenStateInfo);
}
