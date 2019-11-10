package com.finance.commonlib.utils;

import android.content.Context;
import android.text.TextUtils;

import com.finance.commonlib.R;
import com.finance.commonlib.utils.log.LogCat;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DateTimeUtils {
    private DateTimeUtils() {
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 根据字符串格式获取字符串日期
     *
     * @param dateTimePattern 字符串格式枚举
     * @return 字符串日期
     */
    public static String getStringDateTimeByStringPattern(DateTimePattern dateTimePattern) {
        String strDateTime = "";

        String strPattern = "yyyy-MM-dd HH:mm:ss";
        if (dateTimePattern.equals(DateTimePattern.LONG_DATETIME_1))
            strPattern = "yyyy-MM-dd HH:mm:ss";
        else if (dateTimePattern.equals(DateTimePattern.LONG_DATETIME_2))
            strPattern = "yyyy-MM-dd HH:mm";
        else if (dateTimePattern.equals(DateTimePattern.SHORT_DATETIME_1))
            strPattern = "yyyy-MM-dd";
        else if (dateTimePattern.equals(DateTimePattern.SHORT_DATETIME_2))
            strPattern = "yyyy-MM";

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(strPattern, Locale.CHINA);
        strDateTime = mSimpleDateFormat.format(new Date());

        return strDateTime;
    }

    /**
     * 秒转换为指定格式的日期
     *
     * @param second
     * @param patten
     * @return
     */
    public static String secondToDate(long second, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten, Locale.getDefault());
        return format.format(date);
    }

    public static String secondToDateMsg(long second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);
        Date msgDate = calendar.getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String msgDay = dayFormat.format(msgDate);
        String nowDay = dayFormat.format(getNow());
        if (TextUtils.equals(msgDay, nowDay)) {
            SimpleDateFormat format = new SimpleDateFormat("a hh:mm", Locale.getDefault());
            return format.format(msgDate);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return format.format(msgDate);
        }
    }

    public static String secondToPeriod(long second, Context context) {
        long days = second / (3600 * 24);
        long hours = second % (3600 * 24) / 3600;
        return context.getString(R.string.str_period, days, hours);
    }


    public enum DateTimePattern {
        /**
         * yyyy-MM-dd HH:mm:ss
         */
        LONG_DATETIME_1,
        /**
         * yyyy-MM-dd HH:mm
         */
        LONG_DATETIME_2,
        /**
         * yyyy-MM-dd
         */
        SHORT_DATETIME_1,
        /**
         * yyyy-MM
         */
        SHORT_DATETIME_2
    }

    /**
     * 获取当前日期
     *
     * @return 返回当前日期
     */
    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取当前日期的前一天
     *
     * @return 返回当前日期的前一天
     */
    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前日期
     *
     * @return 返回当前日期
     */
    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当前日期的后一天
     *
     * @return 返回当前日期的后一天
     */
    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定月份的第一天
     *
     * @param yyyymm 指定的月份串
     * @return 返回指定月份的第一天
     */
    public static Date getTheFirstDayOfMonth(String yyyymm) {
        String dateStr = yyyymm + "-01 00:00:00";
        return DateTimeUtils.getDateTime(dateStr);
    }

    /**
     * 获取系统月份的第一天
     * <p>
     * yyyymm 指定的月份串
     *
     * @return 返回系统月份的第一天
     */
    public static String getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        return dateFormat.format(calendar.getTime());
    }

    /**
     * 当前季度的开始时间，即2012-01-1 00:00:00
     *
     * @return 返回当前季度第一天
     */
    public static String getCurrentQuarterStartDay() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 3)
            c.set(Calendar.MONTH, 0);
        else if (currentMonth >= 4 && currentMonth <= 6)
            c.set(Calendar.MONTH, 3);
        else if (currentMonth >= 7 && currentMonth <= 9)
            c.set(Calendar.MONTH, 4);
        else if (currentMonth >= 10 && currentMonth <= 12)
            c.set(Calendar.MONTH, 9);
        c.set(Calendar.DATE, 1);
        return dateFormat.format(c.getTime());
    }

    /**
     * 当年开始时间
     *
     * @return 返回今年第一天
     */
    public static String getYearFirstDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        return dateFormat.format(c.getTime());
    }

    /**
     * 获取两个日其中较小的日期
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 返回两个日期中较小的日期
     */
    public static Date getSmallDate(Date date1, Date date2) {
        return date1.compareTo(date2) < 0 ? date1 : date2;
    }

    /**
     * 获取两个日其中较大的日期
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 返回两个日期中较大的日期
     */
    public static Date getBigDate(Date date1, Date date2) {
        return date1.compareTo(date2) > 0 ? date1 : date2;
    }

    /**
     * 在指定的日期上增加年数
     *
     * @param yearAmount 增加的年数
     * @param date       指定日期
     * @return 返回增加年数后的日期
     */
    public static Date addYear2Date(int yearAmount, Date date) {
        Date newDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, yearAmount);
            newDate = calendar.getTime();
        }
        return newDate;
    }

    /**
     * 在指定的日期上加上月份
     *
     * @param monthAmount 增加的月数
     * @param date        制定的日期
     * @return 返回增加月数后的日期
     */
    public static Date addMonth2Date(int monthAmount, Date date) {
        Date newDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, monthAmount);
            newDate = calendar.getTime();
        }
        return newDate;
    }

    /**
     * 在指定的日期上增加天数
     *
     * @param dayAmount 增加的天数
     * @param date      指定日期
     * @return 返回增加天数后的日期
     */
    public static Date addDay2Date(int dayAmount, Date date) {
        Date newDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, dayAmount);
            newDate = calendar.getTime();
        }
        return newDate;
    }

    /**
     * 根据给定的起始日期、结束日期获得它们之间的所有日期的List对象
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 返回所有日期的List对象
     */
    public static List<Date> getDateList(Date startDate, Date endDate) {
        List<Date> dateList = new ArrayList<Date>();
        int dayAmount = getDiffDays(startDate, endDate);
        Date newDate = null;
        for (int i = 0; i <= dayAmount + 1; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_MONTH, i);
            newDate = calendar.getTime();
            dateList.add(newDate);
        }
        return dateList;
    }

    /**
     * 在指定的日期上增加小时数
     *
     * @param hourAmount 小时数
     * @param date       指定日期
     * @return 返回增加小时数后的日期
     */
    public static Date addHour2Date(int hourAmount, Date date) {
        Date newDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, hourAmount);
            newDate = calendar.getTime();
        }
        return newDate;
    }

    /**
     * 在指定的日期上增加分钟数
     *
     * @param minuteAmount 分钟数
     * @param date         指定日期
     * @return 返回增加分钟数后的日期
     */
    public static Date addMinute2Date(int minuteAmount, Date date) {
        Date newDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, minuteAmount);
            newDate = calendar.getTime();
        }
        return newDate;
    }

    /**
     * 在指定的日期上增加秒数
     *
     * @param secondAmount 秒数
     * @param date         指定日期
     * @return 返回增加分钟数后的日期
     */
    public static Date addSecond2Date(int secondAmount, Date date) {
        Date newDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, secondAmount);
            newDate = calendar.getTime();
        }
        return newDate;
    }

    /**
     * 将日期转换成指定格式的字符串
     *
     * @param format 时间表现形式，例如："yyyy-MM-dd"，"yyyy-MM-dd HH:mm:ss"等
     * @param date   待格式化的日期
     * @return 返回格式化后的日期字符串
     */
    public static String formatDate(String format, Date date) {
        return formatDate(format, date, "");
    }

    /**
     * 将日期转换成指定格式的字符串
     *
     * @param format     时间表现形式，例如："yyyy-MM-dd"，"yyyy-MM-dd HH:mm:ss"等
     * @param date       待格式化的日期
     * @param nullString 空日期的替换字符，满足特殊需要
     * @return 返回格式化后的日期字符串
     */
    public static String formatDate(String format, Date date, String nullString) {
        String formatStr = nullString;

        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
            formatStr = simpleDateFormat.format(date);
        }

        return formatStr;
    }

    /**
     * 将日期转换成"yyyy-MM-dd HH:mm:ss"格式的字符串
     *
     * @param date 待格式化的日期
     * @return 返回格式化后的日期字符串
     */
    public static String formatDateTime(Date date) {
        String formatStr = "";

        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            formatStr = simpleDateFormat.format(date);
        }

        return formatStr;
    }

    /**
     * 将日期转换成" HH:mm"格式的字符串
     *
     * @param date 待格式化的日期
     * @return 返回格式化后的日期字符串
     */
    public static String formatTime(Date date) {
        String formatStr = "";

        if (null != date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            formatStr = simpleDateFormat.format(date);
        }

        return formatStr;
    }

    /**
     * 将字符串解析成年月日期类型，如果字符串含有/则按/分割,否则按-分割
     *
     * @param dateYMStr 待解析的字符串
     * @return 返回解析后的日期
     */
    public static Date getDateYM(String dateYMStr) {
        Date date = null;
        try {
            if (dateYMStr != null) {
                String separator = dateYMStr.indexOf('/') > 0 ? "/" : "-";
                DateFormat dateFormat = new SimpleDateFormat("yyyy" + separator + "MM", Locale.getDefault());
                date = dateFormat.parse(dateYMStr);
            }
        } catch (ParseException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }
        return date;
    }

    /**
     * 将字符串解析成年月日日期类型，如果字符串含有/则按/分割,否则按-分割
     *
     * @param dateStr 待解析的字符串
     * @return 返回解析后的日期
     */
    public static Date getDate(String dateStr) {
        Date date = null;
        try {
            if (dateStr != null) {
                String separator = dateStr.indexOf('/') > 0 ? "/" : "-";
                DateFormat dateFormat = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd",
                        Locale.getDefault());
                date = dateFormat.parse(dateStr);
            }
        } catch (ParseException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }
        return date;
    }

    /**
     * 将字符串解析成日期类型，格式自定
     *
     * @param dateStr   待解析的字符串
     * @param formatStr 日期格式
     * @return 返回解析后的日期
     */
    public static Date getDate(String dateStr, String formatStr) {
        Date date = null;
        try {
            if (dateStr != null) {
                DateFormat dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
                date = dateFormat.parse(dateStr);
            }
        } catch (ParseException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }
        return date;
    }

    /**
     * 将字符串解析成年月日时分秒日期时间类型，如果字符串含有/则按/分割,否则以-分
     *
     * @param dateTimeStr 待解析的字符串
     * @return 返回解析后的日期
     */
    public static Date getDateTime(String dateTimeStr) {
        Date date = null;
        try {
            String separator = dateTimeStr.indexOf('/') > 0 ? "/" : "-";
            DateFormat dateFormat = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd HH:mm:ss",
                    Locale.getDefault());
            date = dateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }
        return date;
    }

    /**
     * 获取传入日期的年份
     *
     * @param date 待解析的日期
     * @return 返回该日期的年份
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取传入日期的月份
     *
     * @param date 待解析的日期
     * @return 返回该日期的月份
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取传入日期月份的日
     *
     * @param date 待解析的日期
     * @return 返回该日期的日
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DATE);
    }

    /**
     * 两个日期的月份差
     *
     * @param fromDate 起始日期
     * @param toDate   结束日期
     * @return 返回两个日期的月份差，例1998-4-21~1998-6-21 相差2个月，返回2
     */
    public static int getDiffMonths(Date fromDate, Date toDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        int fromYear = c.get(Calendar.YEAR);
        int fromMonth = c.get(Calendar.MONTH) + 1;
        c.setTime(toDate);
        int toYear = c.get(Calendar.YEAR);
        int toMonth = c.get(Calendar.MONTH) + 1;
        int monthCount = 0;

        if (toYear == fromYear) {
            monthCount = toMonth - fromMonth;
        } else if (toYear - fromYear == 1) {
            monthCount = 12 - fromMonth + toMonth;
        } else {
            monthCount = 12 - fromMonth + 12 * (toYear - fromYear - 1) + toMonth;
        }
        return monthCount;
    }

    /**
     * 两个日期的天数差
     *
     * @param fromDate 起始日期
     * @param toDate   结束日期
     * @return 返回两个日期的天数差，例1998-4-21~1998-4-25 相差4天，返回4
     */
    public static int getDiffDays(Date fromDate, Date toDate) {
        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 两个日期的秒数差
     *
     * @param fromDate 起始日期
     * @param toDate   结束日期
     * @return 返回两个日期的差，结束日期减去起始日期
     */
    public static Long getDiff(Date fromDate, Date toDate) {
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(fromDate);

        Calendar toCal = Calendar.getInstance();
        toCal.setTime(toDate);
        Long diff = (toCal.getTime().getTime() - fromCal.getTime().getTime());
        return diff;
    }

    /**
     * 日期相减
     *
     * @param mdate 被减日期
     * @param sdate 减数日期
     * @return 1, 大于 0 等于 -1小于
     */
    public static int dateCompare(Date mdate, Date sdate) {
        // 被减数为null
        if (null == mdate) {
            // 减数也为null ，则视为相等
            if (null == sdate) {
                return 0;
            }
            // 否则视作小于
            else {
                return -1;
            }
        }
        // 减数为null
        else if (null == sdate) {
            // 视作大于
            return 1;
        }
        // 都不为null则比较时间差
        else {
            Long diff = getDiff(sdate, mdate);
            if (diff > 0) {
                return 1;
            } else if (diff == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    /**
     * 日期相减
     * <p>
     * 减数日期
     *
     * @return 1, 大于 0 等于 -1小于
     */
    public static int compareDate(String date1, String date2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 两个日期的秒数差
     *
     * @param fromDate 起始日期
     * @param toDate   结束日期
     * @return 返回两个日期的秒数差，例1998-4-21 10:00:00~1998-4-21 10:00:50 相差50秒，返回50
     */
    public static Long getDiffSeconds(Date fromDate, Date toDate) {
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(fromDate);
        fromCal.set(Calendar.MILLISECOND, 0);

        Calendar toCal = Calendar.getInstance();
        toCal.setTime(toDate);
        toCal.set(Calendar.MILLISECOND, 0);
        Long diff = (toCal.getTime().getTime() - fromCal.getTime().getTime()) / 1000;
        return diff;
    }

    /**
     * 获取一个星期中的第几天，周日算第一天
     *
     * @param date 待解析的日期
     * @return 返回一个星期中的第几天
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    /**
     * 获取一个星期中的第几天，周一算第一天
     *
     * @param date 待解析的日期
     * @return 返回一个星期中的第几天
     */
    public static int getChinaDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (1 == dayOfWeek) {
            dayOfWeek = 8;
        }
        return dayOfWeek - 1;
    }

    /**
     * 获取一个月中的第几天，一个月中第一天的值为1
     *
     * @param date 待解析的日期
     * @return 返回一个月中的第几天
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth;
    }

    /**
     * 获取当前时间的时间戳，精确到毫秒
     *
     * @return 返回当前时间的时间戳
     */
    public static Long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间的时间戳，精确到秒
     *
     * @return 返回当前时间的时间戳
     */
    public static long currentTimeSecond() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取某日的0时0分0秒的Date对象
     *
     * @param datetime 待解析的日期
     * @return 传入日期的0时0分0秒的Date对象
     */
    public static Date getDayStart(Date datetime) {
        if (null == datetime) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取某日的23时59分59秒的Date对象
     *
     * @param datetime 待解析的日期
     * @return 传入日期的23时59分59秒的Date对象
     */
    public static Date getDayEnd(Date datetime) {
        if (null == datetime) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取某月第一天的0时0分0秒的Date对象
     *
     * @param datetime 待解析的日期
     * @return 指定日期所在月份的第一天时间
     */
    public static Date getMonthDayStart(Date datetime) {
        if (null == datetime) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取某月最后一天 Create Time May 3, 2012 1:37:53 PM
     *
     * @param datetime
     * @return
     */
    public static Date getMonthDayEnd(Date datetime) {
        if (null == datetime) {
            return null;
        }
        Date date = datetime;
        date = addMonth2Date(1, date);
        date = getMonthDayStart(date);
        date = addDay2Date(-1, date);
        return date;
    }

    /**
     * 由Timestamp类型对象转换成Date类型对象
     *
     * @param timestamp Timestamp类型对象
     * @return Date型日期对象
     */
    public static Date transToDate(Timestamp timestamp) {
        if (timestamp != null) {
            return new Date(timestamp.getTime());
        }
        return null;
    }

    /**
     * 遍历指定年月，以周为循环生成一个6*7的二维数组，空闲位为Null
     *
     * @param year  指定年
     * @param month 指定月
     * @return Date数组对象
     */
    public static Date[][] makeCalendar(int year, int month) {
        Date[][] dateArray = new Date[6][7];
        // Object[][]
        // 指定年月的第一天
        Date date = DateTimeUtils.getDate(year + "-" + month + "-01");
        // 次月的第一天
        Date lastDate = DateTimeUtils.addMonth2Date(1, date);
        // 第一天是周几
        int firstDayWeek = DateTimeUtils.getDayOfWeek(date);
        // 将星期日处理为一周的最后一天
        if (1 == firstDayWeek) {
            firstDayWeek = 8;
        }
        int row = 0;
        int col = firstDayWeek - 2;
        // 遍历一个月，以周为循环生成二维数组
        while (DateTimeUtils.getDiffDays(date, lastDate) > 0) {
            if (col > 6) {
                row = row + 1;
                col = 0;
            }
            dateArray[row][col] = date;
            date = DateTimeUtils.addDay2Date(1, date);
            col++;
        }

        return dateArray;
    }

    /**
     * 根据给定的开始、结束日期，以周为循环生成一个n*7列的二维数组，空闲位为Null
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return Date[][]
     */
    public static Date[][] makeCalendar(Date startDate, Date endDate) {
        int n = getDiffDays(startDate, endDate) / 7 + 1;
        Date[][] dateArray = new Date[n][7];
        // 开始日期是周几
        int firstDayWeek = DateTimeUtils.getDayOfWeek(startDate);

        // 将星期日处理为一周的最后一天
        if (1 == firstDayWeek) {
            firstDayWeek = 8;
        }
        int row = 0;
        int col = firstDayWeek - 2;
        // 遍历开始日期和结束日期之间的所有日期（包括开始日期和结束日期），以周为循环生成二维数组
        while (DateTimeUtils.getDiffDays(startDate, endDate) >= 0) {
            if (col > 6) {
                row = row + 1;
                col = 0;
            }
            dateArray[row][col] = startDate;
            startDate = DateTimeUtils.addDay2Date(1, startDate);
            col++;
        }

        return dateArray;
    }

    /**
     * 获取指定年份的休息日列表
     *
     * @param year 指定年
     * @return 指定年份的休息日列表
     */
    public static List<Date> getWeekEndList(Integer year) {
        Date date = DateTimeUtils.getDate(year + "-01-01");
        // 次年的第一天
        Date lastDate = DateTimeUtils.addYear2Date(1, date);
        List<Date> weekendList = new ArrayList<Date>();
        while (DateTimeUtils.getDiffDays(date, lastDate) > 0) {
            int dayOfweek = DateTimeUtils.getChinaDayOfWeek(date);
            if (6 == dayOfweek || 7 == dayOfweek) {
                // System.out.println(DateTimeUtils.formatDate("yyyy-mMM-dd",
                // date) +
                // ":" + dayOfweek);
                weekendList.add(date);
            }
            date = DateTimeUtils.addDay2Date(1, date);
        }
        return weekendList;
    }

    /**
     * 获取指定年份的休息日列表
     *
     * @param year 指定年
     * @return 指定年份的休息日数组
     */
    public static List<Date> getDayList(Integer year) {
        Date date = DateTimeUtils.getDate(year + "-01-01");
        // 次年的第一天
        Date lastDate = DateTimeUtils.addYear2Date(1, date);
        List<Date> dayList = new ArrayList<Date>();
        while (DateTimeUtils.getDiffDays(date, lastDate) > 0) {
            dayList.add(date);
            date = DateTimeUtils.addDay2Date(1, date);
        }
        return dayList;
    }

    public static String getCurrentDateTimeString() {
        String currentTime = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Calendar currentDate = Calendar.getInstance();
            currentTime = format.format(currentDate.getTime());
        } catch (Exception e) {
            return null;
        }
        return currentTime;
    }

    public static Date StringToDatetime(String strDatetime) {
        DateFormat tempformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        try {
            if (strDatetime == null || strDatetime.equals("null")) {
                strDatetime = "0000-00-00 00:00:00";
            }
            date = tempformat.parse(strDatetime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getCurrentDateTime() {
        String currentTime = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault());
            Calendar currentDate = Calendar.getInstance();
            currentTime = format.format(currentDate.getTime());
        } catch (Exception e) {
            return null;
        }
        return currentTime;
    }

}
