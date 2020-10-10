package com.microsys.poc.jni.show;

import android.content.Context;
import android.view.View;

public abstract class RemoteVideoView {

    public abstract View startPreview(Context context, int previewMaxW, int previewMaxH, IRemoteDrawCalc iRemoteDrawCalc,TriangleVerticesCallback callback);

    public abstract int showVideo(byte[] data, int len, int width, int height, int direction);

    public abstract void clear();

    public abstract void hideView();

    /**
     * 强制回调计算显示参数
     */
    public abstract void requestCalcSize();

    /**
     * 允许接收流
     *
     * @param b true允许
     */
    public abstract void enable(boolean b);

    /**
     * 本地方向变动 需要调整远程view显示
     * @param orientation 方向{@link VideoDirection}
     * @param isNeedCalcRemoteViewSize 是否需要重新计算显示
     */
    public abstract void changeScreenOrientation(int orientation, boolean isNeedCalcRemoteViewSize);

    /**
     * 当前锁定设备方向
     * @param lockedOrientation 锁定的方向
     */
    public abstract void setScreenOrientationLocked(int lockedOrientation);
}