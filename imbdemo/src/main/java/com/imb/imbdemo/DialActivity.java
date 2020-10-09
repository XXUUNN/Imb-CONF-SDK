package com.imb.imbdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.imb.sdk.Poc;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.listener.PocCallListener;
import com.imb.sdk.manager.CallManager;
import com.imb.sdk.manager.ManagerService;

import androidx.appcompat.app.AppCompatActivity;

public class DialActivity extends AppCompatActivity {
    public static final String TAG = DialActivity.class.getSimpleName();

    private CallManager manager;
    private EditText pocNumEdit;
    private PocCallListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        setTitle("呼叫");

        pocNumEdit = (EditText) findViewById(R.id.edit_phone_num);

        manager = (CallManager) ManagerService.getManager(ManagerService.CALL_SERVICE);


        listener = new PocCallListener() {
            @Override
            protected void onCallHangUp() {

            }

            @Override
            protected void onCallOutSuccess() {

            }

            @Override
            protected void onStopPlayRing() {

            }

            @Override
            protected void onPlayRing() {

            }

            @Override
            protected void onCallFail(int callChannel, boolean isCallOut) {

            }

            @Override
            protected void onReceivedHalfVideoCall(String callTel, int callChannel) {
                // TODO: 2020/10/9 需要自己判断是组还是人
                final CallInfo callInfo = new CallInfo(callTel, PocConstant.CallDirection.DIR_IN,
                        PocConstant.ContactType.TYPE_GROUP, PocConstant.CallType.CALL_VIDEO_TWO_WAY,
                        callChannel);
                toCallUI(callInfo);
            }

            @Override
            protected void onReceivedFullVideoCall(String callTel, int callChannel) {
                final CallInfo callInfo = new CallInfo(callTel, PocConstant.CallDirection.DIR_IN,
                        PocConstant.ContactType.TYPE_PERSON, PocConstant.CallType.CALL_VIDEO,
                        callChannel);
                toCallUI(callInfo);
            }

            @Override
            protected void onReceivedFullVoiceCall(String callTel, int callChannel) {
                final CallInfo callInfo = new CallInfo(callTel, PocConstant.CallDirection.DIR_IN,
                        PocConstant.ContactType.TYPE_PERSON, PocConstant.CallType.CALL_VOICE,
                        callChannel);
                toCallUI(callInfo);
            }

            @Override
            protected void onReceivedHalfVoiceCall(String callTel, int callChannel) {
                // TODO: 2020/10/9 需要自己判断是组还是人
                final CallInfo callInfo = new CallInfo(callTel, PocConstant.CallDirection.DIR_IN,
                        PocConstant.ContactType.TYPE_GROUP, PocConstant.CallType.CALL_VOICE_TWO_WAY,
                        callChannel);
                toCallUI(callInfo);
            }
        };
        Poc.registerListener(listener);

    }

    public void onCallOutClick(View view) {
        final String pocNum = pocNumEdit.getText().toString().trim();
        if (TextUtils.isEmpty(pocNum)) {
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int id = view.getId();
        int channel = -1;
        int type = 0;
        int numType = 0;
        switch (id) {
            case R.id.btn_person_voice_full:
                type = PocConstant.CallType.CALL_VOICE;
                numType = PocConstant.ContactType.TYPE_PERSON;
                break;
            case R.id.btn_person_video_full:
                type = PocConstant.CallType.CALL_VIDEO;
                numType = PocConstant.ContactType.TYPE_PERSON;
                break;
            case R.id.btn_group_voice_half:
                type = PocConstant.CallType.CALL_VOICE_TWO_WAY;
                numType = PocConstant.ContactType.TYPE_GROUP;
                break;
            case R.id.btn_group_video_half:
                type = PocConstant.CallType.CALL_VIDEO_TWO_WAY;
                numType = PocConstant.ContactType.TYPE_GROUP;
                break;
            default:
        }
        channel = manager.makeCall(type, pocNum);
        if (channel <= 0) {
            Toast.makeText(this, "呼叫失败", Toast.LENGTH_SHORT).show();
        } else {
            CallInfo info = new CallInfo(pocNum, PocConstant.CallDirection.DIR_OUT,
                    numType, type, channel);
            toCallUI(info);
        }
    }

    private void toCallUI(CallInfo info) {
        final Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(CallActivity.INTENT_CALL_INFO,info);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Poc.unregisterListener(listener);
        super.onDestroy();
    }
}
