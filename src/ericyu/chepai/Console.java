package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/28/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.flash.FlashStatusDetector;
import ericyu.chepai.recognize.Recognition;
import ericyu.chepai.robot.*;
import ericyu.chepai.robot.bidstrategy.AmbushAndAidStrategy;
import ericyu.chepai.robot.bidstrategy.AbstractBidStrategy;
import ericyu.chepai.robot.bidstrategy.User;
import ericyu.chepai.train.FlashStatusTrain;
import org.opencv.core.*;

import java.awt.*;

public class Console
{
    private static MyRobot robot;
    private static FlashStatusDetector flashStatusDetector;
    private static AbstractBidStrategy bidStrategy;
    private User user;

    public static void main(String[] args)
    {
        if (!init())
        {
            System.out.println("init failed!");
            return;
        }

        bidStrategy.execute();
    }

    private static boolean init()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("this line is for test encoding :" + MyRobot.NOTIFICATION_REQUEST_VCODE_TOO_OFTEN);

        //generate a robot
        try
        {
            User user = new User("12345678","123456");
            //will detect flash position here
            robot = new MyRobot(new Robot());
            bidStrategy = new AmbushAndAidStrategy(user,robot);
            flashStatusDetector = new FlashStatusDetector(new FlashPosition(),new Recognition(new FlashStatusTrain()));
            flashStatusDetector.addStatusObserver(robot);
            flashStatusDetector.addStatusObserver(bidStrategy);
            Thread detectorThread = new Thread(flashStatusDetector);
            detectorThread.start();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
