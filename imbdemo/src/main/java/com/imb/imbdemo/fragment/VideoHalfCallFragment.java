package com.imb.imbdemo.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.imb.imbdemo.CallInfo;
import com.imb.imbdemo.Constant;
import com.imb.imbdemo.R;
import com.microsys.poc.jni.show.LocalVideoView;

/**
 * @author - gongxun;
 * created on 2020/10/9-10:38;
 * description - 视频半双工
 */
public class VideoHalfCallFragment extends BaseHalfCallFragment {
    int screenOrientation = 0;
    @Override
    protected void updateOnCallUI(CallInfo callInfo, final View onCallView) {
        super.updateOnCallUI(callInfo, onCallView);
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

        onCallView.findViewById(R.id.media_view).bringToFront();
    }
}
