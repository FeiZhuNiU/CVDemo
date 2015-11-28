package ericyu.chepai.robot;

/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/

import com.recognition.software.jdeskew.ImageUtil;
import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * This Thread's duty is to detect current flash status
 */
public class FlashStatusDetector implements Runnable
{
    private FlashPosition flashPosition;
    private Status status;
    public enum Status
    {
        NONE,
        LOGIN,
        BID,
        V_CODE,
        NOTIFICATION
    }

    public FlashStatusDetector(FlashPosition flashPosition)
    {
        this.flashPosition = flashPosition;
        status = Status.NONE;
    }

    @Override
    public void run()
    {

    }
    private Mat getRightPartOfFlash()
    {
        String image = "rightPart.bmp";
        ImageUtils.screenCapture(image,
                flashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
                flashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
        Mat ret = Imgcodecs.imread(image);
        ImageUtils.deleteImage(image);
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
        FlashStatusDetector detector = new FlashStatusDetector(new FlashPosition());
        detector.generateSample();
    }
}
