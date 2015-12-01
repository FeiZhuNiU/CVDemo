package ericyu.chepai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.*;

import static java.lang.Thread.*;

/**
 * Created by 麟 on 2015/11/29.
 */
public class Date
{
    public  static String getCurrentTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(System.currentTimeMillis());
    }

    public static void main(String[] args)
    {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY,23);
//        calendar.set(Calendar.MINUTE,42);
//        calendar.set(Calendar.SECOND,0);
//        Date date1 = calendar.getTime();
//
//        calendar.set(Calendar.HOUR_OF_DAY,23);
//        calendar.set(Calendar.MINUTE,43);
//        calendar.set(Calendar.SECOND,0);
//        Date date2 = calendar.getTime();
//
//        calendar.set(Calendar.HOUR_OF_DAY,23);
//        calendar.set(Calendar.MINUTE,44);
//        calendar.set(Calendar.SECOND,0);
//        Date date3 = calendar.getTime();
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                System.out.println(df.format(System.currentTimeMillis()));
//                System.out.println("hello!!!");
//            }
//        },date1);
//
//        timer.schedule(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                System.out.println(df.format(System.currentTimeMillis()));
//                System.out.println("hello!!! date3");
//            }
//        },date3);
//
//        timer.schedule(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                System.exit(0);
//            }
//        },date2);
        final long start = System.currentTimeMillis();
//        ScheduledExecutorService myScheduledThreadPool = Executors.newScheduledThreadPool(2);
        ExecutorService myScheduledThreadPool = Executors.newSingleThreadExecutor();


        Callable<String> task1 = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                System.out.println("in task1  " + (System.currentTimeMillis() - start));
                Thread.sleep(1000);
                return "return task1";
            }
        };
        Callable<String> task2 = new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {

                System.out.println("in task2  " + (System.currentTimeMillis() - start));
                return "return task2";
            }
        };
        ArrayList<Future<String>> results = new ArrayList<>();
        results.add(myScheduledThreadPool.submit(task1));
        results.add(myScheduledThreadPool.submit(task2));
        results.add(myScheduledThreadPool.submit(task1));

        try
        {
            if((results.get(0).isDone()) && results.get(0).get().equals("return task1"))
            {
                System.out.println("haha");
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }


//        myScheduledThreadPool.schedule(task1,1000, TimeUnit.MILLISECONDS);
//        myScheduledThreadPool.schedule(task3,2000, TimeUnit.MILLISECONDS);
//        myScheduledThreadPool.schedule(task2,3000, TimeUnit.MILLISECONDS);
//        myScheduledThreadPool.schedule(task4,2000, TimeUnit.MILLISECONDS);

    }
}
