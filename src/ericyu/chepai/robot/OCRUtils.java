package ericyu.chepai.robot;

import ericyu.chepai.Logger;
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.image.ImageUtils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/26/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

public class OCRUtils
{
    /**
     * OCR target image
     * @param imageName
     * @return
     */
    public static String doOCR(String imageName)
    {
        File imageFile = new File(imageName);
        Tesseract instance = new Tesseract();
        try
        {
            String result = instance.doOCR(imageFile);
            result = result.trim();
            Logger.log(Logger.Level.INFO, null,"tesseract : " + result);
            return result;
        }
        catch (TesseractException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ocr target RECT of current flash
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static String doOCR(int x, int y, int width,int height)
    {
        if(FlashPosition.origin==null)
        {
            return "";
        }

        String imageName = "temp.bmp";
        //screen capture
        ImageUtils.screenCapture(imageName,
                                 FlashPosition.origin.x + x,
                                 FlashPosition.origin.y + y,
                                 width,
                                 height);
        //ocr
        String ret = doOCR(imageName);

        //clean
        ImageUtils.deleteImage(imageName);

        return ret;
    }
}
