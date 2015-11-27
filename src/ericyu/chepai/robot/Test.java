package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.image.ImageUtils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;

import java.io.File;

public class Test
{
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FlashPosition flashPosition = new FlashPosition();
        ImageUtils.screenCapture("test.bmp",
                                 flashPosition.origin.x + PositionConstants.VERIFICATION_CODE_LT_X,
                                 flashPosition.origin.y + PositionConstants.VERIFICATION_CODE_LT_Y,
                                 PositionConstants.VERIFICATION_CODE_WIDTH,
                                 PositionConstants.VERIFICATION_CODE_HEIGHT);
        File imageFile = new File("test.bmp");
        Tesseract instance = new Tesseract();

        try
        {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        }
        catch (TesseractException e)
        {
            e.printStackTrace();
        }


    }
}
