package com.imb.imbdemo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author - gongxun;
 * created on 2020/9/28-14:19;
 * description -
 */
public class Sp {
    public static final String POC_NUM = "poc_num";
    public static final String POC_PWD = "poc_pwd";
    public static final String POC_SERVER = "poc_server";

    public static final String CENTER_MEETING_NUM = "center_meeting_num";
    public static final String CENTER_NAME = "center_name";
    public static final String CENTER_PWD = "center_pwd";
    public static final String CENTER_HOST = "center_host";



    public static SharedPreferences getSp(Context context){
        return context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }
}
