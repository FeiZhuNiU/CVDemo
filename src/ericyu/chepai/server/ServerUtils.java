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
import ericyu.chepai.CommandConstants;
import ericyu.chepai.FileUtils;
import ericyu.chepai.Logger;

import java.io.*;

public class ServerUtils
{
    private final static String ACCESS_KEY_ID                   = "Ndg6XzPda5JTNFpa";
    private final static String ACCESS_KEY_SECRET               = "qHZ3Q5oJCCMv9Xrfwr2b6KVjwre0Zc";
    private final static String ENDPOINT                        = "http://oss-cn-shanghai.aliyuncs.com";

    public final static String LOG_BUCKET_NAME                  = "paipailog";
    public final static String PROPERTIESPATCH_BUCKET_NAME      = "propertiespatch";
    public final static String RESOURCESPATCH_BUCKET_NAME       = "resourcespatch";

    private final static String DIR_TO_SAVE_LOGS_FROM_SERVER    = "server";
    public final static String META_LAST_MODIFIED_TIME          = "last_modified_time";

    static {
        if (!new File(DIR_TO_SAVE_LOGS_FROM_SERVER).exists())
        {
            FileUtils.mkDirIfNotExists(new File(DIR_TO_SAVE_LOGS_FROM_SERVER));
        }
    }

    /**
     *
     * @param file
     * @param bucketName
     */
    public static void sendFileToBucket(File file, String bucketName)
    {
        if(file.isFile())
        {
            OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            ObjectMetadata meta = new ObjectMetadata();
            meta.addUserMetadata(META_LAST_MODIFIED_TIME, String.valueOf(file.lastModified()));
            PutObjectResult result = client.putObject(bucketName, file.getName(), file);
            System.out.println(result.getETag());
        }else
        {
            File[] files = file.listFiles();
            for(File f : files)
            {
                sendFileToBucket(f, bucketName);
            }
        }
    }


    /**
     *
     * @param bucketName
     * @param dir           "" and null indicate current path
     */
    public static void getAllDataFromBucket(String bucketName, String dir)
    {
        OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        ObjectListing listing = client.listObjects(bucketName);
        for(OSSObjectSummary ossObjectSummary : listing.getObjectSummaries())
        {
            OSSObject object = client.getObject(bucketName,ossObjectSummary.getKey());
            ObjectMetadata meta = object.getObjectMetadata();
            InputStream objectContent = object.getObjectContent();
            OutputStream os = null;
            try
            {
                dir = (dir == null || dir.trim().equals("")) ? object.getKey() : dir + File.separator + object.getKey();
                os = new FileOutputStream(dir);
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
    public static void deleteAllDataInBucket(String bucketName)
    {
        OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
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
            if (args[0].equals(CommandConstants.DOWNLOAD_LOGS_FROM_SERVER))
            {
                try
                {
                    getAllDataFromBucket(LOG_BUCKET_NAME, DIR_TO_SAVE_LOGS_FROM_SERVER);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (args[0].equals(CommandConstants.DELETE_LOGS_ON_SERVER))
            {
                deleteAllDataInBucket(LOG_BUCKET_NAME);
            }
        }
    }
}
