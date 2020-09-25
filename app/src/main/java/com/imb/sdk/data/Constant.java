package com.imb.sdk.data;

import com.microsys.poc.jni.show.VideoDirection;

/**
 * @author - gongxun;
 * created on 2020/9/23-17:13;
 * description -
 */
public class Constant {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    /**
     * sftp默认端口
     */
    public static final int DEFAULT_SFTP_PORT = 2222;

    /**
     * 默认的sip端口
     */
    public static final int DEFAULT_SIP_PORT = 6689;

    /**
     * 默认PoC登录超时时间
     */
    public static final int DEFAULT_POC_EXPIRE_TIME = 3600;

    /**
     * 默认的最大的log文件夹的值
     * byte
     */
    public static final int MAX_LOG_DIR_SIZE_BYTES = 50 * 1024 * 1024;

    /**
     * 默认的登录行为超时时间
     * ms
     */
    public static final int DEFAULT_LOGIN_TIME_OUT = 8 * 1000;

    /**
     * 最新的可能有四个流
     */
    public static final int REMOTE_VIEW_MAX_COUNT = 4;

    public static final int LOCKED_SCREEN_ORIENTATION = VideoDirection.SCREEN_LANDSCAPE_LEFT;

}
