package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.flash.FlashPosition;
import org.opencv.core.Core;

import java.awt.*;

public class Test
{
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        FlashPosition flashPosition = new FlashPosition();
//        ImageUtils.screenCapture("test.bmp",
//                                 flashPosition.origin.x + FlashPosition.REGION_LOWEST_DEAL_X,
//                                 flashPosition.origin.y + FlashPosition.REGION_LOWEST_DEAL_Y,
//                                 FlashPosition.REGION_LOWEST_DEAL_WIDTH,
//                                 FlashPosition.REGION_LOWEST_DEAL_HEIGHT);
//        File imageFile = new File("test.bmp");
//        Tesseract instance = new Tesseract();
//
//        try
//        {
//            String result = instance.doOCR(imageFile);
//            System.out.println(result);
//        }
//        catch (TesseractException e)
//        {
//            e.printStackTrace();
//        }
        while(true)
        {
            try
            {
                MyRobot robot = new MyRobot(new Robot());
                System.out.println(robot.getCurrentLowestDeal());
            }
            catch (AWTException e)
            {
                e.printStackTrace();
                return;
            }
        }


    }
}
