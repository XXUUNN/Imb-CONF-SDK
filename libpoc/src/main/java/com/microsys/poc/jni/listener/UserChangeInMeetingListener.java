package com.microsys.poc.jni.listener;

import com.microsys.poc.jni.entity.UserChangeInMeeting;

/**
 * @author - gongxun;
 * created on 2020/9/23-16:40;
 * description -
 */
public interface UserChangeInMeetingListener extends BaseJniListener {
    /**
     * 在会议中的信息
     * @param userChangeInMeeting 变化
     */
    void onRecUserChangedInMeeting(UserChangeInMeeting userChangeInMeeting);
}
