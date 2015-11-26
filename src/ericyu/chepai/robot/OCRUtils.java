package ericyu.chepai.robot;

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

    public static String doOCR(String imageName)
    {
        File imageFile = new File(imageName);
        Tesseract instance = new Tesseract();
        try
        {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
            return result;
        }
        catch (TesseractException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
