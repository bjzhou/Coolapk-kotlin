package bjzhou.coolapk.app.util;

import android.util.Log;

import java.util.Calendar;

/**
 * User: qii
 * Date: 12-8-28
 */
public class TimeUtility {

    private static final String DATE_FORMAT = "M月d日 HH:mm";
    private static final String YEAR_FORMAT = "yyyy年 M月d日 HH:mm";
    private static final String TAG = "TimeUtility";
    private static int MILL_MIN = 1000 * 60;
    private static int MILL_HOUR = MILL_MIN * 60;
    private static int MILL_DAY = MILL_HOUR * 24;

    private static Calendar msgCalendar = null;
    private static java.text.SimpleDateFormat dayFormat = null;
    private static java.text.SimpleDateFormat dateFormat = null;
    private static java.text.SimpleDateFormat yearFormat = null;


    private TimeUtility() {

    }

    public static String getTime(long time) {
        time = time * 1000;
        long now = System.currentTimeMillis();
        long msg = time;

        Calendar nowCalendar = Calendar.getInstance();

        if (msgCalendar == null)
            msgCalendar = Calendar.getInstance();

        msgCalendar.setTimeInMillis(time);

        long calcMills = now - msg;

        long calSeconds = calcMills / 1000;

        if (calSeconds < 60) {
            return "刚刚";
        }

        long calMins = calSeconds / 60;

        if (calMins < 60) {

            return new StringBuilder().append(calMins).append("分钟前").toString();
        }

        long calHours = calMins / 60;

        if (calHours < 24 && isSameDay(nowCalendar, msgCalendar)) {
            if (dayFormat == null)
                dayFormat = new java.text.SimpleDateFormat("HH:mm");

            String result = dayFormat.format(msgCalendar.getTime());
            return new StringBuilder().append("今天").append(" ").append(result).toString();

        }


        long calDay = calHours / 24;

        if (calDay < 31) {
            if (isYesterDay(nowCalendar, msgCalendar)) {
                if (dayFormat == null)
                    dayFormat = new java.text.SimpleDateFormat("HH:mm");

                String result = dayFormat.format(msgCalendar.getTime());
                return new StringBuilder("昨天").append(" ").append(result).toString();

            } else if (isTheDayBeforeYesterDay(nowCalendar, msgCalendar)) {
                if (dayFormat == null)
                    dayFormat = new java.text.SimpleDateFormat("HH:mm");

                String result = dayFormat.format(msgCalendar.getTime());
                return new StringBuilder("前天").append(" ").append(result).toString();

            } else {
                if (dateFormat == null)
                    dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);

                String result = dateFormat.format(msgCalendar.getTime());
                return new StringBuilder(result).toString();
            }
        }

        long calMonth = calDay / 31;

        if (calMonth < 12 && isSameYear(nowCalendar, msgCalendar)) {
            if (dateFormat == null)
                dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);

            String result = dateFormat.format(msgCalendar.getTime());
            return new StringBuilder().append(result).toString();

        }
        if (yearFormat == null)
            yearFormat = new java.text.SimpleDateFormat(YEAR_FORMAT);
        String result = yearFormat.format(msgCalendar.getTime());
        return new StringBuilder().append(result).toString();


    }

    private static boolean isSameHalfDay(Calendar now, Calendar msg) {
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int msgHOur = msg.get(Calendar.HOUR_OF_DAY);

        if (nowHour <= 12 & msgHOur <= 12) {
            return true;
        } else if (nowHour >= 12 & msgHOur >= 12) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isSameDay(Calendar now, Calendar msg) {
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int msgDay = msg.get(Calendar.DAY_OF_YEAR);

        return nowDay == msgDay;
    }

    private static boolean isYesterDay(Calendar now, Calendar msg) {
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int msgDay = msg.get(Calendar.DAY_OF_YEAR);

        return (nowDay - msgDay) == 1;
    }

    private static boolean isTheDayBeforeYesterDay(Calendar now, Calendar msg) {
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int msgDay = msg.get(Calendar.DAY_OF_YEAR);

        return (nowDay - msgDay) == 2;
    }

    private static boolean isSameYear(Calendar now, Calendar msg) {
        int nowYear = now.get(Calendar.YEAR);
        int msgYear = msg.get(Calendar.YEAR);

        return nowYear == msgYear;
    }
}
