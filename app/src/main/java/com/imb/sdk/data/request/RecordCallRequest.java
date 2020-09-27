package com.imb.sdk.data.request;

import com.microsys.imbconf.util.DateUtils;
import com.microsys.poc.constants.Constant;

import java.util.Date;

/**
 * @author - gongxun;
 * created on 2019/7/31-10:35;
 * description - 保存通话记录
 */
public class RecordCallRequest {

    /**
     * 主叫 did
     */
    public long callerId;
    public String callerName;
    public String callerTel;

    /**
     * 被叫的did
     * 如果是会议组 组员的did 用逗号间隔 123,51,123
     * 同理 名字和号码
     */
    public String calledId;
    public String calledName;
    public String calledTel;

    /**
     * 开始时间
     * 格式yyyy-mm-dd hh:mm:ss
     */
    public String callTime;

    /**
     * 英大事件
     */
    public String answerTime;

    public String endTime;

    /**
     * 结束时间 s
     */
    public long length;

    /**
     * 电话的类型
     * 呼叫类型。1：语音通话 2：视频通话 3：语音对讲 4：视频对讲 5：语音会议 6：视频会议
     * 目前只有 1 2 5 6四种
     */
    public int callType;

    public RecordCallRequest(long callerId, String callerName, String callerTel,
                             String calledId, String calledName, String calledTel,
                             long callTime, long answerTime, long endTime, long length, @Constant.CallType int callType, @Constant.ContactType int contactType) {
        this.callerId = callerId;
        this.callerName = callerName;
        this.callerTel = callerTel;
        this.calledId = calledId;
        this.calledName = calledName;
        this.calledTel = calledTel;

        String pattern = "yyyy-MM-dd HH:mm:ss";
        //打电话的开始时间
        if (callTime!=0) {
            this.callTime = DateUtils.dateToString(new Date(callTime), pattern);
        }

        //接通的开始时间
        if (answerTime != 0) {
            this.answerTime = DateUtils.dateToString(new Date(answerTime), pattern);
        }

        //呼叫结束时间
        if (endTime != 0) {
            this.endTime = DateUtils.dateToString(new Date(endTime), pattern);
        }

        this.length = length;

        if (contactType == Constant.ContactType.TYPE_PERSON) {
            if (callType == Constant.CallType.CALL_VOICE) {
                //语音通话
                this.callType = 1;
            } else if (callType == Constant.CallType.CALL_VIDEO) {
                //视频通话
                this.callType = 2;
            }
        } else if (contactType == Constant.ContactType.TYPE_GROUP) {
            if (callType == Constant.CallType.CALL_VOICE_TWO_WAY) {
                //语音会议
                this.callType = 5;
            } else if (callType == Constant.CallType.CALL_VIDEO_TWO_WAY) {
                //视频会议
                this.callType = 6;
            }
        }
    }
    
    
}
