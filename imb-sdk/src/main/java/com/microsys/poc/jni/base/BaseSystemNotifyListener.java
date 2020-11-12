package com.microsys.poc.jni.base;

import com.microsys.poc.jni.entity.VersionUpdateEvent;
import com.microsys.poc.jni.listener.SystemNotifyListener;

/**
 * 监听用户重复登录
 */
public abstract class BaseSystemNotifyListener implements SystemNotifyListener {
    /**
     * 通知更新事件
     * 注：暂不支持
     */
    @Override
    public void notifyUpdateEvent(VersionUpdateEvent event) {

    }

}
