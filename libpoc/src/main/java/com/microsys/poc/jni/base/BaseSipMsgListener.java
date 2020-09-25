package com.microsys.poc.jni.base;

import com.microsys.poc.jni.entity.SipMsg;
import com.microsys.poc.jni.entity.type.PocCallType;
import com.microsys.poc.jni.entity.type.PocSipMsgDirct;
import com.microsys.poc.jni.entity.type.PocSipMsgType;
import com.microsys.poc.jni.listener.SipMsgListener;
import com.microsys.poc.jni.utils.LogUtil;

/**
 * BaseSipMsgListener
 *
 * @author zhangcd
 * <p>
 * 2014-11-24
 */
public abstract class BaseSipMsgListener implements SipMsgListener {
    @Override
    public void onRecvSipMsg(SipMsg sipMsg) {

        if (sipMsg == null) {
            return;
        }
        String tel = sipMsg.getCallTel();
        PocSipMsgType msgType = PocSipMsgType.of(sipMsg.getMsgType());
        PocCallType callType = PocCallType.of(sipMsg.getCallType());
        PocSipMsgDirct dirctType = PocSipMsgDirct.of(sipMsg.getCallDirc());
        int iRet = sipMsg.getRet();

        LogUtil.getInstance().logWithMethod(new Exception(), "----recvSipMsg--tel = " + tel + " msgtype = " + msgType + " callType = " + callType + " iRet = " + iRet + "  dirctType =" + dirctType, "Zhaolg");

        switch (msgType) {
            case UNKNOWN:
                break;
            case REGISTER:
                break;
            case REGISTER_OK:
                onRegisterResult(tel, iRet);
                break;
            case INVITE: //收到呼入
                if (PocSipMsgDirct.CALLIN == dirctType) {//呼入
                    switch (callType) {
                        case UNKNOW:
                            break;
                        case HALFCALL:
                            //半双工
                            //需要自己判断是组还是人
                            LogUtil.getInstance().logWithMethod(new Exception(), "----recv HALFCALL ---", "Zhaolg");
                            onReceivedHalfVoiceCall(sipMsg.getCallTel(), sipMsg.getCallChannel());
                            break;
                        case FULLCALL:
                            //只有单人的
                            LogUtil.getInstance().logWithMethod(new Exception(), "----recv fullcall ---", "Zhaolg");
                            onReceivedFullVoiceCall(sipMsg.getCallTel(), sipMsg.getCallChannel());
                            break;
                        case VIDEOCALL:
                            //只有单人的
                            LogUtil.getInstance().logWithMethod(new Exception(), "----recv videocall ---", "Zhaolg");
                            onReceivedFullVideoCall(sipMsg.getCallTel(), sipMsg.getCallChannel());
                            break;
                        case HALFVIDEOCALL:
                            LogUtil.getInstance().logWithMethod(new Exception(), "----recv halfvideocall ---", "Zhaolg");
                            onReceivedHalfVideoCall(sipMsg.getCallTel(), sipMsg.getCallChannel());
                            break;
                        case MESSAGE:
                            break;
                        case PICTURE:
                            break;
                        case VIDEOREC:
                            break;
                        case AUDIOREC:
                            break;
                        case MONITOR:
                            break;
                        case REFER:
                            break;
                        case HOLDON:
                            break;
                        default:
                            break;
                    }


                } else if (PocSipMsgDirct.CALLOUT == dirctType) {//呼出
                    if (-1 == iRet) {
                        //呼出失败
                        LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + true, "Zhaolg");
                        onCallFail(sipMsg.getCallChannel(), true);
                    }
                } else {
                    //doNothing
                }
                break;
            case TRY:
                if (PocSipMsgDirct.CALLOUT == dirctType) {//呼出
                    if (-1 == iRet) {
                        //发送100try失败
                        LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + true, "Zhaolg");
                        onCallFail(sipMsg.getCallChannel(), true);
                    }
                }
                break;
            case RING:
                if (PocSipMsgDirct.CALLIN == dirctType) {//呼入
                    if (-1 == iRet) {
                        //发送ring失败
                        LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + false, "Zhaolg");
                        onCallFail(sipMsg.getCallChannel(), false);
                    }
                } else if (PocSipMsgDirct.CALLOUT == dirctType) {
					LogUtil.getInstance().logWithMethod(new Exception(), "----onPlayRing ---", "Zhaolg");
                    onPlayRing();
                }
                break;
            case RING_183:
                if (PocSipMsgDirct.CALLIN == dirctType) {//呼入
                    if (-1 == iRet) {
                        //发送ring失败
                        LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + false, "Zhaolg");
                        onCallFail(sipMsg.getCallChannel(), false);
                    }
                } else if (PocSipMsgDirct.CALLOUT == dirctType) {
					LogUtil.getInstance().logWithMethod(new Exception(), "----onStopPlayRing ---", "Zhaolg");
                    onStopPlayRing();
                }
                break;
            case OK:
                if (PocSipMsgDirct.CALLIN == dirctType) {//呼入
                    if (-1 == iRet) {
                        //发送200ok失败
                        LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + false, "Zhaolg");
                        onCallFail(sipMsg.getCallChannel(), false);
                    }
                } else if (PocSipMsgDirct.CALLOUT == dirctType) {
                	//呼出
					LogUtil.getInstance().logWithMethod(new Exception(), " notifyCallOutSuc", "Zhaolg");
                    onCallOutSucess();
                }
                break;
            case ACK:
                if (PocSipMsgDirct.CALLOUT == dirctType) {
                	//呼出
                    if (-1 == iRet) {
                        //发送ack失败
                        LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + true, "Zhaolg");
                        onCallFail(sipMsg.getCallChannel(), true);
                    }
                }else{

				}
                break;
            case BYE:
				LogUtil.getInstance().logWithMethod(new Exception(), " onCallHangUp", "Zhaolg");
                onCallHangUp();
                break;
            case BYE_OK:
                break;
            case ERROR403:
            case ERROR480:
            case ERROR408:
            case ERROR404:
            	boolean isCallOut;
				if (PocSipMsgDirct.CALLOUT == dirctType) {
					isCallOut = true;
				} else {
					isCallOut = false;
				}
				LogUtil.getInstance().logWithMethod(new Exception(), "----call fail ---isCallOut=" + isCallOut, "Zhaolg");
				onCallFail(sipMsg.getCallChannel(), isCallOut);
                break;
            case MESSAGE:
                break;
            case MESSAGE_OK:
                break;
            case NOTIFY:
                break;
            default:
                break;
        }
    }

    /**
     * 通话挂断
     */
    protected abstract void onCallHangUp();

    /**
     * 呼出成功 进入到通话中
     */
    protected abstract void onCallOutSucess();

    /**
     * 需要停止振铃
     */
    protected abstract void onStopPlayRing();

    /**
     * 收到通话 需要自己 振铃
     */
    protected abstract void onPlayRing();

    /**
     *  呼叫失败
     * @param isCallOut true 呼出失败 false 别人呼入失败
     * @param callChannel 通道号
     */
    protected abstract void onCallFail(int callChannel, boolean isCallOut);

    /**
     *  收到半双工 视频呼叫
     * @param callTel 号码
     * @param callChannel 通道号
     */
    protected abstract void onReceivedHalfVideoCall(String callTel, int callChannel);

    /**
     *  收到全双工 视频呼叫
     * @param callTel 号码
     * @param callChannel 通道号
     */
    protected abstract void onReceivedFullVideoCall(String callTel, int callChannel);

    /**
     *  收到全双工 语音呼叫
     * @param callTel 号码
     * @param callChannel 通道号
     */
    protected abstract void onReceivedFullVoiceCall(String callTel, int callChannel);

    /**
     *  收到半双工 语音呼叫
     * @param callTel 号码
     * @param callChannel 通道号
     */
    protected abstract void onReceivedHalfVoiceCall(String callTel, int callChannel);

    /**
     * PoC的注册结果
     *
     * @param num    注册的人的号码
     * @param result 结果码
     */
    protected abstract void onRegisterResult(String num, int result);
}
