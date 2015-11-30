package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.flash.FlashStatusDetector;

import java.text.SimpleDateFormat;

public class Logger
{
    public enum Level
    {
        INFO,
        WARNING,
        ERROR     //cause strategy failure
    }
    private static String getCurrentTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(System.currentTimeMillis());
    }
    public static void log(Level level, FlashStatusDetector.Status status, String message)
    {
        System.out.println(level + ": ["+ getCurrentTime() +"] [FlashStatus:" + status + "] : " + message);

    }
}
