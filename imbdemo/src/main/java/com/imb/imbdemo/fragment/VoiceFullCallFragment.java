package com.imb.imbdemo.fragment;

import android.view.View;

import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.R;

/**
 * @author - gongxun;
 * created on 2020/9/29-15:05;
 * description -
 */
public class VoiceFullCallFragment extends BaseFullCallFragment {
    @Override
    protected int getOnCallResId() {
        return R.layout.layout_voice_on_call;
    }

    @Override
    protected void updateOnCallUI(CallInfo callInfo, View onCallView) {
        onCallView.findViewById(R.id.btn_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });
        onCallView.findViewById(R.id.btn_mute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute();
            }
        });
        onCallView.findViewById(R.id.btn_speaker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaker();
            }
        });
    }
}
