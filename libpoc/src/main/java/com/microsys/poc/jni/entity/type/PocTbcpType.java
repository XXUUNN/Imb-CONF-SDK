package com.microsys.poc.jni.entity.type;

/**
 * tbcp 消息类型
 */
public enum PocTbcpType {
    TB_Request,        //请求允许发送一个通话突发，即抢权
    TB_Granted,        //抢权通过，可以说话
    TB_Taken,        //话语权被某个号码抢了
    TB_Deny,        //抢权失败，不可以说话
    TB_Release,        //结束通话突发，即放权
    TB_Idle,        //会话处于空闲状态，服务端接收抢权
    TB_Revoke,        //撤回已经授予的讲话授权 6
    TB_Ack,            //响应服务端的消息
    TB_Position,    //
    TB_Queued,        //
    TB_Status,        //所有成员接入状态
    TB_Disconnect,    //会话结束
    TB_Reserved_2,    //
    TB_Reserved_3,    //
    TB_Alloc,        //
    TB_Connect,        //会话开始
    TB_Reserved_5,    //
    TB_Reserved_6,    //
    TB_Taken_No_ACK,//
    TB_Reserved_1,    //
    TB_Timeout,        //
    TB_RAISE_UP_HAND,        //举手，有在抢权中 其他人在抢权 就是一次 举手，
    TB_EX_MSG,//收到消息自动抢权、或者收到
    UNKNOWN;

    public static PocTbcpType of(int value) {
        PocTbcpType type;

        switch (value) {
            case 0:
                type = PocTbcpType.TB_Request;
                break;
            case 1:
                type = PocTbcpType.TB_Granted;
                break;
            case 2:
                type = PocTbcpType.TB_Taken;
                break;
            case 3:
                type = PocTbcpType.TB_Deny;
                break;
            case 4:
                type = PocTbcpType.TB_Release;
                break;
            case 5:
                type = PocTbcpType.TB_Idle;
                break;
            case 6:
                type = PocTbcpType.TB_Revoke;
                break;
            case 7:
                type = PocTbcpType.TB_Ack;
                break;
            case 8:
                type = PocTbcpType.TB_Position;
                break;
            case 9:
                type = PocTbcpType.TB_Queued;
                break;
            case 10:
                type = PocTbcpType.TB_Status;
                break;
            case 11:
                type = PocTbcpType.TB_Disconnect;
                break;
            case 12:
                type = PocTbcpType.TB_Reserved_2;
                break;
            case 13:
                type = PocTbcpType.TB_Reserved_3;
                break;
            case 14:
                type = PocTbcpType.TB_Alloc;
                break;
            case 15:
                type = PocTbcpType.TB_Connect;
                break;
            case 16:
                type = PocTbcpType.TB_Reserved_5;
                break;
            case 17:
                type = PocTbcpType.TB_Reserved_6;
                break;
            case 18:
                type = PocTbcpType.TB_Taken_No_ACK;
                break;
            case 19:
                type = PocTbcpType.TB_Reserved_1;
                break;
            case 20:
                type = PocTbcpType.TB_Timeout;
                break;
            case 21:
                type = PocTbcpType.TB_RAISE_UP_HAND;
                break;
            case 22:
                type = PocTbcpType.TB_EX_MSG;
                break;
            default:
                type = PocTbcpType.UNKNOWN;
                break;
        }

        return type;
    }
}
