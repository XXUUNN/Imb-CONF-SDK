package com.imb.sdk.login;

import android.text.TextUtils;

import com.imb.sdk.data.PocConstant;

/**
 * @author - gongxun;
 * created on 2019/4/25-15:20;
 * description -
 */
public class ResponseTranslateUtils {

    public static String loginResultToDesc(int result) {
        String str;
        if (result == PocConstant.RegisterResult.RESULT_SUCCESS) {
            str = "成功";
        } else if (result == PocConstant.RegisterResult.RESULT_TIME_OUT
                || result == PocConstant.RegisterResult.RESULT_TIME_OUT_1
                || result == PocConstant.RegisterResult.RESULT_TIME_OUT_2) {
            str = "超时";
        } else if (result == PocConstant.RegisterResult.RESULT_PASSWORD_ERROR) {
            str = "密码错误";
        } else if (result == PocConstant.RegisterResult.RESULT_ACCOUNT_CLOSE) {
            str = "账号被遥闭";
        } else if (result == PocConstant.RegisterResult.RESULT_CANCEL) {
            str = "登录取消";
        } else if (result == PocConstant.RegisterResult.RESULT_CONNECT_SFTP_ERROR) {
            str = "sftp连接失败";
        } else {
            str = "异常错误";
        }
        return str;
    }

    /**
     * 服务端返回的用户权限的 int -> boolean
     *
     * @param permission 用户权限
     */
    public static boolean toUserPermission(int permission) {
        if (permission == 0) {
            return false;
        } else if (permission == 1) {
            return true;
        } else {
            return true;
        }
    }

    /**
     * 服务端的id 都是 用/分割
     */
    public static long[] toLongArray(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] arr = str.split("/");
            int length = arr.length;
            long[] longs = new long[length];
            for (int i = 0; i < length; i++) {
                longs[i] = Long.parseLong(arr[i]);
            }
            return longs;
        }
        return null;
    }

    /**
     * 服务端的id 都是 用/分割
     */
    public static String[] toArray(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] arr = str.split("/");
            return arr;
        }
        return null;
    }
}
