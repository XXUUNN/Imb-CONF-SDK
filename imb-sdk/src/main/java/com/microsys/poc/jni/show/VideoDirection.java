package com.microsys.poc.jni.show;

/**
 * @author - gongxun;
 * created on 2019/9/10-11:57;
 * description - 视频的方向
 * <p>
 * 用位表示比较方便，方便取出设备类型和流的类型
 * 设备类型3位（android手机、android平板和电视、iOS手机、windows电脑）、流的类型2位（摄像头1、摄像头2、录屏）
 */
public class VideoDirection {

    public static final int DEVICE_ANDROID_PHONE = 0;
    public static final int DEVICE_ANDROID_HD = 1;
    public static final int DEVICE_ANDROID_TV = 2;

    public static final int CAMERA_BACK = 0;
    public static final int CAMERA_FRONT = 1;

    public static final int SCREEN_PORTRAIT = 0;
    public static final int SCREEN_LANDSCAPE_LEFT = 1;
    public static final int SCREEN_LANDSCAPE_RIGHT = 2;

    public static final int SCREEN_RECORD = 0;

    /**
     * 安卓手机上的方向
     */
    public static final int ANDROID_BACK_PORTRAIT = 0;
    public static final int ANDROID_FRONT_PORTRAIT = 1;
    public static final int ANDROID_BACK_LANDSCAPE = 2;
    public static final int ANDROID_FRONT_LANDSCAPE = 3;

    /**
     * iOS手机方向
     */
    public static final int IOS_BACK_PORTRAIT = 4;
    public static final int IOS_FRONT_PORTRAIT = 5;
    public static final int IOS_BACK_LANDSCAPE = 6;
    public static final int IOS_FRONT_LANDSCAPE = 7;


    /**
     * 安卓平板
     */
    public static final int ANDROID_PAD_BACK = 8;
    public static final int ANDROID_PAD_FRONT = 9;
    public static final int ANDROID_PAD_SCREEN_RECORD = 10;

    /**
     * 安卓电视
     */
    public static final int ANDROID_TV_BACK = 11;
    public static final int ANDROID_TV_FRONT = 12;
    public static final int ANDROID_TV_SCREEN_RECORD = 13;

    public static final int ANDROID_BACK_REVERSE_LANDSCAPE = 14;
    public static final int ANDROID_FRONT_REVERSE_LANDSCAPE = 15;

    /**
     * 自己设备的类型
     */
    public static final int MY_DEVICE_TYPE = getPhoneType();

    private static int getPhoneType() {
        int deviceType = DEVICE_ANDROID_PHONE;
        return deviceType;
    }

    /**
     * 获取方向 不带屏幕录制的
     *
     * @param cameraDirection 摄像头的方向
     * @param screenDirection 屏幕的方向
     * @return 视频里的方向
     */
    public static int getDirection(int cameraDirection, int screenDirection) {

        int direction;
        //手机 没有录屏类型
        if (screenDirection == SCREEN_LANDSCAPE_LEFT) {
            //横向
            if (cameraDirection == CAMERA_FRONT) {
                //前置
                direction = ANDROID_FRONT_LANDSCAPE;
            } else {
                //后置
                direction = ANDROID_BACK_LANDSCAPE;
            }
        } else if (screenDirection == SCREEN_LANDSCAPE_RIGHT) {
            //横向
            if (cameraDirection == CAMERA_FRONT) {
                //前置
                direction = ANDROID_FRONT_REVERSE_LANDSCAPE;
            } else {
                //后置
                direction = ANDROID_BACK_REVERSE_LANDSCAPE;
            }
        } else {
            //竖向
            if (cameraDirection == CAMERA_FRONT) {
                //前置
                direction = ANDROID_FRONT_PORTRAIT;
            } else {
                //后置
                direction = ANDROID_BACK_PORTRAIT;
            }
        }
        return direction;
    }

    public static boolean isSameDirection(int screenDir, int videoDir) {
        if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
            if (videoDir == VideoDirection.ANDROID_BACK_PORTRAIT
                    || videoDir == VideoDirection.ANDROID_FRONT_PORTRAIT
                    || videoDir == VideoDirection.IOS_BACK_PORTRAIT
                    || videoDir == VideoDirection.IOS_FRONT_PORTRAIT) {
                return true;
            }
        } else if (screenDir == VideoDirection.SCREEN_LANDSCAPE_LEFT
                || screenDir == VideoDirection.SCREEN_LANDSCAPE_RIGHT) {
            if (videoDir == VideoDirection.ANDROID_BACK_LANDSCAPE
                    || videoDir == VideoDirection.ANDROID_FRONT_LANDSCAPE
                    || videoDir == VideoDirection.IOS_BACK_LANDSCAPE
                    || videoDir == VideoDirection.IOS_FRONT_LANDSCAPE
                    || videoDir == VideoDirection.ANDROID_PAD_BACK
                    || videoDir == VideoDirection.ANDROID_PAD_FRONT
                    || videoDir == VideoDirection.ANDROID_PAD_SCREEN_RECORD
                    || videoDir == VideoDirection.ANDROID_TV_BACK
                    || videoDir == VideoDirection.ANDROID_TV_FRONT
                    || videoDir == VideoDirection.ANDROID_TV_SCREEN_RECORD) {
                return true;
            }
        }
        return false;
    }
}
