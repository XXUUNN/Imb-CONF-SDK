package com.imb.imbdemo.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.Constant;
import com.imb.imbdemo.R;
import com.microsys.poc.jni.show.IRemoteDrawCalc;
import com.microsys.poc.jni.show.LocalVideoView;
import com.microsys.poc.jni.show.MultiVideoShowManager;
import com.microsys.poc.jni.show.RemoteVideoViewGL;

/**
 * @author - gongxun;
 * created on 2020/9/29-17:00;
 * description - 视频通话
 */
public class VideoFullCallFragment extends BaseFullCallFragment {


    @Override
    protected int getOnCallResId() {
        return R.layout.layout_video_on_call;
    }

    @Override
    protected void updateOnCallUI(CallInfo callInfo, final View onCallView) {
        int screenOrientation = 0;
        final FrameLayout frameLayout = (FrameLayout) onCallView.findViewById(R.id.call_layout);
        LocalVideoView localPreview = new LocalVideoView(getActivity(), Constant.width,
                Constant.height, Constant.frameRate, Constant.bitRate, Constant.iFrameTime,
                0, screenOrientation, true);
        float density = getResources().getDisplayMetrics().density;
        android.hardware.Camera.CameraInfo cameraInfoinfo = new android.hardware.Camera.CameraInfo();
        int cameraOrientation = cameraInfoinfo.orientation;
        localPreview.setMaxSize(((int) (72 * density)),
                ((int) (128 * density)), screenOrientation).adjustAspectRatio(cameraOrientation, screenOrientation);
        localPreview.startCreateAvcEncoder(0);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (72 * density), (int) (128 * density), Gravity.RIGHT | Gravity.BOTTOM);
        frameLayout.addView(localPreview, layoutParams);
        localPreview.setZOrderMediaOverlay(true);
        localPreview.enableLocalVideo(true);

        final RemoteVideoViewGL remoteView = (RemoteVideoViewGL) MultiVideoShowManager.get(MultiVideoShowManager.DEFAULT_ID)
                .createRemoteVideoView(Constant.width, Constant.height, screenOrientation);
        frameLayout.post(new Runnable() {
            @Override
            public void run() {
                final int width = frameLayout.getWidth();
                final int height = frameLayout.getHeight();
                View remotePreview = remoteView
                        .startPreview(getActivity(), width, height, new IRemoteDrawCalc() {
                            @Override
                            public void calc(Params params) {
                                params.x = params.y = 0;
                                params.w = width;
                                params.h = height;
                            }
                        });
                frameLayout.addView(remotePreview, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

                onCallView.findViewById(R.id.media_view).bringToFront();
            }
        });

        onCallView.findViewById(R.id.btn_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });
        onCallView.findViewById(R.id.media_view).bringToFront();
    }

}
