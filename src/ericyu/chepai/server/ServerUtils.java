package ericyu.chepai.server;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 1/20/2016  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import ericyu.chepai.Logger;

import java.io.*;

public class ServerUtils
{
    private static String accessKeyId = "Ndg6XzPda5JTNFpa";
    private static String accessKeySecret = "qHZ3Q5oJCCMv9Xrfwr2b6KVjwre0Zc";
    private static String endPoint  = "http://oss-cn-shanghai.aliyuncs.com";
    private static String bucketName = "feizhuniu";
    private static String dirToSaveDataFromServer="server";

    static {
        if (!new File(dirToSaveDataFromServer).exists())
        {

        }
    }

    public static void sendToDataServer(File file)
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
            OSSObject object = client.getObject(bucketName,ossObjectSummary.getKey());
            ObjectMetadata meta = object.getObjectMetadata();
            InputStream objectContent = object.getObjectContent();
            OutputStream os = null;
            try
            {
                os = new FileOutputStream(dirToSaveDataFromServer + File.separator + object.getKey());
                byte[] buffer = new byte[10];
                while((objectContent.read(buffer))!=-1){
                    os.write(buffer);
                }
            }
            catch (Exception e)
            {
                Logger.log(Logger.Level.WARNING, null, "something is wrong when get data from server!", e);
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(os != null)
                        os.close();
                    objectContent.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * be careful to do this!!!
     */
    public static void deleteAllDataOnServer()
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
        if (args.length == 1)
        {
            if (args[0].equals("downloadResult"))
            {
                try
                {
                    getAllDataFromServer();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (args[0].equals("deleteDataOnServer"))
            {
                deleteAllDataOnServer();
            }
        }
    }
}
