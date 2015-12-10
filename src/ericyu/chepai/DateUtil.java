package ericyu.chepai;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.*;

/**
 * Created by 麟 on 2015/11/29.
 */
public class DateUtil
{
    public static String getCurrentTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(System.currentTimeMillis());
    }

    public static String getCurrentTimeForFileName()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(System.currentTimeMillis());
    }

    public static String formatLongValueToDate(long val)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(val);
    }

    public static int getCurrentHour()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMinute()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }


    public static long getDateLongValue(int hour, int minute, int second)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);
        Date date = calendar.getTime();
        return date.getTime();
    }

    public static void main(String[] args)
    {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.MINUTE));
        System.out.println(calendar.get(Calendar.SECOND));

    }
}
