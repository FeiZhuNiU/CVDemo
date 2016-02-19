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
import java.util.HashMap;
import java.util.Map;

public class ServerUtils
{
    private final static String ACCESS_KEY_ID                   = "Ndg6XzPda5JTNFpa";
    private final static String ACCESS_KEY_SECRET               = "qHZ3Q5oJCCMv9Xrfwr2b6KVjwre0Zc";
    private final static String ENDPOINT                        = "http://oss-cn-shanghai.aliyuncs.com";

    public final static String LOG_BUCKET_NAME                  = "paipailog";
    public final static String CONFIG_PATCH_BUCKET_NAME         = "propertiespatch";
    public final static String RESOURCES_PATCH_BUCKET_NAME      = "resourcespatch";

    private final static String DIR_TO_SAVE_LOGS_FROM_SERVER    = "server";
    public final static String META_LAST_MODIFIED_TIME          = "last_modified_time";

    static {
        if (!new File(DIR_TO_SAVE_LOGS_FROM_SERVER).exists())
        {
            FileUtils.mkDirIfNotExists(new File(DIR_TO_SAVE_LOGS_FROM_SERVER));
        }
    }

    /**
     * TODO: verify before send, if the file is identical with the one on server, skip sending
     * @param file
     * @param bucketName
     */
    public static void sendFileToBucket(File file, String bucketName)
    {
        if(file.isFile())
        {
            OSSClient client = getOssClient();

//            ObjectMetadata meta = new ObjectMetadata();
//
//            meta.addUserMetadata(META_LAST_MODIFIED_TIME, String.valueOf(file.lastModified()));


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

    private static OSSClient getOssClient()
    {
        return new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }




    private static Map<String,String> getObjectsInfo(String bucketName)
    {
        Map<String, String> infos = new HashMap<>();
        OSSClient client = getOssClient();
        ObjectListing listing = client.listObjects(bucketName);
        for(OSSObjectSummary ossObjectSummary : listing.getObjectSummaries())
        {
            OSSObject object = client.getObject(bucketName,ossObjectSummary.getKey());
            infos.put(ossObjectSummary.getKey(), object.getObjectMetadata().getUserMetadata().get(META_LAST_MODIFIED_TIME));
        }
        return infos;
    }

    /**
     *
     * @param bucketName
     * @param dir           "" and null indicate current path
     */
    public static void getAllDataFromBucket(String bucketName, String dir)
    {
        OSSClient client = getOssClient();
        ObjectListing listing = client.listObjects(bucketName);
        for(OSSObjectSummary ossObjectSummary : listing.getObjectSummaries())
        {
            OSSObject object = client.getObject(bucketName,ossObjectSummary.getKey());
            ObjectMetadata meta = object.getObjectMetadata();
            InputStream objectContent = object.getObjectContent();
            OutputStream os = null;
            try
            {
                String dst = (dir == null || dir.trim().equals("")) ? object.getKey() : dir + File.separator + object.getKey();
                os = new FileOutputStream(dst);
                byte[] buffer = new byte[10];
                int i = 0;
                while( (i=objectContent.read(buffer))!=-1)
                {
                    os.write(buffer,0,i);
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
        OSSClient client = getOssClient();
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
        else
        {
//            System.out.println(getObjectsInfo(CONFIG_PATCH_BUCKET_NAME));
            deleteAllDataInBucket(CONFIG_PATCH_BUCKET_NAME);
        }
    }
}
