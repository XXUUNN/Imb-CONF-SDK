package com.imb.sdk.center;

public class UrlManager {
    public static final String DEFAULT_REQUEST_HOST = "https://ai.imbcloud.cn:443";

    public static String requestHost = DEFAULT_REQUEST_HOST;

    public static void setRequestHost(String url) {
        requestHost = url;
    }

//    public static final String requestHost = "http://192.168.1.14:8888/";

    private static String getBasePathUser() {
        return requestHost + "access/user/";
    }

    public static String getLoginPath() {
        return getBasePathUser() + "login";
    }

    public static String getUserInfoPath() {
        return getBasePathUser() + "login/getroles";
    }

    public static String getCheckVersionPath() {
        return getBasePathUser() + "login/checkVersions";
    }

    public static String getUpdatePwdPath() {
        return getBasePathUser() + "login/updatePassword";
    }

    public static String getUploadHeadshotPath() {
        return getBasePathUser() + "uploadHeader";
    }

    public static String getLogoutPath() {
        return getBasePathUser() + "logout";
    }

    public static String getResetPwd() {
        return getBasePathUser() + "forget";
    }

    public static String getGetMobile() {
        return getBasePathUser() + "login/getMobile";
    }

    /**
     * 删除预约会议的通知
     */
    public static String getDeleteMeetingNotify() {
        return getBasePathUser() + "deleteNotifyInfo";
    }

    public static String getBasePathCall() {
        return requestHost + "access/call/";
    }

    public static String getRecordCall() {
        return getBasePathCall() + "cdr";
    }

    public static String getBasePathCommon() {
        return requestHost + "access/common/";
    }

    public static String getSendAuthCode() {
        return getBasePathCommon() + "getIdentityCode";
    }

    public static String getBaseMeetingPath() {
        return requestHost + "access/meeting/";
    }

    /**
     * 获取会议的剩余时间 是会议才有数据
     */
    public static String getGetMeetingRemainingTime() {
        return getBaseMeetingPath() + "getFinishTime";
    }

    /**
     * 获取会议列表
     */
    public static String getGetMeetings() {
        return getBaseMeetingPath() + "app/getAppMeetingPage";
    }

    /**
     * 立即开始回忆
     */
    public static String getStartMeetingNow() {
        return getBaseMeetingPath() + "startNowMeeting";
    }

    /**
     * h5地址
     */
    public static String getBasePathH5() {
        return requestHost + "h5/#/";
    }

    /**
     * 会议详情
     */
    public static String getH5UrlMeetingInfo() {
        return getBasePathH5() + "detail/";
    }


}
