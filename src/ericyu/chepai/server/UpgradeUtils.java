package ericyu.chepai.server;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 1/20/2016  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.chepai.CommandConstants;
import ericyu.chepai.FlashConstants;
import ericyu.chepai.utils.FileUtils;
import ericyu.chepai.recognition.SampleConstants;

import java.io.File;

public class UpgradeUtils
{
    public final static String LOG_BUCKET_NAME                  = "paipailog";
    public final static String CONFIG_PATCH_BUCKET_NAME         = "propertiespatch";
    public final static String RESOURCES_PATCH_BUCKET_NAME      = "resourcespatch";

    private final static String PATCH_CONFIG_DIR = "patch" + File.separator + "config";
    private final static String PATCH_RESOURCE_DIR = "patch" + File.separator + "resources";
    final static String DIR_TO_SAVE_LOGS_FROM_SERVER    = "server";

    public static void uploadPatch()
    {
        ServerUtils.deleteAllDataInBucket(CONFIG_PATCH_BUCKET_NAME);
        ServerUtils.deleteAllDataInBucket(RESOURCES_PATCH_BUCKET_NAME);
        ServerUtils.sendFileToBucket(new File(FlashConstants.FLASH_CONFIG_FILE), CONFIG_PATCH_BUCKET_NAME);
        ServerUtils.sendFileToBucket(new File(SampleConstants.TRAIN_DATA_DIR), RESOURCES_PATCH_BUCKET_NAME);
    }

    public static void upgrade()
    {
        fetchPatch();
        // upgrade flash.flashProperties

        File[] files = new File(PATCH_CONFIG_DIR).listFiles();
        for(File file : files)
        {
            FileUtils.copyFile(file, new File(file.getName()));
        }

        // upgrade resources
        files = new File(PATCH_RESOURCE_DIR).listFiles();
        FileUtils.mkDirIfNotExists(new File(SampleConstants.TRAIN_DATA_DIR));
        for(File file : files)
        {
            FileUtils.copyFile(file, new File(SampleConstants.TRAIN_DATA_DIR + File.separator + file.getName()));
        }
    }

    private static void fetchPatch()
    {
        FileUtils.removeFile(new File(PATCH_CONFIG_DIR));
        FileUtils.removeFile(new File(PATCH_RESOURCE_DIR));

        FileUtils.mkDirIfNotExists(new File(PATCH_CONFIG_DIR));
        FileUtils.mkDirIfNotExists(new File(PATCH_RESOURCE_DIR));

        ServerUtils.getAllDataFromBucket(CONFIG_PATCH_BUCKET_NAME, PATCH_CONFIG_DIR);
        ServerUtils.getAllDataFromBucket(RESOURCES_PATCH_BUCKET_NAME, PATCH_RESOURCE_DIR);
    }

    public static void main(String[] args)
    {
        if(args.length == 1)
        {
            if (args[0].equals(CommandConstants.UPLOAD_PATCH))
            {
                uploadPatch();
            }
            else if (args[0].equals(CommandConstants.UPGRADE))
            {
                upgrade();
            }
            else if (args[0].equals(CommandConstants.DOWNLOAD_LOGS_FROM_SERVER))
            {
                try
                {
                    ServerUtils.getAllDataFromBucket(UpgradeUtils.LOG_BUCKET_NAME, DIR_TO_SAVE_LOGS_FROM_SERVER);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (args[0].equals(CommandConstants.DELETE_LOGS_ON_SERVER))
            {
                ServerUtils.deleteAllDataInBucket(UpgradeUtils.LOG_BUCKET_NAME);
            }
            else
            {
//            System.out.println(getObjectsInfo(CONFIG_PATCH_BUCKET_NAME));
//            deleteAllDataInBucket(CONFIG_PATCH_BUCKET_NAME);
//                ServerUtils.deleteAllDataInBucket(UpgradeUtils.LOG_BUCKET_NAME);
            }
        }



    }


}
