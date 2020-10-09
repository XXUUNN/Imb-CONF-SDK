package com.imb.imbdemo;

import android.os.Bundle;

import com.imb.imbdemo.fragment.VideoFullCallFragment;
import com.imb.imbdemo.fragment.VideoHalfCallFragment;
import com.imb.imbdemo.fragment.VoiceFullCallFragment;
import com.imb.imbdemo.fragment.VoiceHalfCallFragment;
import com.imb.sdk.data.PocConstant;

import androidx.appcompat.app.AppCompatActivity;

public class CallActivity extends AppCompatActivity {

    public static final String INTENT_CALL_INFO = "call_info";
    private CallInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        getCallInfo();

        setCallTitle();

        addCallFragment();
    }

    private void addCallFragment() {
        if (info.callType == PocConstant.CallType.CALL_VOICE){
            final VoiceFullCallFragment fragment = new VoiceFullCallFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }else if (info.callType == PocConstant.CallType.CALL_VIDEO){
            final VideoFullCallFragment fragment = new VideoFullCallFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }else if (info.callType == PocConstant.CallType.CALL_VOICE_TWO_WAY){
            final VoiceHalfCallFragment fragment = new VoiceHalfCallFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }else if (info.callType == PocConstant.CallType.CALL_VIDEO_TWO_WAY){
            final VideoHalfCallFragment fragment = new VideoHalfCallFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }

    }

    private void setCallTitle() {
        final StringBuilder stringBuilder = new StringBuilder();
        if (info.numType == PocConstant.ContactType.TYPE_PERSON) {
            stringBuilder.append("单人");
        } else {
            stringBuilder.append("组");
        }
        stringBuilder.append(info.callNum);
        if (info.callType == PocConstant.CallType.CALL_VOICE) {
            stringBuilder.append("语音通话");
        } else if (info.callType == PocConstant.CallType.CALL_VOICE_TWO_WAY) {
            stringBuilder.append("语音对讲");
        } else if (info.callType == PocConstant.CallType.CALL_VIDEO) {
            stringBuilder.append("视频通话");
        } else {
            stringBuilder.append("视频对讲");
        }
        setTitle(stringBuilder);
    }

    private void getCallInfo() {
        info = getIntent().getParcelableExtra(INTENT_CALL_INFO);
    }
}
