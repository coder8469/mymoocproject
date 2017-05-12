package com.mall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Administrator on 2017-5-1.
 */
public class DateTimeUtil {
    //如果不知道M H为啥要大写，请阅：https://www.zhihu.com/question/23730083
    private static String STANDART_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //joda-time
    //str --> Date
    //Date -- > str
    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDART_FORMAT);
    }

    public static Date strToDate(String dateStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDART_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateStr);
        return dateTime.toDate();
    }
    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateStr);
        return dateTime.toDate();
    }
}
