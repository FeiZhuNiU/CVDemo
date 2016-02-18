package ericyu.chepai.recognition;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/28/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

/**
 * Class for train constants
 */
public class SampleConstants
{
    public static final String TRAIN_DATA_DIR = "resources";

    public static final String V_CODE_SAMPLE_DIR="dump\\unNormalized";
    public static final String V_CODE_SAMPLE_TRAIN_DATA_PATH = TRAIN_DATA_DIR + "\\traindata.png";
    public static final String V_CODE_SAMPLE_TRAIN_CLASSES_PATH = TRAIN_DATA_DIR + "\\trainclasses.png";

    public static final String FLASH_STATUS_SAMPLE_DIR="samples\\FlashStatusImage";
    public static final String FLASH_STATUS_SAMPLE_TRAIN_DATA_PATH = TRAIN_DATA_DIR + "\\flashStatusTrainData.png";
    public static final String FLASH_STATUS_SAMPLE_TRAIN_CLASSES_PATH = TRAIN_DATA_DIR + "\\flashStatusTrainClasses.png";

    public static final String REFRESH_BUTTON_SAMPLE_DIR="samples\\RefreshImage";
    public static final String REFRESH_BUTTON_SAMPLE_TRAIN_DATA_PATH = TRAIN_DATA_DIR + "\\refreshButtonTrainData.png";
    public static final String REFRESH_BUTTON_SAMPLE_TRAIN_CLASSES_PATH = TRAIN_DATA_DIR + "\\refreshButtonTrainClasses.png";
}
