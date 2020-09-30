package com.imb.imbdemo.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.R;
import com.imb.sdk.Poc;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.listener.PocTbcpListener;

import java.util.Map;

/**
 * @author - gongxun;
 * created on 2020/9/30-16:13;
 * description - 对讲
 */
public abstract class BaseHalfCallFragment extends BaseCallFragment {

    private PocTbcpListener pocTbcpListener;

    protected Handler handler = new Handler();

    private Button tbcpBtn;

    @Override

    protected int getCallOutResId() {
        return R.layout.layout_call_out;
    }

    @Override
    protected int getCallInResId() {
        return 0;
    }

    @Override
    protected int getOnCallResId() {
        return R.layout.layout_half_video_on_call;
    }

    @Override
    protected void updateCallOutUI(CallInfo callInfo, View callOutView) {
        TextView infoTv = (TextView) callOutView.findViewById(R.id.tv_info);
        infoTv.setText(callInfo.numType + "_" + callInfo.callNum);
        callOutView.findViewById(R.id.btn_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });
    }

    @Override
    protected void updateCallInUI(CallInfo callInfo, View callInView) {
    }

    @Override
    protected void updateOnCallUI(CallInfo callInfo, View onCallView) {
        onCallView.findViewById(R.id.btn_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });
        tbcpBtn = onCallView.findViewById(R.id.btn_tbcp);
    }

    @Override
    protected void showUI() {
        final int callDir = callInfo.callDir;
        if (callDir == PocConstant.CallDirection.DIR_OUT) {
            showOutUI();
        } else {
            //直接已经接听 到通话页面
            showOnCallUI();
        }
    }

    @Override
    protected void doWhenCallStart() {
        super.doWhenCallStart();
        pocTbcpListener = new PocTbcpListener() {
            @Override
            protected void notifyNeedTbcpRequest() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       tbcpRequest();
                       tbcpBtn.setText("放权");
                    }
                });
            }

            @Override
            protected void notifySsrcRelation(Map<String, Integer> map) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            protected void notifyTbcpRaiseUpHand(String tel) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
f
                    }
                });
            }

            @Override
            protected void notifyTbcpDisconnect(int result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            protected void notifyTbcpIdle() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            protected void notifyTbcpRevoke() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            protected void notifyTbcpTaken(String tel) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            protected void notifyTbcpDeny() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            protected void notifyTbcpGranted() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        Poc.registerListener(pocTbcpListener);
    }

    @Override
    protected void doWhenCallEnd() {
        Poc.unregisterListener(pocTbcpListener);
        super.doWhenCallEnd();
    }

    protected void tbcpRequest(){
        manager.tbcpRequest(callInfo.channel);
    }
    protected void tbcpRelease(){
        manager.tbcpRequest(callInfo.channel);
    }
}
