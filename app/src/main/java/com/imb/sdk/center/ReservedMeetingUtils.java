package com.imb.sdk.center;

/**
 * @author - gongxun;
 * created on 2019/12/3-15:27;
 * description - 预约会议的相关操作
 */
public class ReservedMeetingUtils {
    public static final String TAG = "ReservedMeetingUtils";
    /**
     * 预约会议已读
     * 忽略请求结果
     */
    public static void updateMeetingAlreadyRead() {
        ImbHttpClient.updateMeetingAlreadyRead();
    }

    public static String getReservedMeetingUrl(String meetingId, String token) {
        String url = UrlManager.getH5UrlMeetingInfo() + meetingId + "?token=" + token;
        return url;
    }
}
