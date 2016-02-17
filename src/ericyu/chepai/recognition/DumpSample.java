package ericyu.chepai.recognition;

import com.iknow.image.ImageUtils;
import ericyu.chepai.CommandConstants;
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.recognition.flashstatus.FlashStatusTrain;
import ericyu.chepai.recognition.vcoderegion.RefreshButtonTrain;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 12/15/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/
public class DumpSample
{
    private static void dumpFlashStatusSample()
    {
//        Mat mat = ImageUtils.screenCapture(
//                FlashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
//                FlashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
//                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
//                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
//        Mat processed = (new FlashStatusTrain()).process(mat,null).get(0);
//        Imgcodecs.imwrite("flashStatus.bmp",processed);
    }
    private static void dumpRefreshButtonSample()
    {
//        Mat mat = ImageUtils.screenCapture(
//                FlashPosition.origin.x + FlashPosition.REGION_VCODE_X,
//                FlashPosition.origin.y + FlashPosition.REGION_VCODE_Y,
//                FlashPosition.REGION_VCODE_WIDTH,
//                FlashPosition.REGION_VCODE_HEIGHT);
//        Mat processed = (new RefreshButtonTrain()).process(mat,null).get(0);
//        Imgcodecs.imwrite("refreshButton.bmp",processed);
    }


    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if(args.length == 1)
        {
            if(args[0].equals(CommandConstants.DUMP_FLASHSTATUS_SAMPLE))
            {
                dumpFlashStatusSample();
            }
            else if (args[0].equals(CommandConstants.DUMP_VCODE_REGION_SAMPLE))
            {
                dumpRefreshButtonSample();
            }
        }
    }
}
