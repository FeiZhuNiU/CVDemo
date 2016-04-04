package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/28/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/
import ericyu.chepai.robot.MyRobot;
import ericyu.chepai.robot.bidstrategy.AbstractBidStrategy;
import ericyu.chepai.robot.bidstrategy.AmbushAndAidStrategy;
import ericyu.chepai.robot.bidstrategy.User;
import ericyu.chepai.server.UpgradeUtils;
import ericyu.chepai.utils.DateUtil;
import ericyu.chepai.utils.Logger;
import org.opencv.core.Core;

import java.awt.*;

public class Console
{

    private static long exitTime;
    static {
        exitTime = DateUtil.getDateLongValue(Configuration.exitTimeHour, Configuration.exitTimeMinute,3);
        Logger.log(Logger.Level.INFO, null, "Program will shutdown at " + DateUtil.formatLongValueToDate(exitTime));
        Logger.log(Logger.Level.INFO, null, exitTime - System.currentTimeMillis() + "ms to go");
    }

    private static MyRobot robot;
    private static AbstractBidStrategy bidStrategy;
    private static FlashStatusDetector flashStatusDetector;

    public static User getUser()
    {
        return user;
    }

    private static User user;

    public static void main(String[] args)
    {
        if(args.length == 1)
        {
            if(args[0].equals(CommandConstants.DOWNLOAD_LOGS_FROM_SERVER))
            {
                UpgradeUtils.main(args);
            }
            else if (
                    args[0].equals(CommandConstants.TEST_VCODE) ||
                    args[0].equals(CommandConstants.TEST_LOWEST_BID) ||
                    args[0].equals(CommandConstants.TEST_REFRESH_BUTTON))
            {
                try
                {
                    MyRobot.main(args);
                }
                catch (AWTException e)
                {
                    e.printStackTrace();
                }
            }
            else if (
                    args[0].equals(CommandConstants.UPLOAD_PATCH) ||
                    args[0].equals(CommandConstants.UPGRADE))
            {
                UpgradeUtils.main(args);
            }

        }
        else if (args.length == 0)
        {

            if (!init())
            {
                Logger.log(Logger.Level.ERROR, flashStatusDetector.getStatus(), "init failed!");
                return;
            }
            bidStrategy.execute();
            // for test
//            bidStrategy.printResult();
        }

    }

    private static boolean init()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Logger.log(Logger.Level.INFO, null, "this line is for test encoding :" + MyRobot.NOTIFICATION_REQUEST_VCODE_TOO_OFTEN);

        try
        {
            user = new User(Configuration.username, Configuration.password);

//            Thread flashPositionDetector = new Thread(new FlashPosition.FlashPositionDetector());
//            flashPositionDetector.start();
//            FlashPosition.setOrigin();
            flashStatusDetector = new FlashStatusDetector();
            robot = new MyRobot(new Robot());
            bidStrategy = new AmbushAndAidStrategy(user,robot);
            flashStatusDetector.addStatusObserver(robot);
            flashStatusDetector.addStatusObserver(bidStrategy);

            Thread exitThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        if (System.currentTimeMillis() > exitTime)
                        {
                            Logger.sendLog();
                            System.exit(1);
                        }
                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            exitThread.start();

            Thread detectorThread = new Thread(flashStatusDetector);
            detectorThread.start();

            Thread lowestBidDetector = new Thread(robot.new LowestBidDetector());
            lowestBidDetector.start();

        }
        catch (AWTException e)
        {
            Logger.log(Logger.Level.ERROR, null, "console init failed!", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
