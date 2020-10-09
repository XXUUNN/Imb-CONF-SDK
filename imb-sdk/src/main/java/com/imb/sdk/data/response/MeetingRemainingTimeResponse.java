package com.imb.sdk.data.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author - gongxun;
 * created on 2019/11/22-10:25;
 * description - 获取会议结束时间
 */
public class MeetingRemainingTimeResponse extends BaseResponse{
    public MeetingInfo data;


    public static class MeetingInfo{
        /**
         * 会议的名称
         */
        public String meetingName;
        /**
         * 单位second
         */
        @JSONField(name = "time")
        public long remainingTime;
    }
}
