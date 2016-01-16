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
import ericyu.chepai.robot.bidstrategy.User;

import java.io.*;
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
        sendToDataServer(new File(logFile));
    }

    private static void sendToDataServer(File file)
    {
        OSSClient client = new OSSClient(endPoint,accessKeyId,accessKeySecret);

        PutObjectResult result = client.putObject(bucketName, file.getName(), file);
        System.out.println(result.getETag());

    }

    public static void getAllDataFromServer() throws Exception
    {
        OSSClient client = new OSSClient(endPoint,accessKeyId,accessKeySecret);

        ObjectListing listing = client.listObjects(bucketName);
        for(OSSObjectSummary ossObjectSummary : listing.getObjectSummaries())
        {
            OSSObject object = client.getObject(bucketName,ossObjectSummary.getKey());
            ObjectMetadata meta = object.getObjectMetadata();
            InputStream objectContent = object.getObjectContent();
            OutputStream os = new FileOutputStream("C:\\" + object.getKey());
            byte[] buffer = new byte[10];
            while((objectContent.read(buffer))!=-1){
                os.write(buffer);
            }
            os.close();

            objectContent.close();

        }
    }

    public static void deleteAllDataOnServer(String bucketName)
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
//        deleteAllDataOnServer(bucketName);
        try
        {
            getAllDataFromServer();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
