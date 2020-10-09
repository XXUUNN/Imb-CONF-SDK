package com.imb.sdk.data.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * @author - gongxun;
 * created on 2019/12/3-10:36;
 * description - 会议
 */
public class Meeting {
    public String id;

    @JSONField(name = "meetingSubject")
    public String name;

    public Date createTime;

    public String hostName;

    public String hostTel;

    /**
     * 0视频 1音频
     */
    public int meetingType;

    public Date startTime;

    public Date endTime;

    public int userCount;

    public String remark;

    /**
     * 会议状态 0 未开始，1 进行中，2已结束
     */
    public int meetingState;


}
