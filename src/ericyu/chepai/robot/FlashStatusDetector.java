package ericyu.chepai.robot;

/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/

import ericyu.chepai.image.ImageUtils;
import ericyu.chepai.recognize.Recognition;
import ericyu.chepai.train.AllPixelEigenvetorStrategy;
import ericyu.chepai.train.FlashStatusTrain;
import ericyu.chepai.train.SampleConstants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

/**
 * This Thread's duty is to detect current flash status
 */
public class FlashStatusDetector implements Runnable
{
    private FlashPosition flashPosition;
    private Status status;
    private Recognition recognition;
    public enum Status
    {
        NONE,
        LOGIN,
        BID,
        V_CODE,
        NOTIFICATION
    }

    public FlashStatusDetector(FlashPosition flashPosition, Recognition recognition)
    {
        this.recognition = recognition;
        this.flashPosition = flashPosition;
        status = Status.NONE;
    }

    @Override
    public void run()
    {
        while(true)
        {
            Mat target = getRightPartOfFlash();
            List<Mat> toRecogs = recognition.getTrainedData().process(target);
            int result = recognition.recognize(toRecogs.get(0),1);
            switch (result)
            {
                case 1:
                    status = Status.LOGIN;
                    break;
                case 2:
                    status = Status.BID;
                    break;
                case 3:
                    status = Status.V_CODE;
                    break;
                case 4:
                    status = Status.NOTIFICATION;
                    break;
            }
            System.out.println(status);
        }
    }
    private Mat getRightPartOfFlash()
    {
        Mat ret = ImageUtils.screenCapture(
                flashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
                flashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
        return ret;
    }

    private Mat generateSample()
    {
        Mat src = getRightPartOfFlash();
        Mat binary = ImageUtils.color2Binary(src);
        Imgcodecs.imwrite("test.bmp",binary);
        return binary;
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FlashStatusDetector detector = new FlashStatusDetector(new FlashPosition(),new Recognition(new FlashStatusTrain(SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_DATA_PATH,SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_CLASSES_PATH,new AllPixelEigenvetorStrategy())));
//        detector.generateSample();
        Thread thread = new Thread(detector);
        thread.start();
    }
}
