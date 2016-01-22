package ericyu.chepai.server;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 1/20/2016  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.chepai.CommandConstants;
import ericyu.chepai.Configuration;
import ericyu.chepai.FileUtils;
import ericyu.chepai.train.SampleConstants;

import java.io.File;

public class UpgradeUtils
{
    private final static String PATCH_CONFIG_DIR = "patch" + File.separator + "config";
    private final static String PATCH_RESOURCE_DIR = "patch" + File.separator + "resources";

    static {
        FileUtils.mkDirIfNotExists(new File(PATCH_CONFIG_DIR));
        FileUtils.mkDirIfNotExists(new File(PATCH_RESOURCE_DIR));
    }

    public static void uploadPatch()
    {
        ServerUtils.sendFileToBucket(new File(Configuration.CONFIG_FILE), ServerUtils.CONFIG_PATCH_BUCKET_NAME);
        ServerUtils.sendFileToBucket(new File(SampleConstants.TRAIN_DATA_DIR), ServerUtils.RESOURCES_PATCH_BUCKET_NAME);
    }

    public static void upgrade()
    {
        fetchPatch();
        // upgrade config.properties
        File[] files = new File(PATCH_CONFIG_DIR).listFiles();
        for(File file : files)
        {
            FileUtils.copyFile(file, new File(file.getName()));
        }

        // upgrade resources
        files = new File(PATCH_RESOURCE_DIR).listFiles();
        for(File file : files)
        {
            FileUtils.copyFile(file, new File(SampleConstants.TRAIN_DATA_DIR + File.separator + file.getName()));
        }
    }

    private static void fetchPatch()
    {
        ServerUtils.getAllDataFromBucket(ServerUtils.CONFIG_PATCH_BUCKET_NAME, PATCH_CONFIG_DIR);
        ServerUtils.getAllDataFromBucket(ServerUtils.RESOURCES_PATCH_BUCKET_NAME, PATCH_RESOURCE_DIR);
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
        }
    }


}
