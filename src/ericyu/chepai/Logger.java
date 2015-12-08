package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.aliyun.oss.OSSClient;
import ericyu.chepai.flash.FlashStatusDetector;


public class Logger
{

    private static String accessKeyId = "Ndg6XzPda5JTNFpa";
    private static String accessKeySecret = "qHZ3Q5oJCCMv9Xrfwr2b6KVjwre0Zc";
    private static String endPoint  = "feizhuniu.oss-cn-shanghai.aliyuncs.com";

    public enum Level
    {
        INFO,
        WARNING,
        ERROR     //cause strategy failure
    }

    public static void log(Level level, FlashStatusDetector.Status status, String message)
    {
        System.out.println(level + ": ["+ DateUtil.getCurrentTime() +"] [FlashStatus:" + status + "] : " + message);
//        sendToDataServer(level,status,message);
    }

    private static void sendToDataServer(Level level, FlashStatusDetector.Status status, String message)
    {
        OSSClient client = new OSSClient(endPoint,accessKeyId,accessKeySecret);

    }
}
