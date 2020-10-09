package com.imb.sdk.data.response;

import com.imb.sdk.data.entity.Meeting;

import java.util.List;

/**
 * @author - gongxun;
 * created on 2019/12/3-14:00;
 * description - 获取会议
 */
public class MeetingResponse extends BaseResponse {
    public List<Meeting> data;
}
