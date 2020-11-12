package com.imb.sdk.call;

import android.util.Log;

import com.imb.sdk.data.PocConstant;
import com.imb.sdk.util.GroupOperationHelper;
import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.entity.type.PocCallType;
import com.microsys.poc.jni.utils.LogUtil;

/**
 * @author - gongxun;
 * created on 2019/4/12-16:45;
 * description - 电话操作
 * jni操作接口未接入
 */
public class CallUtils {
    public static final String TAG = "CallUtils";

    /**
     * 打电话
     *
     * @param callType 电话类型
     * @param num      号码
     * @return <=0 失败 >0 通道号
     */
    public static int makeCall(@PocConstant.CallType int callType, String num) {
        LogUtil.getInstance().logWithMethod(new Exception(), " makeCall: type=" + callType + "_num=" + num, "x");
        int channel = 0;
        int jniCallType = -1;
        if (callType == PocConstant.CallType.CALL_VOICE) {
            jniCallType = PocCallType.getTypeof(PocCallType.FULLCALL);
        } else if (callType == PocConstant.CallType.CALL_VOICE_TWO_WAY) {
            jniCallType = PocCallType.getTypeof(PocCallType.HALFCALL);
        } else if (callType == PocConstant.CallType.CALL_VIDEO) {
            jniCallType = PocCallType.getTypeof(PocCallType.VIDEOCALL);
        } else if (callType == PocConstant.CallType.CALL_VIDEO_TWO_WAY) {
            jniCallType = PocCallType.getTypeof(PocCallType.HALFVIDEOCALL);
        }
        if (jniCallType > 0) {
            channel = JniUtils.getInstance().PocMakeCall(num, jniCallType, 0);
        }
        return channel;
    }

    /**
     * 接电话
     *
     * @param callType 电话类型
     * @param channel  通道号
     * @return true 成功
     */
    public static boolean acceptCall(@PocConstant.CallType int callType, int channel) {
        LogUtil.getInstance().logWithMethod(new Exception(), callType + "acceptCall: " + channel, "x");

        int result = -1;
        if (callType == PocConstant.CallType.CALL_VOICE) {
            result = JniUtils.getInstance().PocPickUp(PocCallType.getTypeof(PocCallType.FULLCALL), channel);
        } else {
            result = JniUtils.getInstance().PocPickUp(PocCallType.getTypeof(PocCallType.VIDEOCALL), channel);
        }
        return result == 0;
    }

    /**
     * 拒接电话
     *
     * @return true 成功
     */
    public static boolean hangUpCall(int channel) {
        LogUtil.getInstance().logWithMethod(new Exception(), "hangUpCall: " + channel, "x");
        int result = JniUtils.getInstance().PocHangUp(channel);

        return result == 0;
    }

    /**
     * ptt抢权
     *
     * @param channel 通道号
     * @return true 成功
     */
    public static boolean tbcpRequest(int channel) {
        Log.i(TAG, "tbcpRequest: " + channel);

        int result = JniUtils.getInstance().PocTbcpRequest(channel);
        return result == 0;
    }

    /**
     * ptt释放权利
     *
     * @param channel 通道号
     * @return true 成功
     */
    public static boolean tbcpRelease(int channel) {
        Log.i(TAG, "tbcpRelease: " + channel);

        int result = JniUtils.getInstance().PocTbcpRelease(channel);
        return result == 0;
    }

    /**
     * 点名 主持人有权利 让其强权
     */
    public static boolean tbcpForceOneRequest(int channel, String num) {
        Log.i(TAG, "tbcpForceOneRequest: " + channel + " num:" + num);
        int result = JniUtils.getInstance().PocTbcpInfo(channel, 0, num);
        return result == 0;
    }

    /**
     * 停止某人的强权 主持人能释放他人的强权
     */
    public static boolean tbcpForceOneRelease(int channel, String num) {
        Log.i(TAG, "tbcpForceOneRelease: " + channel + " num:" + num);
        int result = JniUtils.getInstance().PocTbcpInfo(channel, 2, num);
        return result == 0;
    }

    /**
     * 邀请其他人员加入这个通话，组对讲有效
     * @param groupNum 组的号码
     * @param groupType 组的类型
     * @param invitedMemberNums 邀请加入的人的号码
     * @param myNum 自己的号码
     */
    public static void inviteOneToMeeting(String groupNum, int groupType, java.lang.Iterable<String>  invitedMemberNums, String myNum){
        GroupOperationHelper.addMembers(groupNum, groupType, invitedMemberNums, myNum);
    }
}
