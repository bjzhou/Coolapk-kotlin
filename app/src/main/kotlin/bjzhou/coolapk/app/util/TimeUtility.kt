package bjzhou.coolapk.app.util

import java.util.*

/**
 * User: qii
 * Date: 12-8-28
 */
object TimeUtility {

    private val DATE_FORMAT = "M月d日 HH:mm"
    private val YEAR_FORMAT = "yyyy年 M月d日 HH:mm"
    private val TAG = "TimeUtility"
    private val MILL_MIN = 1000 * 60
    private val MILL_HOUR = MILL_MIN * 60
    private val MILL_DAY = MILL_HOUR * 24

    private var msgCalendar = Calendar.getInstance()
    private var dayFormat: java.text.SimpleDateFormat? = null
    private var dateFormat: java.text.SimpleDateFormat? = null
    private var yearFormat: java.text.SimpleDateFormat? = null

    fun getTime(_time: Long): String {
        var time = _time
        time *= 1000
        val now = System.currentTimeMillis()
        val msg = time

        val nowCalendar = Calendar.getInstance()

        msgCalendar.timeInMillis = time

        val calcMills = now - msg

        val calSeconds = calcMills / 1000

        if (calSeconds < 60) {
            return "刚刚"
        }

        val calMins = calSeconds / 60

        if (calMins < 60) {

            return StringBuilder().append(calMins).append("分钟前").toString()
        }

        val calHours = calMins / 60

        if (calHours < 24 && isSameDay(nowCalendar, msgCalendar)) {
            if (dayFormat == null)
                dayFormat = java.text.SimpleDateFormat("HH:mm", Locale.US)

            val result = dayFormat!!.format(msgCalendar!!.time)
            return StringBuilder().append("今天").append(" ").append(result).toString()

        }


        val calDay = calHours / 24

        if (calDay < 31) {
            if (isYesterday(nowCalendar, msgCalendar)) {
                if (dayFormat == null)
                    dayFormat = java.text.SimpleDateFormat("HH:mm", Locale.US)

                val result = dayFormat!!.format(msgCalendar!!.time)
                return StringBuilder("昨天").append(" ").append(result).toString()

            } else if (isTheDayBeforeYesterday(nowCalendar, msgCalendar)) {
                if (dayFormat == null)
                    dayFormat = java.text.SimpleDateFormat("HH:mm", Locale.US)

                val result = dayFormat!!.format(msgCalendar!!.time)
                return StringBuilder("前天").append(" ").append(result).toString()

            } else {
                if (dateFormat == null)
                    dateFormat = java.text.SimpleDateFormat(DATE_FORMAT, Locale.US)

                val result = dateFormat!!.format(msgCalendar!!.time)
                return StringBuilder(result).toString()
            }
        }

        val calMonth = calDay / 31

        if (calMonth < 12 && isSameYear(nowCalendar, msgCalendar)) {
            if (dateFormat == null)
                dateFormat = java.text.SimpleDateFormat(DATE_FORMAT, Locale.US)

            val result = dateFormat!!.format(msgCalendar!!.time)
            return StringBuilder().append(result).toString()

        }
        if (yearFormat == null)
            yearFormat = java.text.SimpleDateFormat(YEAR_FORMAT, Locale.US)
        val result = yearFormat!!.format(msgCalendar!!.time)
        return StringBuilder().append(result).toString()


    }

    private fun isSameHalfDay(now: Calendar, msg: Calendar): Boolean {
        val nowHour = now.get(Calendar.HOUR_OF_DAY)
        val msgHOur = msg.get(Calendar.HOUR_OF_DAY)

        if ((nowHour <= 12) and (msgHOur <= 12)) {
            return true
        } else return (nowHour >= 12) and (msgHOur >= 12)
    }

    private fun isSameDay(now: Calendar, msg: Calendar): Boolean {
        val nowDay = now.get(Calendar.DAY_OF_YEAR)
        val msgDay = msg.get(Calendar.DAY_OF_YEAR)

        return nowDay == msgDay
    }

    private fun isYesterday(now: Calendar, msg: Calendar): Boolean {
        val nowDay = now.get(Calendar.DAY_OF_YEAR)
        val msgDay = msg.get(Calendar.DAY_OF_YEAR)

        return nowDay - msgDay == 1
    }

    private fun isTheDayBeforeYesterday(now: Calendar, msg: Calendar): Boolean {
        val nowDay = now.get(Calendar.DAY_OF_YEAR)
        val msgDay = msg.get(Calendar.DAY_OF_YEAR)

        return nowDay - msgDay == 2
    }

    private fun isSameYear(now: Calendar, msg: Calendar): Boolean {
        val nowYear = now.get(Calendar.YEAR)
        val msgYear = msg.get(Calendar.YEAR)

        return nowYear == msgYear
    }
}
