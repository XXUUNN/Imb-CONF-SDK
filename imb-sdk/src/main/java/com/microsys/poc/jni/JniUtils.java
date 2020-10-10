package com.microsys.poc.jni;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Environment;
import android.util.Log;

import com.microsys.poc.jni.entity.AddressBook;
import com.microsys.poc.jni.entity.AddressUrl;
import com.microsys.poc.jni.entity.CallBackInfo;
import com.microsys.poc.jni.entity.CallTypeChange;
import com.microsys.poc.jni.entity.EsipHeadMsg;
import com.microsys.poc.jni.entity.GroupEditResult;
import com.microsys.poc.jni.entity.GroupListenStateInfo;
import com.microsys.poc.jni.entity.LocalPower;
import com.microsys.poc.jni.entity.PwdInfo;
import com.microsys.poc.jni.entity.Resolution;
import com.microsys.poc.jni.entity.SelfInfo;
import com.microsys.poc.jni.entity.SipMsg;
import com.microsys.poc.jni.entity.SktInfo;
import com.microsys.poc.jni.entity.SynGroupListenStateInfo;
import com.microsys.poc.jni.entity.TbcpMsg;
import com.microsys.poc.jni.entity.TextMsg;
import com.microsys.poc.jni.entity.UserChangeInMeeting;
import com.microsys.poc.jni.entity.UserState;
import com.microsys.poc.jni.entity.UserTypeInfo;
import com.microsys.poc.jni.entity.VersionUpdateEvent;
import com.microsys.poc.jni.entity.VideoData;
import com.microsys.poc.jni.entity.VideoRecvData;
import com.microsys.poc.jni.entity.type.CallBackInfoType;
import com.microsys.poc.jni.entity.type.PocSipMsgType;
import com.microsys.poc.jni.listener.AddrBookListener;
import com.microsys.poc.jni.listener.AddressUrlListener;
import com.microsys.poc.jni.listener.BaseJniListener;
import com.microsys.poc.jni.listener.CallTypeChangeListener;
import com.microsys.poc.jni.listener.EsipHeadListener;
import com.microsys.poc.jni.listener.GroupEditResultListener;
import com.microsys.poc.jni.listener.GroupListenStateListener;
import com.microsys.poc.jni.listener.LocalMediaPowerListener;
import com.microsys.poc.jni.listener.ResetPwdListener;
import com.microsys.poc.jni.listener.ResolutionListener;
import com.microsys.poc.jni.listener.SelfInfoListener;
import com.microsys.poc.jni.listener.SipMsgListener;
import com.microsys.poc.jni.listener.SktInfoListener;
import com.microsys.poc.jni.listener.SynGroupListenStateListener;
import com.microsys.poc.jni.listener.SystemNotifyListener;
import com.microsys.poc.jni.listener.TbcpMsgListener;
import com.microsys.poc.jni.listener.TextMsgListener;
import com.microsys.poc.jni.listener.UserChangeInMeetingListener;
import com.microsys.poc.jni.listener.UserStateListener;
import com.microsys.poc.jni.listener.UserTypeInfoListener;
import com.microsys.poc.jni.show.MultiVideoShowManager;
import com.microsys.poc.jni.show.RemoteVideoViewGL;
import com.microsys.poc.jni.show.RemoteViewManager;
import com.microsys.poc.jni.show.VideoShowManager;
import com.microsys.poc.jni.utils.AvcDecoderAsync;
import com.microsys.poc.jni.utils.LogUtil;
import com.microsys.poc.jni.utils.WaitNotify;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author zhangcd
 * <p>
 * 2014-11-13
 */
public final class JniUtils {
    static {
        System.loadLibrary("pocfec");
        System.loadLibrary("microsys_video");
        System.loadLibrary("avutil-54");
        System.loadLibrary("avcodec-56");
        System.loadLibrary("avformat-56");
        System.loadLibrary("swscale-3");
        System.loadLibrary("video");
        System.loadLibrary("pocclient_jni");
        System.loadLibrary("opus");
        System.loadLibrary("ogg");
        System.loadLibrary("webrtc_ns");
        System.loadLibrary("webrtc_aecm");
        System.loadLibrary("webrtc_vad");
        System.loadLibrary("my_webrtc");
        System.loadLibrary("webrtc_audio_preprocessing");
    }

    private static final JniUtils instance = new JniUtils();

    // 音频获取源
    private int audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private int sampleRateInHz;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    @SuppressWarnings("deprecation")
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int streamType = AudioManager.STREAM_VOICE_CALL;

    // 缓冲区字节大小
    private int bufferSizeInBytesIn;
    private int bufferSizeInBytesOut;
    private int sizeInShorts;

    private List<SipMsgListener> mSipMsgListeners = new CopyOnWriteArrayList<SipMsgListener>();
    private List<TbcpMsgListener> mTbcpMsgListeners = new CopyOnWriteArrayList<TbcpMsgListener>();
    private List<TextMsgListener> mTextMsgListeners = new CopyOnWriteArrayList<TextMsgListener>();
    private List<SelfInfoListener> mSelfInfoListeners = new CopyOnWriteArrayList<SelfInfoListener>();
    private List<AddrBookListener> mAddrBookListeners = new CopyOnWriteArrayList<AddrBookListener>();
    private List<UserStateListener> mUserStateListeners = new CopyOnWriteArrayList<UserStateListener>();
    private List<ResolutionListener> mResolutionListeners = new CopyOnWriteArrayList<ResolutionListener>();
    private List<SystemNotifyListener> mSystemNotifyListeners = new CopyOnWriteArrayList<SystemNotifyListener>();
    private List<LocalMediaPowerListener> mLocalPowerListeners = new CopyOnWriteArrayList<LocalMediaPowerListener>();
    private List<GroupEditResultListener> mTempGroupListeners = new CopyOnWriteArrayList<GroupEditResultListener>();
    private List<SktInfoListener> mSktInfoListeners = new CopyOnWriteArrayList<SktInfoListener>();
    private List<ResetPwdListener> mResetPwdListeners = new CopyOnWriteArrayList<ResetPwdListener>();
    private List<GroupListenStateListener> mGroupStateListeners = new CopyOnWriteArrayList<GroupListenStateListener>();
    private List<SynGroupListenStateListener> mSynGroupStateListeners = new CopyOnWriteArrayList<SynGroupListenStateListener>();
    private List<CallTypeChangeListener> mTypeChangeListeners = new CopyOnWriteArrayList<CallTypeChangeListener>();
    private List<EsipHeadListener> esipHeadListeners = new CopyOnWriteArrayList<EsipHeadListener>();
    private List<AddressUrlListener> addressUrlListeners = new CopyOnWriteArrayList<AddressUrlListener>();
    private List<UserTypeInfoListener> mUserTypeInfoListeners = new CopyOnWriteArrayList<UserTypeInfoListener>();
    private List<UserChangeInMeetingListener> mUserChangeInMeetingListenters = new CopyOnWriteArrayList<UserChangeInMeetingListener>();

    private List<CallBackInfo> callBackInfos = new ArrayList<CallBackInfo>();

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private boolean isAudioRecordStart = false;
    private boolean isAudioTrackStart = false;

    private Thread callBackThread;
    //默认大于500为大组
    private int bigGroupNum = 500;
    private List<String> esipHeadList = new ArrayList<String>();

    private JniUtils() {
        sampleRateInHz = 48000;
        sizeInShorts = 960;
        LogUtil.getInstance().logWithMethod(new Exception(), "jniUtils init", "x");
    }

    public static JniUtils getInstance() {
        return instance;
    }

