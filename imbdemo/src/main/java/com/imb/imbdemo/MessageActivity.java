package com.imb.imbdemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.imb.sdk.Poc;
import com.imb.sdk.listener.PocMessageListener;
import com.imb.sdk.manager.ManagerService;
import com.imb.sdk.manager.MsgManager;
import com.imb.sdk.msg.MessageUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity{

    private ScrollView scrollView;
    private TextView msgTv;
    private PocMessageListener messageListener;

    private StringBuilder messagesStringBuilder = new StringBuilder();
    private EditText targetNumEdit;
    private EditText messageEdit;
    private String targetNum;
    private MsgManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        setTitle("消息");

        scrollView = (ScrollView) findViewById(R.id.scroview);
        msgTv = (TextView) findViewById(R.id.tv_msg_received);

        targetNumEdit = (EditText) findViewById(R.id.edit_target_num);
        messageEdit = (EditText) findViewById(R.id.edit_send);
        messageEdit.setHorizontallyScrolling(false);
        messageEdit.setMaxLines(Integer.MAX_VALUE);
        messageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    targetNum = targetNumEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(targetNum)) {
                        Toast.makeText(MessageActivity.this, "号码不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    final String content = messageEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(MessageActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    //发送文字
                    sendTxtMsg(content);
                    messageEdit.setText(null);
                    return true;
                }
                return false;
            }
        });

        startListenMsg();
        manager = (MsgManager) ManagerService.getManager(ManagerService.MSG_SERVICE);
    }

    private void sendTxtMsg(final String content) {
        manager.sendTxtMessage(targetNum, content, new MessageUtils.MessageCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MessageActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MessageActivity.this, "发送失败"+content, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startListenMsg(){
        messageListener = new PocMessageListener() {
            @Override
            protected void notifyRecvTxtMsg(String numA, String numB, String text) {
                handleTxtMsg(numA,numB,text);
            }

            @Override
            protected void notifyRecvTxtPic(String numA, String numB, String text) {

            }

            @Override
            protected void notifyRecvTxtVideo(String numA, String numB, String text) {

            }

            @Override
            protected void notifyRecvTxtAudio(String numA, String numB, String text) {

            }

            @Override
            protected void notifyRecvTxtFile(String numA, String numB, String text) {

            }
        };
        Poc.registerListener(messageListener);
    }

    private void handleTxtMsg(final String numA, final String numB, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesStringBuilder.append("\nnumA="+numA +" numB=" + numB+" text=["+text+"]");
                msgTv.setText(messagesStringBuilder);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void stopListenMsg(){
        if (messageListener != null) {
            Poc.unregisterListener(messageListener);
            messageListener = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopListenMsg();
        super.onDestroy();
    }
}
