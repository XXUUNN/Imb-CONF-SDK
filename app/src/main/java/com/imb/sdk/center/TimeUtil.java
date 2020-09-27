package com.imb.sdk.center;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by fzq on 2018/3/21.
 */

public class TimeUtil {

    /**
     * 在get请求中用到的时间格式
     */
    public static String formatMillisToGMT() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(new Date());
    }
}
