package com.imb.imbdemo.fragment;

import android.view.View;
import android.widget.TextView;

import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.R;

/**
 * @author - gongxun;
 * created on 2020/9/29-11:48;
 * description - 全双工
 */
public abstract class BaseFullCallFragment extends BaseCallFragment {
    @Override
    protected int getCallOutResId() {
        return R.layout.layout_call_out;
    }

    @Override
    protected int getCallInResId() {
        return R.layout.layout_call_in;
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
        TextView infoTv = (TextView) callInView.findViewById(R.id.tv_info);
        infoTv.setText(callInfo.numType + "_" + callInfo.callNum);
        callInView.findViewById(R.id.btn_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });
        callInView.findViewById(R.id.btn_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept();
            }
        });
    }
}
