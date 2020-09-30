package com.imb.sdk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Administrator
 */

public class DateUtils {
    public static boolean equals(Date date1, Date date2) {
        if (date1 != null) {
            if (date2 != null) {
                return date1.getTime() == date2.getTime();
            } else {
                return false;
            }
        } else {
            if (date2 != null) {
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 当前几天后的日期
     *
     * @param num
     */
    public static String nextDate(int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, +num);
        return dateToString(cal.getTime(), "yyyy-MM-dd");
    }

    /**
     * Date 转 String
     *
     * @param date
     * @param pattern
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String dateToString(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                String dateStr = sdf.format(date);
                return dateStr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param oldTime 时间
     * @return -1 ：同一天.    0：昨天 .   1 ：至少是前天.
     * @throws ParseException 转换异常
     */
    public static int isYesterday(Date oldTime) {
        Date newTime = new Date();
        //将下面的 理解成  yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = null;
        try {
            today = format.parse(todayStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //昨天 86400000=24*60*60*1000 一天
        if ((today.getTime() - oldTime.getTime()) > 0 && (today.getTime() - oldTime.getTime()) <= 86400000) {
            return 0;
        } else if ((today.getTime() - oldTime.getTime()) <= 0) { //至少是今天
            return -1;
        } else { //至少是前天
            return 1;
        }

    }

    /**
     * 返回北京时间
     *
     * @return 北京时间的毫秒
     */
    public static long getCurTimeWithTimeZone() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = calendar.getTime();
        long time = date.getTime();
        return time;
    }

    public static Date str2date(String timeStr, String pattern) {
        Date date = null;
        try {
            SimpleDateFormat r = new SimpleDateFormat(pattern);
            date = r.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 两个时分秒相同的date  相差天数
     * date1-date2
     *
     * @return 相差天数
     */
    public static int differentDays(Date date1, Date date2) {
        int days = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static String toTimeDesc(Date time) {
        if (time == null) {
            return null;
        }
        int offset = DateUtils.isYesterday(time);
        String timeStr;
        if (offset < 0) {
            //今天
            timeStr = "今天" + DateUtils.dateToString(time, "HH:mm");
        } else if (offset == 0) {
            //昨天
            timeStr = "昨天" + DateUtils.dateToString(time, "HH:mm");
        } else {
            //前几天
            timeStr = DateUtils.dateToString(time, "yyyy年MM月dd日 HH:mm");
        }
        return timeStr;
    }

    public static boolean isSameDay(Date time1, Date time2) {
        Calendar instance1 = Calendar.getInstance();
        instance1.setTime(time1);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTime(time2);

        if (instance1.get(Calendar.YEAR) == instance2.get(Calendar.YEAR)
                && instance1.get(Calendar.DAY_OF_YEAR) == instance2.get(Calendar.DAY_OF_YEAR)) {
            //同一天
            return true;
        }
        return false;
    }

    public static String getDayOfWeek(Date time) {
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String day = dateFm.format(time);
        return day;
    }

    public static String formatDay(Date time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(time);
    }
}
