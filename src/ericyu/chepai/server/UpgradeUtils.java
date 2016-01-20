package ericyu.chepai.server;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 1/20/2016  (yulin.jay@gmail.com)            |
 +===========================================================================*/

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
        ServerUtils.sendFileToBucket(new File(Configuration.CONFIG_FILE), ServerUtils.PROPERTIESPATCH_BUCKET_NAME);
//        ServerUtils.sendFileToBucket(new File(SampleConstants.TRAIN_DATA_DIR), ServerUtils.RESOURCESPATCH_BUCKET_NAME);
    }

    public static void upgrade()
    {
        // upgrade config.properties


        // upgrade resources
    }

    private static void downloadPatch()
    {
        ServerUtils.getAllDataFromBucket(ServerUtils.PROPERTIESPATCH_BUCKET_NAME, PATCH_CONFIG_DIR);
        ServerUtils.getAllDataFromBucket(ServerUtils.RESOURCESPATCH_BUCKET_NAME, PATCH_RESOURCE_DIR);
    }

}
