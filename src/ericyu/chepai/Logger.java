package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.flash.FlashStatusDetector;


public class Logger
{
    public enum Level
    {
        INFO,
        WARNING,
        ERROR     //cause strategy failure
    }

    public static void log(Level level, FlashStatusDetector.Status status, String message)
    {
        System.out.println(level + ": ["+ Date.getCurrentTime() +"] [FlashStatus:" + status + "] : " + message);
    }
}
