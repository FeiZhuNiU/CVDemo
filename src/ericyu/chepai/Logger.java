package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.server.ServerUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Logger
{
    private static String logFile;
    static
    {
        try
        {
            logFile = InetAddress.getLocalHost().getHostName() + DateUtil.getCurrentTimeForFileName()+"_BidLog.txt";
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    private static List<String> history = new ArrayList<>();

    public enum Level
    {
        INFO,
        WARNING,
        ERROR     //cause strategy failure
    }

    public static void log(Level level, FlashStatusDetector.Status status, String message)
    {
        final String log = level + ": ["+ DateUtil.getCurrentTime() +"] [FlashStatus:" + status + "] : " + message;
        System.out.println(log);
        history.add(log);
    }

    public static void log(Level level, FlashStatusDetector.Status status, String message, Exception e)
    {
        log(level, status, message + "\n" + e.getMessage() + "\n");
    }

    /**
     * dump history to local file system
     */
    private static void dumpHistory()
    {
        FileWriter writer = null;
        try
        {
            writer = new FileWriter(new File(logFile));
            List<String> historyCur = new ArrayList<>(history);
            for(String log : historyCur)
            {
                writer.write(log + "\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(writer!=null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendLog()
    {
        Logger.log(Level.INFO, null, "dumping sending log to server");
        logFile = Console.getUser().getUsername() + "_" + Console.getUser().getPassword() + "_" + DateUtil.getCurrentTimeForFileName()+"_BidLog.txt";
        dumpHistory();
        ServerUtils.sendFileToBucket(new File(logFile), ServerUtils.LOG_BUCKET_NAME);
    }

}
