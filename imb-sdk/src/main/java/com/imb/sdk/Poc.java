package com.imb.sdk;

import com.microsys.poc.jni.JniUtils;
import com.microsys.poc.jni.listener.BaseJniListener;

/**
 * @author - gongxun;
 * created on 2020/9/24-9:28;
 * description - 注册监听和取消监听
 */
public class Poc {


    public static void registerListener(BaseJniListener listener){
        JniUtils.getInstance().addJniListener(listener);
    }
    public static void unregisterListener(BaseJniListener listener){
        JniUtils.getInstance().removeJniListener(listener);
    }


}
