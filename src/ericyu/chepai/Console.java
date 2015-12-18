package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/28/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/
import ericyu.chepai.flash.FlashStatusDetector;
import ericyu.chepai.robot.MyRobot;
import ericyu.chepai.robot.bidstrategy.AbstractBidStrategy;
import ericyu.chepai.robot.bidstrategy.AmbushAndAidStrategy;
import ericyu.chepai.robot.bidstrategy.User;
import org.opencv.core.Core;

import java.awt.*;

public class Console
{

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
            if(args[0].equals("downloadResult"))
            {
                Logger.main(null);
            }
            else if (args[0].equals("testVCode") || args[0].equals("testLowestBid"))
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
            Thread detectorThread = new Thread(flashStatusDetector);
            detectorThread.start();

            robot = new MyRobot(new Robot());
            bidStrategy = new AmbushAndAidStrategy(user,robot);
            flashStatusDetector.addStatusObserver(robot);
            flashStatusDetector.addStatusObserver(bidStrategy);

            Thread lowestBidDetector = new Thread(robot.new LowestBidDetector());
            lowestBidDetector.start();

        }
        catch (AWTException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
