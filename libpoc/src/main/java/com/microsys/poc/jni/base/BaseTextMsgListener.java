package com.microsys.poc.jni.base;

import com.microsys.poc.jni.entity.TextMsg;
import com.microsys.poc.jni.entity.type.PocCallType;
import com.microsys.poc.jni.entity.type.PocSipMsgType;
import com.microsys.poc.jni.listener.TextMsgListener;
import com.microsys.poc.jni.utils.LogUtil;

/**
 * BaseTextMsgListener
 *
 * @author zhangcd
 * <p>
 * 2014-11-24
 */
public abstract class BaseTextMsgListener implements TextMsgListener {
    @Override
    public void onRecvTextMsg(TextMsg txtMsg) {
        //需要检测内容来判断是收到视频还是图片或者是普通消息
        PocSipMsgType msgType = PocSipMsgType.of(txtMsg.getMsgType());
        PocCallType callType = PocCallType.of(txtMsg.getCallType());

        LogUtil.getInstance().logWithMethod(new Exception(),
				msgType + " mediaType="+callType+" callTel=" + txtMsg.getcallTel() + " FirstCallTel=" +
						txtMsg.getFirstCallTel() + " text=" + txtMsg.getText(), "x");
        switch (msgType) {
            case UNKNOWN:
                break;
            case MESSAGE:
                switch (callType) {
                    case MESSAGE:
                        notifyRecvTxtMsg(txtMsg.getcallTel(), txtMsg.getFirstCallTel(), txtMsg.getText());
                        break;
                    case CALLLOCATION:
                        break;
                    case PICTURE:
                    	//收到图片
						notifyRecvTxtPic(txtMsg.getcallTel(), txtMsg.getFirstCallTel(), txtMsg.getText());
                        break;
                    case VIDEOREC:
                    	//视频传输
						notifyRecvTxtVideo(txtMsg.getcallTel(), txtMsg.getFirstCallTel(), txtMsg.getText());
                        break;
                    case AUDIOREC:
                    	//音频传输
						notifyRecvTxtAudio(txtMsg.getcallTel(), txtMsg.getFirstCallTel(), txtMsg.getText());
                        break;
                    case CALLFILE:
                    	//文件传输
						notifyRecvTxtFile(txtMsg.getcallTel(), txtMsg.getFirstCallTel(), txtMsg.getText());
                        break;
                    default:
                        break;
                }
                break;
            case MESSAGE_OK:
				notifySendMessageSuc(txtMsg.getcallTel());
                break;
            default:
                notifySendMessageFail(txtMsg.getcallTel());
                break;
        }
    }

	/**
	 * 接收到文本消息
	 * @param numA 组的号码或者个人的号码
	 * @param numB 如果numA是组号码 那么这个值就是具体的发送的人的号码，numA是个人号码 那么这个值无意义
	 * @param text 具体消息
	 */
	protected abstract void notifyRecvTxtMsg(String numA, String numB, String text);

	/**
	 * 接收到图片消息
	 * @param numA 组的号码或者个人的号码
	 * @param numB 如果numA是组号码 那么这个值就是具体的发送的人的号码，numA是个人号码 那么这个值无意义
	 * @param text 具体消息
	 */
	protected abstract void notifyRecvTxtPic(String numA, String numB, String text);

	/**
	 * 接收到视频消息
	 * @param numA 组的号码或者个人的号码
	 * @param numB 如果numA是组号码 那么这个值就是具体的发送的人的号码，numA是个人号码 那么这个值无意义
	 * @param text 具体消息
	 */
	protected abstract void notifyRecvTxtVideo(String numA, String numB, String text);

	/**
	 * 接收到录音消息
	 * @param numA 组的号码或者个人的号码
	 * @param numB 如果numA是组号码 那么这个值就是具体的发送的人的号码，numA是个人号码 那么这个值无意义
	 * @param text 具体消息
	 */
	protected abstract void notifyRecvTxtAudio(String numA, String numB, String text);

	/**
	 * 接收到文件消息
	 * @param numA 组的号码或者个人的号码
	 * @param numB 如果numA是组号码 那么这个值就是具体的发送的人的号码，numA是个人号码 那么这个值无意义
	 * @param text 具体消息
	 */
	protected abstract void notifyRecvTxtFile(String numA, String numB, String text);

	/**
	 * 发送成功
	 * @param channel 发送消息时返回的值一一对应
	 */
	protected abstract void notifySendMessageSuc(String channel);

	/**
	 * 发送失败
	 * @param channel 发送消息时返回的值一一对应
	 */
	protected abstract void notifySendMessageFail(String channel);
}
