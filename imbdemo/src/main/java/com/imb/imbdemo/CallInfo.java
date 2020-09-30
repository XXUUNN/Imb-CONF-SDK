package com.imb.imbdemo;

import android.os.Parcel;
import android.os.Parcelable;

import com.imb.sdk.data.PocConstant;

/**
 * @author - gongxun;
 * created on 2019/4/11-17:49;
 * description - 电话详情信息
 */
public class CallInfo implements Parcelable {


    public String callNum;

    /**
     *  0表示播出 1表示呼入
     */
    @PocConstant.CallDirection
    public int callDir;

    /**
     * 组还是个人
     * 0 个人
     * 1 组
     */
    @PocConstant.ContactType
    public int numType;

    /**
     * 电话类型
     * 0 语音通话
     * 1 语音对讲
     * 2 视频通话
     * 3 视频对讲
     */
    @PocConstant.CallType
    public int callType;

    /**
     * 通道号
     */
    public int channel;


    public CallInfo() {

    }

    public CallInfo(String callNum, @PocConstant.CallDirection int callDir,
                    @PocConstant.ContactType int numType,
                    @PocConstant.CallType int callType, int channel) {
        this.callNum = callNum;
        this.callDir = callDir;
        this.numType = numType;
        this.callType = callType;
        this.channel = channel;
    }

    protected CallInfo(Parcel in) {
        callNum = in.readString();
        callDir = in.readInt();
        numType = in.readInt();
        callType = in.readInt();
        channel = in.readInt();
    }

    public static final Creator<CallInfo> CREATOR = new Creator<CallInfo>() {
        @Override
        public CallInfo createFromParcel(Parcel in) {
            return new CallInfo(in);
        }

        @Override
        public CallInfo[] newArray(int size) {
            return new CallInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(callNum);
        dest.writeInt(callDir);
        dest.writeInt(numType);
        dest.writeInt(callType);
        dest.writeInt(channel);
    }
}
