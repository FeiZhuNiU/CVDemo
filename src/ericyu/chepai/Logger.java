package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import ericyu.chepai.flash.FlashStatusDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Logger
{

    private static String accessKeyId = "Ndg6XzPda5JTNFpa";
    private static String accessKeySecret = "qHZ3Q5oJCCMv9Xrfwr2b6KVjwre0Zc";
    private static String endPoint  = "http://oss-cn-shanghai.aliyuncs.com";
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

    private static String bucketName = "feizhuniu";

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

    /**
     * dump history at the end
     */
    private static void dumpHistory()
    {
        FileWriter writer = null;
        try
        {
            String preline = "";
            writer = new FileWriter(new File(logFile));
            for(String log : history)
            {
                if(!log.equals(preline))
                {
                    writer.write(log + "\n");
                }
                preline = log;
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
        dumpHistory();
        sendToDataServer(new File(logFile));
    }
    private static void sendToDataServer(File file)
    {
        OSSClient client = new OSSClient(endPoint,accessKeyId,accessKeySecret);

        PutObjectResult result = client.putObject(bucketName, file.getName(), file);
        System.out.println(result.getETag());

    }

    public static void getAllDataFromServer()
    {
        OSSClient client = new OSSClient(endPoint,accessKeyId,accessKeySecret);

        ObjectListing listing = client.listObjects(bucketName);
        for(OSSObjectSummary ossObjectSummary : listing.getObjectSummaries())
        {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, ossObjectSummary.getKey());
            ObjectMetadata objectMetadata = client.getObject(getObjectRequest,new File("dataFromServer_" + ossObjectSummary.getKey() + ".txt"));
        }
    }

    public static void deleteAllData(String bucketName)
    {
        OSSClient client = new OSSClient(endPoint,accessKeyId,accessKeySecret);
        ObjectListing listing = client.listObjects(bucketName);

        for(OSSObjectSummary ossObjectSummary : listing.getObjectSummaries())
        {
            client.deleteObject(bucketName,ossObjectSummary.getKey());
        }
    }

    public static void main(String[] args)
    {
        deleteAllData(bucketName);
    }
}
