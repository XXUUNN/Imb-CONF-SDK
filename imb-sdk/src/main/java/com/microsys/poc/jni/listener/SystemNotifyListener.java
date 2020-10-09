package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.VersionUpdateEvent;

/**
 * 监听用户重复登录、版本更新、用户密码修改
 */
public interface SystemNotifyListener extends BaseJniListener {
    /**
     * 通知更新事件
     * 注：暂不支持
     */
    void notifyUpdateEvent(VersionUpdateEvent event);

    /**
     * 通知用户另一地点登录(下线通知)
     */
    void notifyUserTwoLoadingEvent();

}
