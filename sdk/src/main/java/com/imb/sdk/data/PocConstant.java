package com.imb.sdk.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author - gongxun;
 * created on 2019/3/21-9:58;
 * description - 公用数据
 */
public class PocConstant {

    @IntDef(value = {ServerCallMode.MODE_LEGACY, ServerCallMode.MODE_A})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServerCallMode {
        /**
         * 传统模式 所有媒体数据均由服务段转发
         */
        int MODE_LEGACY = 0;
        /**
         * 新模式 点对点的通话 由客户端直接对发 不经过服务段转发
         */
        int MODE_A = 1;
    }

    /**
     * 电话的类型
     */
    @IntDef(value = {CallType.CALL_VOICE, CallType.CALL_VOICE_TWO_WAY, CallType.CALL_VIDEO, CallType.CALL_VIDEO_TWO_WAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallType {
        /**
         * 语音通话
         */
        int CALL_VOICE = 0;
        /**
         * 语音对讲
         */
        int CALL_VOICE_TWO_WAY = 1;
        /**
         * 视频通话
         */
        int CALL_VIDEO = 2;
        /**
         * 视频对讲
         */
        int CALL_VIDEO_TWO_WAY = 3;
    }


    /**
     * 联系人的类型
     */
    @IntDef(value = {ContactType.TYPE_PERSON, ContactType.TYPE_GROUP, ContactType.TYPE_UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContactType {
        /**
         * 个人
         */
        int TYPE_PERSON = 0;
        /**
         * 组
         */
        int TYPE_GROUP = 1;
        /**
         * 类型
         */
        int TYPE_UNKNOWN = 2;
    }

    /**
     * 电话的方向
     */
    @IntDef(value = {CallDirection.DIR_OUT, CallDirection.DIR_IN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallDirection {
        int DIR_OUT = 0;
        int DIR_IN = 1;
    }

    /**
     * 电话的状态
     * 已接通，未接通
     */
    @IntDef(value = {CallStatus.STATUS_HANDLED, CallStatus.STATUS_MISSED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallStatus {
        int STATUS_MISSED = 1;
        int STATUS_HANDLED = 0;
    }

    /**
     * @see <a href="https://blog.csdn.net/wunderup/article/details/5136441">tbcp</a>
     */
    @IntDef(value = {TbcpType.GRANTED, TbcpType.DENY, TbcpType.TAKEN, TbcpType.IDLE, TbcpType.DISCONNECT, TbcpType.REVOKE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TbcpType {
        /**
         * 允许
         */
        int GRANTED = 0;
        /**
         * 拒绝
         */
        int DENY = 1;
        /**
         * 其他参与者
         */
        int TAKEN = 2;
        /**
         * 目前空闲
         */
        int IDLE = 3;
        /**
         * 断开连接
         */
        int DISCONNECT = 4;
        /**
         * 抢全失效
         */
        int REVOKE = 5;
    }


    @IntDef(value = {MessageType.TYPE_PLAIN_TEXT, MessageType.TYPE_PIC, MessageType.TYPE_SOUND,
            MessageType.TYPE_VIDEO, MessageType.TYPE_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageType {
        int TYPE_PLAIN_TEXT = 0;
        int TYPE_PIC = 1;
        int TYPE_SOUND = 2;
        int TYPE_VIDEO = 3;
        int TYPE_FILE = 4;

    }

    @IntDef(value = {MessageDir.DIR_SEND, MessageDir.DIR_RECEIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageDir {
        int DIR_SEND = 0;
        int DIR_RECEIVE = 1;
    }

    @IntDef(value = {MessageSendStatus.SUCCESS, MessageSendStatus.SENDING, MessageSendStatus.FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageSendStatus {
        /**
         * 成功
         */
        int SUCCESS = 0;
        /**
         * 发送中
         */
        int SENDING = 1;
        /**
         * 失败
         */
        int FAIL = 2;
    }

    public interface RegisterResult {
        /**
         * 未知异常错误
         */
        int RESULT_UNKOWN = -9;
        /**
         * 超时
         */
        int RESULT_TIME_OUT = -1;
        /**
         * 超时
         */
        int RESULT_TIME_OUT_1 = -2;
        /**
         * 超时
         */
        int RESULT_TIME_OUT_2 = 66;
        /**
         * 账户被遥闭
         */
        int RESULT_ACCOUNT_CLOSE = -4;
        /**
         * 密码不对
         */
        int RESULT_PASSWORD_ERROR = -5;

        /**
         * 登录被取消了
         */
        int RESULT_CANCEL = 77;

        /**
         * 连接sftp失败
         */
        int RESULT_CONNECT_SFTP_ERROR = 888;

        /**
         * 同步通讯录失败 超时或者收到了失败
         */
        int RESULT_SYNC_ADDRESS_BOOK_ERROR = 999;

        /**
         * 成功
         */
        int RESULT_SUCCESS = 0;


    }

    @IntDef({GroupType.TYPE_NORMAL, GroupType.TYPE_DYNAMIC, GroupType.TYPE_BROADCAST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GroupType {
        /**
         * 普通组 不能增删
         */
        int TYPE_NORMAL = 0;
        /**
         * 动态组 能添加和删除
         */
        int TYPE_DYNAMIC = 1;
        /**
         * 临时组
         */
        int TYPE_TEMP = 2;
        /**
         * 只有创建者能说话
         */
        int TYPE_BROADCAST = 3;

    }

    /**
     * 电话的业务类型
     */
    @IntDef({CallServiceType.DEFAULT, CallServiceType.ONE_OFF, CallServiceType.RESERVED_MEETING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallServiceType {
        /**
         * 普通的电话 （除另外两种的普通电话）
         */
        int DEFAULT = 0;
        /**
         * 一次性的电话（poc临时组的通话 且不是美博云会议）
         */
        int ONE_OFF = 1;
        /**
         * 预约的会议（美博云的预约会议）
         */
        int RESERVED_MEETING = 2;
    }

    @IntDef({CallUserType.NORMAL, CallUserType.DISPATCHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallUserType {
        /**
         * 普通用户
         */
        int NORMAL = 0;
        /**
         * 调度用户
         */
        int DISPATCHER = 1;
    }
}