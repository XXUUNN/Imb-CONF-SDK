package com.imb.sdk.center;

import android.text.TextUtils;

/**
 * @author - gongxun;
 * created on 2019/7/17-10:17;
 * description - 服务端返回的部分数据的转换
 */
public class ImbServerResponseTranslate {
    public static boolean isCallInstanceType(String instanceType) {
        if (TextUtils.equals("call", instanceType)) {
            return true;
        } else {
            return false;
        }
    }
}
