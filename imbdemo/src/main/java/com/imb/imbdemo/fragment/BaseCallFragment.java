package com.imb.imbdemo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.imb.imbdemo.CallActivity;
import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.R;
import com.imb.sdk.Poc;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.listener.PocCallListener;
import com.imb.sdk.manager.CallManager;
import com.imb.sdk.manager.ManagerService;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author - gongxun;
 * created on 2020/9/29-11:06;
 * description - 通话
 */
public abstract class BaseCallFragment extends Fragment {

    private FrameLayout container;

    protected CallInfo callInfo;

    protected CallManager manager;

    private View callOutView;
    private View callInView;
    private View onCallView;
    private PocCallListener listener;


    /**
     * 呼出
     *
     * @return 呼出的布局
     */
    protected abstract @LayoutRes
    int getCallOutResId();

    /**
     * 呼入
     *
     * @return 呼入的布局
     */
    protected abstract @LayoutRes
    int getCallInResId();

    /**
     * 电话中
     *
     * @return 电话中
     */
    protected abstract @LayoutRes
    int getOnCallResId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_call, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        container = (FrameLayout) view;

        initData();
        initView();

        doWhenCallStart();
    }

    private void initData() {
        manager = (CallManager) ManagerService.getManager(ManagerService.CALL_SERVICE);
        callInfo = getArguments().getParcelable(CallActivity.INTENT_CALL_INFO);

        if (callInfo.callType == PocConstant.CallType.CALL_VIDEO) {
            manager.startShowVideo(false);
        } else if (callInfo.callType == PocConstant.CallType.CALL_VIDEO_TWO_WAY) {
            manager.startShowVideo(true);
        }
    }

    private void initView() {
        showUI();
    }

    protected void showUI() {
        final int callDir = callInfo.callDir;
        if (callDir == PocConstant.CallDirection.DIR_OUT) {
            showOutUI();
        } else {
            showInUI();
        }
    }

    private void showInUI() {
        if (callInView == null) {
            callInView = inflaterLayout(getCallInResId());
        }
        container.removeAllViews();
        container.addView(callInView);

        updateCallInUI(callInfo, callInView);
    }

    protected void showOutUI() {
        if (callOutView == null) {
            callOutView = inflaterLayout(getCallOutResId());
        }
        container.removeAllViews();
        container.addView(callOutView);

        updateCallOutUI(callInfo, callOutView);
    }

    private boolean isOnCallUI = false;

    protected void showOnCallUI() {
        if (isOnCallUI) {
            return;
        }
        isOnCallUI = true;
        if (onCallView == null) {
            onCallView = inflaterLayout(getOnCallResId());
        }
        container.removeAllViews();
        container.addView(onCallView);

        updateOnCallUI(callInfo, onCallView);
    }

    private View inflaterLayout(@LayoutRes int layoutId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
        return view;
    }

    protected boolean hangUp() {
        final boolean b = manager.hangUpCall(callInfo.channel);
        //结束页面
        getActivity().finish();
        return b;
    }

    protected boolean accept() {
        final boolean b = manager.acceptCall(callInfo.callType, callInfo.channel);
        if (b) {
            showOnCallUI();
        }
        return b;
    }

    protected void mute() {
        manager.switchMicMute(!manager.isCurMicMute());
    }

    protected void speaker() {
        manager.switchSpeakerphoneOn(!manager.isSpeakerphoneOn());
    }

    @Override
    public void onDestroyView() {
        if (callInfo.callType == PocConstant.CallType.CALL_VIDEO
                || callInfo.callType == PocConstant.CallType.CALL_VIDEO_TWO_WAY) {
            manager.stopShowVideo();
        }
        doWhenCallEnd();
        super.onDestroyView();
    }

    protected void doWhenCallStart() {
        manager.enableReadWriteAudioAndVideo(true, true);
        listener = new PocCallListener() {
            @Override
            protected void onCallHangUp() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hangUp();
                    }
                });
            }

            @Override
            protected void onCallOutSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "停止放铃声", Toast.LENGTH_SHORT).show();
                        showOnCallUI();
                    }
                });
            }

            @Override
            protected void onStopPlayRing() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "停止放铃声", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            protected void onPlayRing() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "开始放铃声", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            protected void onCallFail(int callChannel, boolean isCallOut) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hangUp();
                    }
                });
            }

            @Override
            protected void onReceivedHalfVideoCall(String callTel, int callChannel) {

            }

            @Override
            protected void onReceivedFullVideoCall(String callTel, int callChannel) {

            }

            @Override
            protected void onReceivedFullVoiceCall(String callTel, int callChannel) {

            }

            @Override
            protected void onReceivedHalfVoiceCall(String callTel, int callChannel) {

            }
        };
        Poc.registerListener(listener);
    }

    protected void doWhenCallEnd() {
        Poc.unregisterListener(listener);
        //在挂断一次 避免没挂断
        manager.hangUpCall(callInfo.channel);

        manager.enableReadWriteAudioAndVideo(false, false);
        Log.i("ddddddd", "onDestroyView: ");
    }

    protected abstract void updateOnCallUI(CallInfo callInfo, View onCallView);

    protected abstract void updateCallOutUI(CallInfo callInfo, View callOutView);

    protected abstract void updateCallInUI(CallInfo callInfo, View callInView);
}
