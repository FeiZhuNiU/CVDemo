package ericyu.chepai.robot.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 麟 on 2015/11/29.
 */
public class MyTime
{
    public static void main(String[] args)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,42);
        calendar.set(Calendar.SECOND,0);
        Date date1 = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,43);
        calendar.set(Calendar.SECOND,0);
        Date date2 = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,44);
        calendar.set(Calendar.SECOND,0);
        Date date3 = calendar.getTime();

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(df.format(System.currentTimeMillis()));
                System.out.println("hello!!!");
            }
        },date1);

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(df.format(System.currentTimeMillis()));
                System.out.println("hello!!! date3");
            }
        },date3);

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.exit(0);
            }
        },date2);
    }
}
