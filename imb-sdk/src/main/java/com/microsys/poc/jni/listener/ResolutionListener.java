package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.Resolution;

public interface ResolutionListener extends BaseJniListener {

    void onRecvResolution(Resolution Resolution);
}
