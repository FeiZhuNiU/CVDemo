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
import java.util.List;


public class Logger
{

    private static String accessKeyId = "Ndg6XzPda5JTNFpa";
    private static String accessKeySecret = "qHZ3Q5oJCCMv9Xrfwr2b6KVjwre0Zc";
    private static String endPoint  = "http://oss-cn-shanghai.aliyuncs.com";

    private static String bucketName = "feizhuniu";

    private Level level;
    private FlashStatusDetector.Status status;
    private String message;

    public Logger(Level level, FlashStatusDetector.Status status, String message)
    {
        this.level = level;
        this.status = status;
        this.message = message;
    }

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
        getAllDataFromServer();
    }
}
