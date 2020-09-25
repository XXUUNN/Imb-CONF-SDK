package com.microsys.poc.jni.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 日期时间工具类
 *
 * @author LuLihong
 * @date 2011-11-3
 */
public class TimeUtils {
    private static final SimpleDateFormat FMT_YMDHMS_1 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");


    /**
     * 获取当前时间
     */
    private static long currLong() {
        return System.currentTimeMillis();
    }

    public static String currTime() {
        return longToString(currLong());
    }

    private static String longToString(long ltime) {
        return longToString(ltime, FMT_YMDHMS_1);
    }

    private static String longToString(long ltime, SimpleDateFormat format) {
        Date date = new Date(ltime);
        return format.format(date);
    }
}
