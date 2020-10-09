package com.microsys.poc.jni.base;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.microsys.poc.jni.entity.TbcpMsg;
import com.microsys.poc.jni.entity.type.PocTbcpType;
import com.microsys.poc.jni.listener.TbcpMsgListener;
import com.microsys.poc.jni.utils.LogUtil;

import java.util.Map;

/**
 * BaseTbcpMsgListener
 *
 * @author zhangcd
 * <p>
 * 2014-11-24
 */
public abstract class BaseTbcpMsgListener implements TbcpMsgListener {
    @Override
    public void onRecvTbcpMsg(TbcpMsg msg) {
        if (msg == null) {
            return;
        }
        PocTbcpType tbcpType = PocTbcpType.of(msg.getMsgType());
        String tel = msg.getTel();
        int result = msg.getRet();

        switch (tbcpType) {
            case TB_Request:
                break;
            case TB_Granted:
                notifyTbcpGranted();
                break;
            case TB_Deny:
				notifyTbcpDeny();
                break;
            case TB_Release:
                break;
            case TB_Taken:
				notifyTbcpTaken(tel);
                break;
            case TB_Revoke:
				notifyTbcpRevoke();
                break;
            case TB_Idle:
				notifyTbcpIdle();
                break;
            case TB_Ack:
                break;
            case TB_Connect:
                break;
            case TB_Disconnect:
				notifyTbcpDisconnect(result);
                break;
            case TB_Status:
                break;
            case TB_Alloc:
                break;
            case TB_RAISE_UP_HAND:
				notifyTbcpRaiseUpHand(tel);
                break;
            case TB_EX_MSG:
				notifyTbcpExMsg(result, tel);
                break;
            default:
                break;
        }

    }

	/**
	 *
	 * @param flag 0:被人点名，这时需要抢权 1:msg携带的是在线人员的ssrc对应关系
	 * @param msg 当flag是1时 英文冒号和分号隔开 号码:SSRC;号码:SSRC
	 */
	protected  void notifyTbcpExMsg(int flag, String msg){
		if (flag == 0) {
			notifyNeedTbcpRequest();
		}else if(flag == 1){
			if (!TextUtils.isEmpty(msg)) {
				String[] split = msg.split(";");
				if (split != null && split.length > 0) {
					 ArrayMap<String, Integer> map = new ArrayMap<>();
					for (String s : split) {
						String[] strings = s.split(":");
						if (strings != null && strings.length == 2) {
							try {
								String num = strings[0];
								int ssrc = Integer.parseInt(strings[1]);
								map.put(num,ssrc);
							} catch (NumberFormatException e) {
								LogUtil.getInstance().logWithMethod(new Exception(),"notifyTbcpExMsg parseInfo: " + strings + " 解析不正确","x");
								e.printStackTrace();
							}
						}
					}
					notifySsrcRelation(map);
				}
			}
		}
	}

	/**
	 * 被主持人点名要求发言了。 需要抢权
	 */
	protected abstract void notifyNeedTbcpRequest();

	/**
	 * 多人视频半双工会有
	 * 当前视频流的对应的关系 流的号码:SSRC;号码:SSRC
	 * @param map [号码，SSRC]
	 */
	protected abstract void notifySsrcRelation(Map<String, Integer> map);

	/**
	 * 举手想发言
	 * @param tel 想抢权的人
	 */
	protected abstract void notifyTbcpRaiseUpHand(String tel);

	/**
	 * disconnect
	 * @param result 结果码 0:主动挂机 1:空闲超时挂机 2:发起者结束呼叫 3:所有被叫终端已挂机
	 */
	protected abstract void notifyTbcpDisconnect(int result);

	/**
	 * idle
	 */
	protected abstract void notifyTbcpIdle();

	/**
	 * revoke
	 */
	protected abstract void notifyTbcpRevoke();

	/**
	 * taken
	 * @param tel taken的人号码
	 */
	protected abstract void notifyTbcpTaken(String tel);

	/**
	 * deny
	 */
	protected abstract void notifyTbcpDeny();

	/**
	 * granted
	 */
	protected abstract void notifyTbcpGranted();
}
