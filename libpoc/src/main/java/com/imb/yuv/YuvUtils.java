package com.imb.yuv;

/**
 * @author - gongxun;
 * created on 2019/7/26-9:23;
 * description -
 */
public class YuvUtils {
    static {
        System.loadLibrary("yuv");
    }
    public static native void NV21ToI420(byte[] in, byte[] out, int w, int h);
    public static native void NV12ToI420(byte[] in, byte[] out, int w, int h);

    public static native void NV21ToNV12(byte[] in, byte[] out, int w, int h);

    /**
     * @param rotation 90 180 270
     */
    public static native void RotateNV21(byte[] in, byte[] out, int w, int h, int rotation);

    /**
     * @param rotation 90 180 270
     */
    public static native void RotateI420(byte[] in, byte[] out, int w, int h, int rotation);
}
