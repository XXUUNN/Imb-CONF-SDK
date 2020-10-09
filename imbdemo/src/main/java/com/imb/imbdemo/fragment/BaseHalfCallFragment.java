package com.imb.imbdemo.fragment;

import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.Constant;
import com.imb.imbdemo.R;
import com.imb.sdk.Poc;
import com.imb.sdk.data.PocConstant;
import com.imb.sdk.listener.PocTbcpListener;
import com.imb.sdk.listener.PocUserChangeInMeetingListener;
import com.microsys.poc.jni.entity.UserChangeInMeeting;

import java.util.Map;

import androidx.appcompat.app.AlertDialog;

/**
 * @author - gongxun;
 * created on 2020/9/30-16:13;
 * description - 对讲
 */
public abstract class BaseHalfCallFragment extends BaseCallFragment {

    private PocTbcpListener pocTbcpListener;

    protected Handler handler = new Handler();
    private StringBuilder stringBuilder = new StringBuilder();

    private Button tbcpBtn;
    private TextView outTv;
    private ScrollView scrollView;
    private PocUserChangeInMeetingListener pocUserChangeInMeetingListener;
    private String caller;
    /**
     * 是否申请对话了
     */
    private boolean isRequested;

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
        tbcpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequested) {
                    tbcpRelease();
                    updateTbcpBtn(false);
                } else {
                    tbcpRequest();
                    updateTbcpBtn(true);
                }
            }
        });
        outTv = onCallView.findViewById(R.id.tv_out);
        scrollView = onCallView.findViewById(R.id.scroll_view);
        final EditText editText = (EditText) onCallView.findViewById(R.id.edit_force);
        editText.setHorizontallyScrolling(false);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    final String targetNum = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(targetNum)) {
                        Toast.makeText(getContext(), "号码不能为空", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    new AlertDialog.Builder(getContext()).setNegativeButton("强制放权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            forceOneRelease(targetNum);
                        }
                    }).setPositiveButton("强制抢权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            forceOneRequest(targetNum);
                        }
                    }).setMessage("强制" + targetNum + "放权或者抢权").show();

                    return true;
                }
                return false;
            }
        });
    }

    private void updateTbcpBtn(boolean isRequested) {
        if (isRequested != this.isRequested) {
            this.isRequested = isRequested;
            if (this.isRequested) {
                tbcpBtn.setText("放权");
            } else {
                tbcpBtn.setText("抢权");
            }
        }
    }

    @Override
    protected void showUI() {
        final int callDir = callInfo.callDir;
        if (callDir == PocConstant.CallDirection.DIR_OUT) {
//            showOutUI(); todo 应该是呼出界面 收到呼出成功 在进入通话中界面
            // TODO: 2020/10/9 由于对方直接接听的，监听的晚 可能会漏掉；呼出成功消息 demo没写常在监听，这里就直接进入通话中界面
            showOnCallUI();
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
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tbcpRequest();
                            updateTbcpBtn(true);
                        }
                    });
                }
            }

            @Override
            protected void notifySsrcRelation(final Map<String, Integer> map) {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifySsrcRelation " + map.toString());

                            if (map.size() == 1) {
                                final String num = map.keySet().iterator().next();
                                if (TextUtils.equals(num, Constant.myPocNum)) {
                                    if (!isRequested) {
                                        //自己放权成功了
                                        updateTbcpBtn(false);
                                    }
                                }
                            }
                            // TODO: 2020/10/9 如果自己发起者，收到这个消息，里面只有自己 自己又刚执行了 release，那么就是
                            // TODO: 2020/10/9 自己放权成功 .除了发起者 其他人放权都会收到idle消息
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpRaiseUpHand(final String tel) {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifyTbcpRaiseUpHand " + tel);
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpDisconnect(int result) {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpIdle() {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifyTbcpIdle ");
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpRevoke() {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifyTbcpRevoke ");
                            updateTbcpBtn(false);
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpTaken(final String tel) {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifyTbcpTaken " + tel);
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpDeny() {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifyTbcpDeny ");
                            updateTbcpBtn(false);
                        }
                    });
                }
            }

            @Override
            protected void notifyTbcpGranted() {
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out("notifyTbcpGranted ");
                            updateTbcpBtn(true);
                        }
                    });
                }
            }
        };
        Poc.registerListener(pocTbcpListener);
        pocUserChangeInMeetingListener = new PocUserChangeInMeetingListener() {
            @Override
            public void onRecUserChangedInMeeting(final UserChangeInMeeting userChangeInMeeting) {
                caller = userChangeInMeeting.getCaller();
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            out(userChangeInMeeting.toString());
                            out(TextUtils.equals(Constant.myPocNum, caller) ? "自己是发起者" : "自己不是发起者");
                        }
                    });
                }
            }
        };
        Poc.registerListener(pocUserChangeInMeetingListener);
    }

    @Override
    protected void doWhenCallEnd() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        Poc.unregisterListener(pocUserChangeInMeetingListener);
        Poc.unregisterListener(pocTbcpListener);
        super.doWhenCallEnd();
    }

    protected void tbcpRequest() {
        manager.tbcpRequest(callInfo.channel);
    }

    protected void tbcpRelease() {
        manager.tbcpRelease(callInfo.channel);
    }

    protected void forceOneRequest(String num) {
        manager.tbcpForceOneRequest(callInfo.channel, num);
    }

    protected void forceOneRelease(String num) {
        manager.tbcpForceOneRelease(callInfo.channel, num);
    }

    protected void out(String text) {
        if (outTv != null && getContext() != null) {
            stringBuilder.append(text + "\n");
            outTv.setText(stringBuilder);
            scrollView.fullScroll(View.FOCUS_DOWN);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
