package com.microsys.poc.jni.entity;

/**
 * @author - gongxun;
 * created on 2019/8/29-11:35;
 * description - 用户在会议中的变化
 */
public class UserChangeInMeeting {


    /**
     * 是否需要刷新一个KeyFrame 视频
     * true false
     */
    private String flag;
    /**
     * 0 当前在线的所有人
     */
    private int type;
    /**
     * 会议的发起者
     */
    private String caller;
    private String onlineList;

    public UserChangeInMeeting(int type, String caller, String onlineList, String flag) {
        this.type = type;
        this.caller = caller;
        this.onlineList = onlineList;
        this.flag = flag;
    }

    public String getCaller() {
        return caller;
    }

    public String getOnlineList() {
        return onlineList;
    }

    public int getType() {
        return type;
    }

    public String getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return "UserChangeInMeeting{" +
                "flag='" + flag + '\'' +
                ", type=" + type +
                ", caller='" + caller + '\'' +
                ", onlineUidList='" + onlineList + '\'' +
                '}';
    }
}