    private void createRecordAndTrack() {
        switch (sampleRateInHz) {
            case 8000:
                sizeInShorts = 160;
                break;
            case 16000:
                sizeInShorts = 320;
                break;
            case 48000:
                sizeInShorts = 960;
                break;
            default:
                sizeInShorts = 320;
                break;
        }

        // 获得缓冲区字节大小
        bufferSizeInBytesIn = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        // 创建AudioRecord对象
        audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytesIn);
        int audioSessionId = audioRecord.getAudioSessionId();


        bufferSizeInBytesOut = AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);

        boolean isSupportAec = false;
        boolean isSupportNS = false;
        if (AcousticEchoCanceler.isAvailable()) {
            LogUtil.getInstance().logWithMethod(new Exception(), "系统支持回音消除", "x");
            AcousticEchoCanceler acousticEchoCanceler = AcousticEchoCanceler.create(audioSessionId);
            if (acousticEchoCanceler != null) {
                acousticEchoCanceler.setEnabled(true);
                isSupportAec = true;
                LogUtil.getInstance().logWithMethod(new Exception(), "系统回音消除已开启", "x");
            }
        } else {
            LogUtil.getInstance().logWithMethod(new Exception(), "系统不支持回音消除", "x");
        }

        if (NoiseSuppressor.isAvailable()) {
            NoiseSuppressor suppressor = NoiseSuppressor.create(audioSessionId);
            LogUtil.getInstance().logWithMethod(new Exception(), "系统支持降噪", "x");
            if (suppressor != null) {
                suppressor.setEnabled(true);
                isSupportNS = true;
                LogUtil.getInstance().logWithMethod(new Exception(), "系统降噪已开启", "x");
            }
        } else {
            LogUtil.getInstance().logWithMethod(new Exception(), "系统不支持降噪", "x");
        }


        if (isSupportAec || isSupportNS) {
            audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig,
                    audioFormat, bufferSizeInBytesOut, AudioTrack.MODE_STREAM, audioSessionId);
        } else {
            audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig,
                    audioFormat, bufferSizeInBytesOut, AudioTrack.MODE_STREAM);
        }
    }

    public void addJniListener(BaseJniListener jniListener) {
        if (jniListener instanceof AddrBookListener) {
            mAddrBookListeners.add((AddrBookListener) jniListener);
        }
        if (jniListener instanceof SelfInfoListener) {
            mSelfInfoListeners.add((SelfInfoListener) jniListener);
        }
        if (jniListener instanceof SipMsgListener) {
            mSipMsgListeners.add((SipMsgListener) jniListener);
        }
        if (jniListener instanceof TbcpMsgListener) {
            mTbcpMsgListeners.add((TbcpMsgListener) jniListener);
        }
        if (jniListener instanceof TextMsgListener) {
            mTextMsgListeners.add((TextMsgListener) jniListener);
        }
        if (jniListener instanceof UserStateListener) {
            mUserStateListeners.add((UserStateListener) jniListener);
        }
        if (jniListener instanceof SystemNotifyListener) {
            mSystemNotifyListeners.add((SystemNotifyListener) jniListener);
        }
        if (jniListener instanceof ResolutionListener) {
            mResolutionListeners.add((ResolutionListener) jniListener);
        }
        if (jniListener instanceof LocalMediaPowerListener) {
            mLocalPowerListeners.add((LocalMediaPowerListener) jniListener);
        }
        if (jniListener instanceof GroupEditResultListener) {
            mTempGroupListeners.add((GroupEditResultListener) jniListener);
        }
        if (jniListener instanceof SktInfoListener) {
            mSktInfoListeners.add((SktInfoListener) jniListener);
        }
        if (jniListener instanceof ResetPwdListener) {
            mResetPwdListeners.add((ResetPwdListener) jniListener);
        }
        if (jniListener instanceof GroupListenStateListener) {
            mGroupStateListeners.add((GroupListenStateListener) jniListener);
        }
        if (jniListener instanceof EsipHeadListener) {
            esipHeadListeners.add((EsipHeadListener) jniListener);
        }
        if (jniListener instanceof SynGroupListenStateListener) {
            mSynGroupStateListeners.add((SynGroupListenStateListener) jniListener);
        }
        if (jniListener instanceof CallTypeChangeListener) {
            mTypeChangeListeners.add((CallTypeChangeListener) jniListener);
        }
        if (jniListener instanceof AddressUrlListener) {
            addressUrlListeners.add((AddressUrlListener) jniListener);
        }
        if (jniListener instanceof UserTypeInfoListener) {
            mUserTypeInfoListeners.add((UserTypeInfoListener) jniListener);
        }
        if (jniListener instanceof UserChangeInMeetingListener) {
            mUserChangeInMeetingListenters.add((UserChangeInMeetingListener) jniListener);
        }
    }

    public void removeJniListener(BaseJniListener jniListener) {
        if (jniListener instanceof AddrBookListener) {
            mAddrBookListeners.remove((AddrBookListener) jniListener);
        }
        if (jniListener instanceof SelfInfoListener) {
            mSelfInfoListeners.remove((SelfInfoListener) jniListener);
        }
        if (jniListener instanceof SipMsgListener) {
            mSipMsgListeners.remove((SipMsgListener) jniListener);
        }
        if (jniListener instanceof TbcpMsgListener) {
            mTbcpMsgListeners.remove((TbcpMsgListener) jniListener);
        }
        if (jniListener instanceof TextMsgListener) {
            mTextMsgListeners.remove((TextMsgListener) jniListener);
        }
        if (jniListener instanceof UserStateListener) {
            mUserStateListeners.remove((UserStateListener) jniListener);
        }
        if (jniListener instanceof SystemNotifyListener) {
            mSystemNotifyListeners.remove((SystemNotifyListener) jniListener);
        }
        if (jniListener instanceof ResolutionListener) {
            mResolutionListeners.remove((ResolutionListener) jniListener);
        }
        if (jniListener instanceof LocalMediaPowerListener) {
            mLocalPowerListeners.remove((LocalMediaPowerListener) jniListener);
        }
        if (jniListener instanceof GroupEditResultListener) {
            mTempGroupListeners.remove((GroupEditResultListener) jniListener);
        }
        if (jniListener instanceof SktInfoListener) {
            mSktInfoListeners.remove((SktInfoListener) jniListener);
        }
        if (jniListener instanceof ResetPwdListener) {
            mResetPwdListeners.remove((ResetPwdListener) jniListener);
        }
        if (jniListener instanceof GroupListenStateListener) {
            mGroupStateListeners.remove((GroupListenStateListener) jniListener);
        }
        if (jniListener instanceof EsipHeadListener) {
            esipHeadListeners.remove((EsipHeadListener) jniListener);
        }
        if (jniListener instanceof SynGroupListenStateListener) {
            mSynGroupStateListeners.remove((SynGroupListenStateListener) jniListener);
        }
        if (jniListener instanceof CallTypeChangeListener) {
            mTypeChangeListeners.remove((CallTypeChangeListener) jniListener);
        }
        if (jniListener instanceof AddressUrlListener) {
            addressUrlListeners.remove((AddressUrlListener) jniListener);
        }
        if (jniListener instanceof UserTypeInfoListener) {
            mUserTypeInfoListeners.remove((UserTypeInfoListener) jniListener);
        }
        if (jniListener instanceof UserChangeInMeetingListener) {
            mUserChangeInMeetingListenters.remove((UserChangeInMeetingListener) jniListener);
        }
    }

    /**
     * 是否正在调整分辨率
     */
    private volatile boolean isResetSampleRate = false;

    private void recvGetSamplerate(int sampleRate) {
        if (sampleRateInHz == 0) {
            sampleRateInHz = sampleRate;
            createRecordAndTrack();
        } else {
            if (sampleRate != sampleRateInHz) {
                isResetSampleRate = true;
                //换了采样率  重新开启录音和播放
                sampleRateInHz = sampleRate;
                stopReadAudio();
                stopWriteAudio();
                createRecordAndTrack();
                isResetSampleRate = false;
            }
        }

        LogUtil.getInstance().logWithMethod(new Exception(), "recvGetSamplerate" + sampleRateInHz, "xun");
        Log.i(TAG, "recvGetSamplerate: " + sampleRateInHz);

    }

    /**
     * 接收同个用户二次登入消息
     */
    private synchronized void recvUserTwoLoading() {
        CallBackInfo callback = new CallBackInfo();
        callback.setTwoLoading(true);
        callback.setInfoType(CallBackInfoType.USERTWOLOADING);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();
    }

    /**
     * 接收sip消息
     *
     * @param callTel     主叫号码
     * @param callChannel 呼叫通道
     * @param callType    呼叫类型
     * @param msgType     消息类型
     * @param callDirc    消息來源:0 未知，例如register包，1：呼入 2：呼出 3：unknown，暂不处理
     * @param ret         0:成功 -1:失败
     */
    private synchronized void recvSipMsg(final String callTel,
                                         final String calledTel, final int callChannel, final int callType,
                                         final int msgType, final int callDirc, final int ret) {
        Log.i(TAG, "recvSipMsgffff" + PocSipMsgType.of(msgType));
        SipMsg sipMessage = new SipMsg(callTel, calledTel, callChannel,
                callType, msgType, callDirc, ret);
        CallBackInfo callback = new CallBackInfo();
        callback.setSipMsg(sipMessage);
        callback.setInfoType(CallBackInfoType.SIPMSG);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();

    }

    /**
     * 接收tbcp消息
     * handsup msgType ==21 举手
     *
     * @param msgType 消息类型
     * @param ret     0:成功 -1:失败
     */
    private void recvTbcpMsg(final int msgType, final String tel, final int ret) {
        TbcpMsg tbcpMsg = new TbcpMsg(msgType, tel, ret);
        CallBackInfo callback = new CallBackInfo();
        callback.setTbcpMsg(tbcpMsg);
        callback.setInfoType(CallBackInfoType.TBCPMSG);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();
    }

    private Map<String, String> msgMap = new HashMap<String, String>();

    /**
     * 接收短信信息
     *
     * @param text       短信内容
     * @param sipMsgType 短信流程类型
     * @param ret        0:成功 -1:失败
     */
    private void recvTextMsg(final String text, final String firstCall,
                             final String callTel, final String msgId, final int callType, final int sipMsgType,
                             final int ret) {
        LogUtil.getInstance().logWithMethod(new Exception(), "recvTextMsg ==text==" + text + "=firstCall==" + firstCall + "==callTel=" + callTel + "==msgId==" + msgId + "ret ==" + ret, "Zhaolg");
        if (msgMap.containsKey(msgId)) {//防止重复msg
            return;
        }
        TextMsg textMsg = new TextMsg(text, firstCall, callTel, msgId, callType,
                sipMsgType, ret);
        CallBackInfo callback = new CallBackInfo();
        callback.setTxtMsg(textMsg);
        callback.setInfoType(CallBackInfoType.TEXTMSG);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();
        if (msgMap.size() >= 100) {//100条清一下
            msgMap.clear();
        }
        if (msgId != null) {
            //返回发送成功时这个是null
            msgMap.put(msgId, msgId);
        }
    }


    /**
     * 开始下载通讯录文件的信号
     *
     * @param addlistUrl
     * @param result
     * @param reason
     */
    private void recvAddressListMsg(String addlistUrl, String result, String reason) {
        System.out.println("===========recvAddressListMsg=======addlistUrl======" + addlistUrl + "======result========" + result + "====reason===" + reason);

        AddressUrl url = new AddressUrl();
        url.setReason(reason);
        url.setResult(result);
        url.setUrl(addlistUrl);

        CallBackInfo callback = new CallBackInfo();
        callback.setAddressUrl(url);

        callback.setInfoType(CallBackInfoType.ADDRESSURL);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();

    }


    /**
     * 接收自身信息
     *
     * @param name     用户昵称
     * @param gruopTel 能呼叫的群组
     */
    private void recvSelfInfo(final String name, final String gruopTel) {
        SelfInfo selfInfo = new SelfInfo(name, gruopTel);
        CallBackInfo callback = new CallBackInfo();
        callback.setSelfInfo(selfInfo);
        callback.setInfoType(CallBackInfoType.SELFINFO);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();
    }

    /**
     * 接收通讯录
     *
     * @param tag    0:用户,1:群组 2：广播  3：组织架构
     * @param did    did
     * @param id     uid or gid
     * @param tel    号码
     * @param name   名字
     * @param member 群组中的成员
     * @param opt    0:del 1:add
     */
    private void recvAddressBook(final int tag, final int did, final int id,
                                 final String tel, final String name, final String member,
                                 final int opt, final int st, final int endFlag, final String creater) {// st 1 online 2 offline (if not user
        // 0 normalgroup 1dynamicgroup 2
        // tempgroup)

        AddressBook addressBook = new AddressBook(tag, did, id, tel, name,
                member, opt, st, endFlag, creater);

        CallBackInfo callback = new CallBackInfo();
        callback.setAddressbook(addressBook);
        callback.setInfoType(CallBackInfoType.ADDRESSBOOK);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();
    }

    /**
     * @param tag   用户 or 群组
     * @param id    uid or gid
     * @param state 0:out 1:in
     */
    private void recvUserSt(final int tag, final int id, final int state) {
        UserState userState = new UserState(tag, id, state);
        System.out.println("User state notify 0  id ==" + id + "state =="
                + state);
        CallBackInfo callback = new CallBackInfo();
        callback.setUserState(userState);
        callback.setInfoType(CallBackInfoType.USERSTATE);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();
    }

    /**
     * 接收对讲组的监听状态
     *
     * @param gid
     * @param state gid -2  state -2 表示同步结束刷新
     */
    private void recvGroupListenSt(final int gid, final int state) {

        System.out.println("========recvGroupListenSt===========gid====" + gid + "====state==" + state);
        SynGroupListenStateInfo groupListenState = new SynGroupListenStateInfo(gid, state);
        CallBackInfo callback = new CallBackInfo();
        callback.setSynGroupListenStateInfo(groupListenState);
        callback.setInfoType(CallBackInfoType.SYNGROUPLISTENSTATE);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();

    }

    /**
     * 接收对讲组的监听状态（ 新增组的时候）
     *
     * @param gid
     * @param endflag
     */
    private String members = "";

    private void recvGroupListenStWhenGetNewGroup(final String member, final int packageFlag, final int endflag) {

        System.out.println("========recvGroupListenStWhenGetNewGroup===========member====" + member + "=====packageFlag==" + packageFlag + "====endflag==" + endflag);
        //如果是空的，就不管
        if (member == null || "".equals(member.trim())) {
            return;
        }
        if (packageFlag == 0) {//开始拼接
            members = member + "/" + members;
        } else if (packageFlag == 1) {//拼接结束
            members = member + "/" + members;
            if (members.length() > 0) {
                members = members.substring(0, members.length() - 1);
                String[] gidArray = members.split("/");
                for (int i = 0; i < gidArray.length; i++) {
                    int gid = Integer.valueOf(gidArray[i]);
                    //给过来的都是被频闭的，用来刷新界面
                    recvGroupListenSt(gid, 1);
                }
                members = "";//清除
                //最后刷新一下
                recvGroupListenSt(-2, -2);

            }
        } else if (packageFlag == -1) {//小于80
            String[] gidArray = member.split("/");
            for (int i = 0; i < gidArray.length; i++) {
                int gid = Integer.valueOf(gidArray[i]);
                //给过来的都是被频闭的，用来改变数据
                recvGroupListenSt(gid, 1);
            }
            //最后刷新一下
            recvGroupListenSt(-2, -2);

        }


    }

    /**
     * 接收大组判别条件
     *
     * @param bigGroupNum
     */
    private void recvBigGroupSt(int bigGroupNum) {
        System.out.println("============bigGroupNum=============" + bigGroupNum);
        this.bigGroupNum = bigGroupNum;
    }

    /**
     * 接受esip组的字冠
     *
     * @param nums 123:124:125
     */
    private void recvEsipHeadNum(final String nums) {
        System.out.println("========recvEsipHeadNum============nums===" + nums);
        EsipHeadMsg esipHeadMsg = new EsipHeadMsg(nums);
        esipHeadList.addAll(esipHeadMsg.getHeadList());
        CallBackInfo callback = new CallBackInfo();
        callback.setEsipHeadMsg(esipHeadMsg);
        callback.setInfoType(CallBackInfoType.ESIPHEADNUM);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();

    }

    /**
     * @param num  版本号
     * @param url  下载地址
     * @param msg  描述信息
     * @param time 时间
     * @param mode 下载模式
     */
    private void recvVersionUpdate(final String num, final String url,
                                   final String msg, final String time, final String mode) {
        System.out.println("=========11111===recvVersionUpdate====num====" + num + "==url=" + url + "==msg==" + msg + "==time==" + time + "===mode===" + mode);
        VersionUpdateEvent versionUpdateEvent = new VersionUpdateEvent(msg,
                mode, url, num, time);
        CallBackInfo callback = new CallBackInfo();
        callback.setUpdateEvent(versionUpdateEvent);
        callback.setInfoType(CallBackInfoType.VERSIONUPDATE);
        synchronized (callBackInfos) {
            callBackInfos.add(callback);
        }
        WaitNotify.doNotify();

    }

    private void setResolution(int iHeight, int iWidth, int iFrameRate) {
        Resolution resolution = new Resolution(iHeight, iWidth, iFrameRate,
                "local");
        CallBackInfo callBack = new CallBackInfo();
        callBack.setResulution(resolution);
        callBack.setInfoType(CallBackInfoType.RESOLUTION);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        Log.i(TAG, iWidth + "setResolution: " + iHeight);
        WaitNotify.doNotify();

    }

    /**
     * @param msg 发送短信权限
     * @param pic 发送图片权限
     * @param vid 发送视频权限
     * @param mon 视频监控权限
     * @param hbt heartbeatTime 心跳
     */
    private void notifyMediaPower(int msg, int pic, int vid, int mon, int hbt) {
        LocalPower local = new LocalPower(msg, pic, vid, 1, 1, 1, mon, hbt, 1, 30);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setLocal(local);
        callBack.setInfoType(CallBackInfoType.LOCALMEDIAPOWER);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();
    }

    /**
     * @param type
     * @param groupTel
     * @param ret      0 成功 非0 操作失败 通知客户端操作临时组成功
     */
    private void notifyTemGroupTel(int type, String groupTel, int ret) {
        System.out.println("notifyTemGroupTel type ==" + type + "ret ==" + ret);
        GroupEditResult tempGroup = new GroupEditResult(type, groupTel, ret);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setTempGroup(tempGroup);
        callBack.setInfoType(CallBackInfoType.TEMPGROUPSUC);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();

    }

    /**
     * 通知重邀请
     *
     * @param chn        通道号
     * @param changeMode 改变的模式
     */
    private void notifyCallTypeChange(int chn, int changeMode) {

        CallTypeChange callTypeChange = new CallTypeChange(chn, changeMode);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setCallTypeChange(callTypeChange);
        callBack.setInfoType(CallBackInfoType.CALLTYPECHANGE);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();

    }

    /**
     * 通知随路信令
     *
     * @param chn     通道号
     * @param content 信令内容
     */
    private void notifySktInfo(int chn, String content) {

        SktInfo sktInfo = new SktInfo(chn, content);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setSktInfo(sktInfo);
        callBack.setInfoType(CallBackInfoType.SKTINFO);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();

    }

    /**
     * 用于判别当前被叫或者主叫的身份 是否为调度用户
     *
     * @param chn
     * @param type 0 表示普通用户 1 表示调度用户
     */
    private void notifyCurrentCallUserType(int chn, int type) {
        System.out.println("=============notifyCurrentCallUserType=============type====" + type);

        UserTypeInfo typeInfo = new UserTypeInfo(chn, type);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setUserTypeInfo(typeInfo);
        callBack.setInfoType(CallBackInfoType.USERTYPEINFO);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();

    }

    /**
     * 重置密码的结果
     *
     * @param userTel 用户号
     * @param ret     0 成功 非0 操作失败
     */
    private void notifyReSetPwdSucceed(String userTel, int ret) {

        System.out.println("notifyReSetPwdSucceed userTel ==" + userTel + "ret ==" + ret);
        PwdInfo pwdInfo = new PwdInfo(userTel, ret);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setPwdInfo(pwdInfo);
        callBack.setInfoType(CallBackInfoType.RESETPWDSUC);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();
    }

    /**
     * 通知改变组呼监听状态的结果
     *
     * @param groupTel     操作后的组号
     * @param ret          0 成功 非0 操作失败
     * @param currentState 0 表示普通 1 表示屏蔽
     */
    public void notifyChangeGroupListenStateSuc(String groupTel, int ret, int currentState) {

        System.out.println("notifyChangeGroupListenStateSuc groupTel ==" + groupTel + "ret ==" + ret + "==currentState===" + currentState);
        GroupListenStateInfo groupListenStateInfo = new GroupListenStateInfo(groupTel, ret, currentState);
        CallBackInfo callBack = new CallBackInfo();
        callBack.setGroupListenStateInfo(groupListenStateInfo);
        callBack.setInfoType(CallBackInfoType.GROUPLISTENSTATE);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();
    }

    /**
     * 用户加入会议的通知方法
     *
     * @param caller        发起人的电话号码
     * @param onlineUidList uid列表 / 分隔开
     * @param flag          true 有人加入会议 false 只有下线的 （有人加入会议时 需要视频出一个KeyFrame）
     */
    private void notifyUsersAddIntoMeeting(final String caller, final String onlineUidList, String flag) {
        Log.i(TAG, flag + " caller: " + caller + "onlineList" + onlineUidList);
        CallBackInfo callBack = new CallBackInfo();
        UserChangeInMeeting userChangeInMeeting = new UserChangeInMeeting(0, caller, onlineUidList, flag);
        callBack.setUserChangeInMeeting(userChangeInMeeting);
        callBack.setInfoType(CallBackInfoType.MEETING_USERS_ON_CALL_CHANGE);
        synchronized (callBackInfos) {
            callBackInfos.add(callBack);
        }
        WaitNotify.doNotify();
    }

    /**
     * 是否允许发送声音和视频
     * 对讲 中 视频和声音发送 上层的控制 库没有控制是否送
     */
    private boolean enableRead = false;
    /**
     * 是否允许接收声音和视频
     */
    private boolean enableWrite = false;

    /**
     * 是否允许发送接收声音和视频
     * 上层也控制一下逻辑
     *
     * @param enableRead  true允许读取声音和视频发送出去
     * @param enableWrite true允许接收库里传来的声音和视频
     */
    public void enableReadWriteAudioAndVideo(boolean enableRead, boolean enableWrite) {
        this.enableRead = enableRead;
        this.enableWrite = enableWrite;
        Log.i(TAG, "enableReadWriteAudioAndVideo: r=" + enableRead + "_w=" + enableWrite);
    }

    /**
     * 读取声卡
     *
     * @return 声音的长度
     */
    private int readAudio(final short[] buff) {
        if (!enableRead) {
            Log.i(TAG, "readreadAudio: no");
            return 0;
        }

        if (isResetSampleRate) {
            //正在调整
            return 0;
        }
        int len = 0;
        if (!isAudioRecordStart) {
            if (null == audioRecord) {

                bufferSizeInBytesIn = AudioRecord.getMinBufferSize(sampleRateInHz,
                        channelConfig, audioFormat);
                audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                        channelConfig, audioFormat, bufferSizeInBytesIn);
            }

            if (AudioRecord.STATE_UNINITIALIZED == audioRecord
                    .getState()) {
                Log.e(TAG, "readAudio: AudioRecord.STATE_UNINITIALIZED");
                return len;
            }

            audioRecord.startRecording();

            isAudioRecordStart = true;

        }
        if (AudioRecord.STATE_UNINITIALIZED == audioRecord
                .getState()) {
            return len;
        }
        try {
            if (clearAudioRecordFlag) {
                //需要清理旧 的声音缓存 read 一个缓存的数据
                Log.i(TAG, "readAudio: 抢全了 clearAudioRecordFlag 000");
                audioRecord.stop();
                audioRecord.startRecording();
                Log.i(TAG, "readAudio: 抢全了 clearAudioRecordFlag 111");
                clearAudioRecordFlag = false;
            }
            len = audioRecord.read(buff, 0, sizeInShorts);

//            byte[] data = shortToByte(buff,len);
//            getAudioFile(data, data.length);

//            VolumeControl.agentVolume(buff, len, 2F);
//            Log.i(TAG, "readAudio: ");

        } catch (Exception e) {
            e.printStackTrace();
        }

        //防止发生stopAudio后还可能调用read
        return len < 0 ? 0 : len;
    }

    private void getAudioFile(byte[] data, int len) {
        try {//华为手机播放速度过快的解决方法
            Thread.sleep(20);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/sendAudio.pcm");
        try {

            if (null != file) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (file.length() >= 1024 * 1024 * 1024) {//如果文件大于1G，删
                    file.delete();
                }

                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data, 0, len);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void getAudioFileRec(byte[] data, int len) {

        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/recAudio.arm");
        try {

            if (null != file) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (file.length() >= 1024 * 1024 * 1024) {//如果文件大于1G，删
                    file.delete();
                }

                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data, 0, len);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * short 转 byte[]
     */
    private byte[] shortToByte(short[] buff, int len) {

        byte[] data = new byte[len * 2];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int temp = buff[i];
            byte[] b = new byte[2];
            for (int k = 0; k < b.length; k++) {
                b[k] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
                temp = temp >> 8; // 向右移8位
            }
            System.arraycopy(b, 0, data, j, b.length);
            j = j + 2;

        }
        return data;
    }

    /**
     * 写入声卡
     *
     * @param len 声音长度
     */
    private void writeAudio(short[] buff, int len) {
        if (!enableWrite) {
            Log.i(TAG, "writeAudio: " + enableWrite);
            return;
        }

        if (isResetSampleRate) {
            //正在调整
            return;
        }

        if (!isAudioTrackStart) {
            if (null == audioTrack) {
//                    || AudioTrack.STATE_UNINITIALIZED == audioTrack.getState()) {
                bufferSizeInBytesOut = AudioTrack.getMinBufferSize(sampleRateInHz,
                        channelConfig, audioFormat);
                audioTrack = new AudioTrack(streamType, sampleRateInHz,
                        channelConfig, audioFormat, bufferSizeInBytesOut,
                        AudioTrack.MODE_STREAM);
            }

            if (AudioTrack.STATE_UNINITIALIZED == audioTrack.getState()) {
                Log.e(TAG, "writeAudio: AudioTrack.STATE_UNINITIALIZED");
                return;
            }

            audioTrack.play();
            isAudioTrackStart = true;
        }
//        Log.i("zhouhl", len + "");
        try {
            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING
                    && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {

                audioTrack.write(buff, 0, len);

//                    byte[] data = shortToByte(buff,len);
//                    getAudioFileRec(data, data.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止读取声卡
     */
    private void stopReadAudio() {
        if (isAudioRecordStart) {
            if (null != audioRecord) {
                try {
                    audioRecord.stop();
                    audioRecord.release();

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            audioRecord = null;
            isAudioRecordStart = false;
        }
    }

    /**
     * 停止写入声卡
     */
    private void stopWriteAudio() {
        if (isAudioTrackStart) {
            audioTrack.flush();
            if (null != audioTrack
                    && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                    audioTrack.stop();
                }
                audioTrack.release();
                audioTrack = null;
            }

            isAudioTrackStart = false;
        }
    }

    /**
     * 为true就需要清一下read的缓存 在需要读取最新声音的前一次清掉旧的缓存
     */
    private boolean clearAudioRecordFlag = false;

    /**
     * 强权成功在读取声音时候 需要清理掉缓存 以读取最新的声音 避免出现以前的声音
     */
    public void resetAudioRecord() {
        clearAudioRecordFlag = true;
    }

    /**
     * 停止读写声卡
     */
    private void stopAudio() {
        System.out.println("===========stopAudio=============");
        /**
         * 保证audio的write和read执行结束。循环不会再调write和read。这时在关闭
         * 解决方法：poc库调用这个方法放在声音的send和receive的线程循环后（未改）
         */
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWriteAudio();
        stopReadAudio();
    }

    /**
     * @param cameraTowards 0：【视频方向】 1：【数据类型0=默认类型 1=H264 2=NV21】
     */
    private int readVideo(byte[] data, byte[] timestamps, byte[] cameraTowards) {
        if (!enableRead) {
            Log.i(TAG, "readreadVideo: no");
            return 0;
        }

        int len = 0;
        VideoData vData;
        byte[] dataTmp;

        if (videoSendDataList.size() > 0) {
            synchronized (videoSendDataList) {
                vData = videoSendDataList.remove(0);
            }

            dataTmp = vData.getData();

            if (dataTmp == null) {
                return 0;
            }
            System.arraycopy(dataTmp, 0, data, 0, dataTmp.length);

//		    getYUVFile(data,data.length);
//			getH264File(dataTmp,dataTmp.length);

            // vData.getTimestamps());
            String timestamp = String.format("%010d", vData.getTimestamps());

            byte[] tmp = timestamp.getBytes();

            System.arraycopy(tmp, 0, timestamps, 0, tmp.length);

            cameraTowards[0] = (byte) vData.getCameraId();
            cameraTowards[1] = 0;

            len = dataTmp.length;
            Log.i(TAG, "readVideo: " + len);
        }

        return len;
    }

    private void getH264File(byte[] data, int len) {

        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/AvcEncoderRecive.264");
        try {

            if (null != file) {
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data, 0, len);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void getYUVFile(byte[] data, int len, int cameraTowards) {

        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/1/video_" +
                cameraTowards +
                "_.yuv");
        try {

            if (null != file) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (file.length() >= 1024 * 1024 * 1024) {//如果文件大于1G，删
                    file.delete();
                }

                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(data, 0, len);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // 是否第一次进入
    private boolean isFirstTime = true;
    private long firstStampTime = 0;
    private long lastStampTime = 0;

    public void resetRemoteVideoData() {
        Log.i(TAG1, "resetRemoteVideoData: ");
        MultiVideoShowManager.clearAllVideo();
    }

    public void resetRemoteVideoData(int id) {
        Log.i(TAG1, "resetRemoteVideoData: ");
        MultiVideoShowManager.get(id).clearVideoData();
    }

    private static final String TAG = "JniUtils";
    private static final String TAG1 = "cacheLastFrame";

    public byte[] allocMemory(int id, int len) {
        VideoShowManager videoShowManager = MultiVideoShowManager.get(id);
        if (videoShowManager == null) {
            return null;
        }
        return videoShowManager.getSpaceFromMemory(len);
    }

    /**
     * 收到视频数据
     * 视频通话时回调的这个方法
     * 如果是软解就需要丢掉一帧（流的方向和数据有一帧是不对的）
     * {@link RemoteVideoViewGL}
     * 这里方向变化后return了 就丢掉了一帧错误方向的画面
     *
     * @param data          视频数据
     * @param cameraTowards 视频流的方向
     */
    private void writeVideo(byte[] data, int len, int height, int width,
                            int stampTime, int cameraTowards, int ssrc) {

        if (!enableWrite) {
            Log.i(TAG, "writeVideo: " + enableWrite);
            return;
        }

//        Log.e("d", "writeVideo  len=" + len + "=height=" + height + "=width==" + width + "=stampTime=" +
//                stampTime + "=cameraTowards=" + cameraTowards);
        if (data == null || width == 0 || height == 0) {
            return;
        }

        if (isFirstTime) {
            firstStampTime = stampTime;
            lastStampTime = stampTime;
            isFirstTime = false;
        }

        int id;
        if (ssrc == 0) {
            id = MultiVideoShowManager.DEFAULT_ID;
        } else {
            id = MultiVideoShowManager.ssrcToId(ssrc);
            if (!MultiVideoShowManager.checkIdValid(id)) {
                return;
            }
        }

        Log.i(TAG, "writeVideo: " + len + "_" + stampTime);

        if (null != RemoteViewManager.adaptiveGet(id) && null != data && len > 0) {
            if (AvcDecoderAsync.deCodecMode == 1) {
                AvcDecoderAsync decoder = decoder = getDecoder(id);
                if (decoder != null) {
                    decoder.asyncInput(data, len, width, height, stampTime, cameraTowards);
                }
//                Log.i(TAG, "writeVideo: "+id);
            } else {
                // 申请空间
                byte[] outputData = MultiVideoShowManager.get(id).getSpaceFromMemory(len);
                if (null == outputData) {
                    System.out.println("======outputData == null========");
                    return;
                }
                System.arraycopy(data, 0, outputData, 0, len);
                VideoRecvData recvData = new VideoRecvData();
                recvData.setData(outputData);
                recvData.setWidth(width);
                recvData.setHeight(height);
                long ptsUsec = computePresentationTime(stampTime);
                recvData.setCpTime(ptsUsec);
                recvData.setDirection(cameraTowards);
                recvData.setDataLen(len);
                try {
                    MultiVideoShowManager.get(id).putVideoData(recvData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long stampTime) {

        if (stampTime < lastStampTime) {
            firstStampTime = stampTime;
        }
        lastStampTime = stampTime;

        return (long) (132 + (stampTime - firstStampTime) * 1000000 / 90000);
    }

    private final List<AvcDecoderAsync> decoderList = new ArrayList<>();

    private AvcDecoderAsync getDecoder(int id) {
        synchronized (decoderList) {
            if (decoderList.size() > id) {
                return decoderList.get(id);
            }
        }
        return null;
    }

    private void startMultiAvcDecoders(int count) {
        synchronized (decoderList) {
            for (int i = 0; i < count; i++) {
                AvcDecoderAsync avcDecoderAsync = startAvcDecoder(i);
                decoderList.add(avcDecoderAsync);
            }
        }
    }

    private void stopMultiAvcDecoders() {
        synchronized (decoderList) {
            for (AvcDecoderAsync avcDecoderAsync : decoderList) {
                stopAvcDecoder(avcDecoderAsync);
            }
            decoderList.clear();
        }
    }

    private void startOneAvcDecoder() {
        synchronized (decoderList) {
            decoderList.clear();
            AvcDecoderAsync decoderAsync = startAvcDecoder(MultiVideoShowManager.DEFAULT_ID);
            decoderList.add(decoderAsync);
        }
    }

    private void stopOneAvcDecoder() {
        synchronized (decoderList) {
            for (AvcDecoderAsync avcDecoderAsync : decoderList) {
                stopAvcDecoder(avcDecoderAsync);
            }
            decoderList.clear();
        }
    }

    private AvcDecoderAsync startAvcDecoder(final int id) {
        AvcDecoderAsync decoder = AvcDecoderAsync.createDecoder(id, new AvcDecoderAsync.Callback() {
            @Override
            public void callback(VideoRecvData recvData) {
                try {
                    MultiVideoShowManager.get(id).putVideoData(recvData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return decoder;
    }

    private void stopAvcDecoder(AvcDecoderAsync decoder) {
        destroyAvcDecoder(decoder);
    }

    /**
     * 显示视频流的准备函数
     */
    public void prepareStartVideoShow(boolean isMultiStreams) {

        if (AvcDecoderAsync.deCodecMode == 1) {
            // 实例化解码器

            if (isMultiStreams) {
                startMultiAvcDecoders(MultiVideoShowManager.getMaxCount());
            } else {
                startOneAvcDecoder();
            }
        }

        if (isMultiStreams) {
            MultiVideoShowManager.startAll();
        } else {
            MultiVideoShowManager.start();
        }
    }

    /**
     * 结束视频流显示的函数
     */
    public void prepareStopVideoShow() {
        if (AvcDecoderAsync.deCodecMode == 1) {
            stopMultiAvcDecoders();
        }

        MultiVideoShowManager.stopAll();

        isFirstTime = true;
        firstStampTime = 0;
        lastStampTime = 0;
    }

    private void destroyAvcDecoder(AvcDecoderAsync decoder) {
        if (decoder != null) {
            decoder.inputData = null;
            // 释放编码器对象
            decoder.close();
        }
    }

    private List<VideoData> videoSendDataList = new ArrayList<VideoData>();

    public void setVideoData(byte[] data, int timestamps, int cameraId) {
        synchronized (videoSendDataList) {
            try {
                if (videoSendDataList.size() > 4) {
                    videoSendDataList.remove(0);
                }
//                Log.e("fffffff", cameraId + " setVideoData: " + videoSendDataList.size());
                VideoData dataV = new VideoData();
                dataV.setData(data);
                dataV.setTimestamps(timestamps);
                dataV.setCameraId(cameraId);
                videoSendDataList.add(dataV);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止视频
     * call from jni
     */
    private void stopVideo() {
        // 清空视频信息
        videoSendDataList.clear();
    }

    /**
     * call from jni
     */
    private void clearVideoBuf() {
        videoSendDataList.clear();
    }

    /**
     * 实时上报经纬度接口
     * call from jni
     */
    private void getOrientionInfo(final byte[] longitude, final byte[] latitude) {
        System.out.println(" getOrientionInfo");

//		byte[] longTmp;
//		byte[] latiTmp;

//		if (null == location) {
//			longTmp = "120".getBytes();
//			latiTmp = "30".getBytes();
//		} else {
//			longTmp = (location.getLongitude() + "").getBytes();
//			latiTmp = (location.getLatitude() + "").getBytes();
//		}

//		System.arraycopy(longTmp, 0, longitude, 0, longTmp.length);
//
//		System.arraycopy(latiTmp, 0, latitude, 0, latiTmp.length);

    }

    /**
     * 收发sip的线程
     */
    private Future pocMainSipTask;

    public void startPocMainSipThread() {

        stopPocMainSipThread();

        pocMainSipTask = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: 000");
                //jni处理协议
                JniUtils.getInstance().PocMainForRecvSipAndTbcp();
                Log.i(TAG, "run: 111");
            }
        });
    }

    /**
     * 关闭线程
     */
    public void stopPocMainSipThread() {
        if (pocMainSipTask != null) {
            if (!pocMainSipTask.isCancelled()) {
                pocMainSipTask.cancel(true);
            }
            pocMainSipTask = null;
        }
    }

    class DealCallBackInfo implements Runnable {

        @Override
        public void run() {
            if (callBackThread != null) {
                callBackThread.setPriority(6);
            }
            Log.e(TAG, "run: DealCallBackInfo start");
            LogUtil.getInstance().logWithMethod(new Exception(), "DealCallBackInfo start", "x");
            while (isProcessThreadOn) {
                WaitNotify.doWait();

                CallBackInfo info;
                while (true) {
                    synchronized (callBackInfos) {
                        if (callBackInfos.size() > 0) {
                            info = callBackInfos.remove(0);
                        } else {
                            break;
                        }
                    }
                    CallBackInfoType infoType = info.getInfoType();
                    switch (infoType) {
                        case ADDRESSBOOK:
                            AddressBook addressBook = info.getAddressbook();
                            for (AddrBookListener addrBookListener : mAddrBookListeners) {
                                addrBookListener.onRecvAddrBook(addressBook);
                            }
                            break;
                        case SELFINFO:
                            SelfInfo selfInfo = info.getSelfInfo();
                            for (SelfInfoListener selfInfoListener : mSelfInfoListeners) {
                                if (null != selfInfoListener) {
                                    selfInfoListener.onRecvSelfInfo(selfInfo);
                                }
                            }
                            break;
                        case SIPMSG:
                            SipMsg sipMessage = info.getSipMsg();
                            for (SipMsgListener sipMsgListener : mSipMsgListeners) {
                                if (sipMsgListener != null) {
                                    sipMsgListener.onRecvSipMsg(sipMessage);
                                }
                            }
                            break;
                        case TBCPMSG:
                            TbcpMsg tbcpMsg = info.getTbcpMsg();
                            for (TbcpMsgListener tbcpMsgListener : mTbcpMsgListeners) {
                                if (tbcpMsgListener != null) {
                                    tbcpMsgListener.onRecvTbcpMsg(tbcpMsg);
                                }
                            }
                            break;
                        case USERSTATE:
                            UserState userState = info.getUserState();

                            for (UserStateListener userStateListener : mUserStateListeners) {
                                userStateListener.onRecvUserState(userState);
                            }
                            break;
                        case TEXTMSG:
                            TextMsg textMsg = info.getTxtMsg();
                            for (TextMsgListener textMsgListener : mTextMsgListeners) {
                                if (textMsgListener != null) {
                                    textMsgListener.onRecvTextMsg(textMsg);
                                }
                            }
                            break;
                        case RESOLUTION:
                            Resolution resolution = info.getResulution();
                            for (ResolutionListener res : mResolutionListeners) {
                                if (null != res) {
                                    res.onRecvResolution(resolution);
                                }
                            }
                            break;
                        case USERTWOLOADING:
                            for (SystemNotifyListener l : mSystemNotifyListeners) {
                                l.notifyUserTwoLoadingEvent();
                            }
                            break;
                        case VERSIONUPDATE:
                            VersionUpdateEvent updateEvent = info.getUpdateEvent();
                            for (SystemNotifyListener l : mSystemNotifyListeners) {
                                l.notifyUpdateEvent(updateEvent);
                            }
                            break;
                        case LOCALMEDIAPOWER:
                            LocalPower local = info.getLocal();
                            for (LocalMediaPowerListener l : mLocalPowerListeners) {
                                l.onRecvLocalMediaPower(local);
                            }
                            break;
                        case TEMPGROUPSUC:
                            GroupEditResult tempGroup = info.getTempGroup();
                            for (GroupEditResultListener l : mTempGroupListeners) {
                                l.onResultCallback(tempGroup);
                            }
                            break;
                        case SKTINFO:
                            SktInfo sktInfo = info.getSktInfo();
                            for (SktInfoListener l : mSktInfoListeners) {
                                l.onNotifySktInfo(sktInfo);
                            }
                            break;
                        case USERTYPEINFO:
                            UserTypeInfo typeInfo = info.getUserTypeInfo();
                            for (UserTypeInfoListener l : mUserTypeInfoListeners) {
                                l.onNotifyUserTypeInfo(typeInfo);
                            }
                            break;
                        case CALLTYPECHANGE:
                            CallTypeChange callTypeChange = info
                                    .getCallTypeChange();
                            for (CallTypeChangeListener l : mTypeChangeListeners) {
                                l.notifyCallTypeChange(callTypeChange);
                            }
                            break;

                        case RESETPWDSUC:
                            PwdInfo pwdInfo = info.getPwdInfo();
                            for (ResetPwdListener l : mResetPwdListeners) {
                                l.onRecvRestPwd(pwdInfo);
                            }
                            break;

                        case GROUPLISTENSTATE:
                            GroupListenStateInfo groupListenStateInfo = info.getGroupListenStateInfo();
                            for (GroupListenStateListener l : mGroupStateListeners) {
                                l.onRecvGroupListenState(groupListenStateInfo);
                            }
                            break;
                        case ESIPHEADNUM:

                            EsipHeadMsg esipHeadMsg = info.getEsipHeadMsg();
                            for (EsipHeadListener l : esipHeadListeners) {
                                l.onNotifyEsipHeadNum(esipHeadMsg);
                            }
                            break;
                        case SYNGROUPLISTENSTATE:
                            SynGroupListenStateInfo groupListenState = info.getSynGroupListenStateInfo();

                            for (SynGroupListenStateListener l : mSynGroupStateListeners) {
                                l.onRecvSynGroupListenState(groupListenState);
                            }
                            break;
                        case ADDRESSURL:
                            AddressUrl addressUrl = info.getAddressUrl();
                            for (AddressUrlListener l : addressUrlListeners) {
                                l.onRecAddressUrl(addressUrl);
                            }
                            break;
                        case MEETING_USERS_ON_CALL_CHANGE:
                            UserChangeInMeeting userChangeInMeeting = info.getUserChangeInMeeting();
                            for (UserChangeInMeetingListener l : mUserChangeInMeetingListenters) {
                                l.onRecUserChangedInMeeting(userChangeInMeeting);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            Log.e(TAG, "run: DealCallBackInfo end " + isProcessThreadOn);
            LogUtil.getInstance().logWithMethod(new Exception(), "DealCallBackInfo end", "x");
        }
    }


    private volatile boolean isProcessThreadOn = false;

    public void startProcessCallBack() {
        if (isProcessThreadOn) {
            stopProcessCallback();
        }

        isProcessThreadOn = true;

        if (null == callBackThread) {
            callBackThread = new Thread(new DealCallBackInfo());
            callBackThread.start();
            LogUtil.getInstance().logWithMethod(new Exception(), "Login222", "Zhaolg");

        } else if (!callBackThread.isAlive()) {
            callBackThread = new Thread(new DealCallBackInfo());
            callBackThread.start();
            LogUtil.getInstance().logWithMethod(new Exception(), "Login33", "Zhaolg");
        }
    }

    public void stopProcessCallback() {
        isProcessThreadOn = false;
        if (callBackThread != null) {
            callBackThread.interrupt();
            callBackThread = null;
        }
        callBackInfos.clear();
    }

    /**
     * 初始化
     *
     * @param ip            本地ip
     * @param tel           用户号码
     * @param name          用户名
     * @param pwd           密码
     * @param serverIp      服务端ip
     * @param version       版本号
     * @param dvid          设备唯一id
     * @param serverSipPort 服务端端口
     * @return 0:成功
     */
    public native int PocClientInitConfig(String ip, String tel, String name,
                                          String pwd, String serverIp, String version, String dvid,
                                          int serverSipPort, String phoneTel);

    /**
     * 初始化定位信息上传的socket
     *
     * @param ip
     * @param port
     * @return
     */
    public native int PocMapSocketInit(String ip, int port);

    /**
     * @param iExpireFlag 0:注销, 其他:注册周期
     * @param extra
     * @return 0:成功
     */
    public native int PocRegister(int iExpireFlag, String longitude,
                                  String latitude, String extra);

    /**
     * @param calledTel   被叫号码
     * @param callType    呼叫类型，对应PocCallType
     * @param callContent 随路信令
     * @param isBigGroup  是否是大组呼叫 0 不是  1是
     * @return 通道号
     */
    public native int PocMakeCallForSignal(String calledTel, int callType,
                                           String callContent, int isBigGroup);

    /**
     * @param calledTel 被叫号码
     * @param callType  呼叫类型，对应PocCallType
     * @return 通道号
     */
    public native int PocMakeCall(String calledTel, int callType, int isBigGroup);

    /**
     * @param calledTel 被叫号码
     * @param msg       文本消息
     * @param msgLen    消息长度
     * @param callType  呼叫类型，对应PocCallType
     * @return 通道号
     */
    public native int PocSendMsg(String calledTel, String msg, int msgLen,
                                 int callType);


    public native int PocSendLocalAddrListUrl(String addlistUrl);

    /**
     * 重邀请
     *
     * @param chnIndex       通道号
     * @param callChangeMode 呼叫改变类型
     * @param callType       该邀请的呼叫类型
     */
    public native int PocsendReinvite(int chnIndex, int callChangeMode,
                                      int callType);

    /**
     * 录音
     *
     * @param chnIndex     通道号
     * @param iRecordFlag  是否开启录音
     * @param chRecordPath 文件路径
     */
    public native int PocSetRecordFlag(int chnIndex, int iRecordFlag,
                                       String chRecordPath);

    /**
     * 语音抢权
     *
     * @param chnIndex 通道id
     * @return 0:成功
     */
    public native int PocTbcpRequest(int chnIndex);

    /**
     * 语音抢权释放
     *
     * @param chnIndex 通道id
     * @return 0:成功
     */
    public native int PocTbcpRelease(int chnIndex);

    /**
     * 用户摘机
     *
     * @param callType 呼叫类型
     * @param chnIndex 呼叫通道
     * @return
     */
    public native int PocPickUp(int callType, int chnIndex);

    /**
     * 视频呼叫接收视频buff
     */
    public native int PocSetRecvVideoBuf(ByteBuffer buf, int bufLen);

    /**
     * 用户主动挂机
     *
     * @param chnIndex 通道id
     * @return 0:成功
     */
    public native int PocHangUp(int chnIndex);

    /**
     * 收协议包进程需要的入口函数
     */
    public native void PocMainForRecvSipAndTbcp();

    /**
     * 发送同步信息
     *
     * @param content
     * @return 0:成功
     */
    public native int PocSendSubcribe(String content);

    /**
     * 设置视频信息
     *
     * @param videoId   视频ID
     * @param videoMode 视频模式，默认1
     * @param videoBr   随意写一个1
     * @param height    视频高度
     * @param width     视频宽度
     * @param framerate 帧率
     * @param bitrate   比特率
     */
    public native void PocSetVideoInfo(String videoId, int videoMode,
                                       int videoBr, int height, int width, int framerate, int bitrate,
                                       int complexityMode, int intraframetime, String allSize,
                                       String allFrameRate, int phoneType);

    /**
     * 退出时释放所有资源
     *
     * @return
     */
    public native int PocFreeClient();

    /**
     * 设置so库的日志开关
     *
     * @param flag    0:关闭 1:打开
     * @param logPath 日志路径
     * @return
     */
    public native int PocSetWriteLogFlag(int flag, String logPath);

    /**
     * 设置声音参数的方法
     */
    public native int PocSetSendFrame(int sampleperchn, int sampleratehz,
                                      int chnnum, int volumeNum);

    /**
     * 临时组统一接口
     */
    public native int PocTemporaryGroup(int eType, String groupTel,
                                        String userTels, int GroupMode);

    /**
     * 设置音频参数的方法
     */
    public native int PocSetRecvFrame(int sampleperchn, int sampleratehz,
                                      int chnnum);

    public native int PocSetStreamArg(int delayms, int analoglevel);

    public native int PocSetAgcArg(int leveldb, int gaindb);

    public native int PocInitAecm(int aectype, int enable, int mode);

    public native int PocInitNs(int enable, int mode);

    public native int PocInitAgc(int enable, int mode);

    // public native int OnLoad();

    /**
     * 设置视频参数的方法
     */

    public native int PocSetQosArg(int maxbitrate, int sendcount, int looptime);

    public native int PocSetFecArg(int rate, int useuepprot, int maxframes,
                                   int masktype);

    public native int PocSetVideoEnc(int multiplethreadid, int enableframeskip,
                                     int slicemode, int mtu);

    /**
     * 设置sip通讯协议 基于udp 或者 tcp
     *
     * @param sipMode 0 is tcp 1 is udp
     */
    public native int PocSetSipDealMode(int sipMode);

    /**
     * 设置组监听状态
     *
     * @param groupTel
     * @param userTel
     * @param groupState 0 表示普通  1 表示屏蔽
     * @return
     */
    public native int PocSwitchGroupListenState(String groupTel, String userTel, int groupState);

    /**
     * 重置密码
     *
     * @param newPwd
     * @param oldPwd
     * @param userTel
     * @return
     */
    public native int PocReSetPwd(String newPwd, String oldPwd, String userTel);

    /**
     * 发送自身的定位信息
     *
     * @param data 里面是json格式的数据
     * @return
     */

    public native int PocSendSelfLocationData(String data);

    /**
     * 获得加密钥匙
     */
    public native String PocGetKey();

    /**
     * 设置通话模式
     * 0 传统模式 所有媒体数据由服务段转发
     * 1 点对点的通话 由客户端直接对发 不经由服务端
     *
     * @param callMode 0 1
     */
    public native void PocSetCallMode(int callMode);

    /**
     * 发到服务端 强制某人发言或者停止发言
     *
     * @param type 0发言 2停止发言
     * @param tel  某人的号码
     */
    public native int PocTbcpInfo(int chnIndex, int type, String tel);

    public native int YUVscale(byte[] inData, int inW, int inH, byte[] outData, int outW, int outH);

    public native void YuvConcatLR(byte[] yuvL, byte[] yuvR, int w, int h, byte[] data);

    /**
     * 去掉各种监听
     * 发送服务端登出
     * 停止掉sip线程
     * 清理so库的资源
     * 停止so回调的数据的处理线程
     */
    public void destroy() {
        //清除回调
        removeAllListener();
        PocRegister(0, null, null, "");
        stopPocMainSipThread();
        PocFreeClient();
        stopProcessCallback();
    }

    private void removeAllListener() {
        mSipMsgListeners.clear();
        mTbcpMsgListeners.clear();
        mTextMsgListeners.clear();
        mSelfInfoListeners.clear();
        mAddrBookListeners.clear();
        mUserStateListeners.clear();
        mResolutionListeners.clear();
        mSystemNotifyListeners.clear();
        mLocalPowerListeners.clear();
        mTempGroupListeners.clear();
        mSktInfoListeners.clear();
        mResetPwdListeners.clear();
        mGroupStateListeners.clear();
        mSynGroupStateListeners.clear();
        mTypeChangeListeners.clear();
        esipHeadListeners.clear();
        addressUrlListeners.clear();
        mUserTypeInfoListeners.clear();
        mUserChangeInMeetingListenters.clear();

    }
}
