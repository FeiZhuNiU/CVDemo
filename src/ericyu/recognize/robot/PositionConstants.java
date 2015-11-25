package ericyu.recognize.robot;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.recognize.image.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class PositionConstants
{

    /**
     * the order should be b/g/r
     */
    private static double[] topLeftCornerColor = {43, 31.0, 25};

    /**
     * coordinate of top left corner
     */
    public static Point origin;
    static
    {
        origin = findPositionOfTargetColor(topLeftCornerColor);
    }

    public static final int FLASH_WIDTH = 900;
    public static final int FLASH_HEIGHT = 700;

    public static final int VERIFICATION_CODE_CONFIRM_ORIGIN_X = 555;
    public static final int VERIFICATION_CODE_CONFIRM_ORIGIN_Y = 465;

    public static final int VERIFICATION_CODE_ORIGIN_X=747;
    public static final int VERIFICATION_CODE_ORIGIN_Y=365;
    public static final int VERIFICATION_CODE_WIDTH=105;
    public static final int VERIFICATION_CODE_HEIGHT=28;


    /**
     * return null if target color is not found
     * @return
     */
    private static Point findPositionOfTargetColor(double[] targetColor)
    {
        Point ret = null;
        ImageUtils.screenCapture("findPosition.bmp");
        Mat screen = Imgcodecs.imread("findPosition.bmp");
        boolean hasFound = false;
        for (int i = 0; i < screen.height(); ++i)
        {
            if (hasFound)
            {
                break;
            }
            for (int j = 0; j < screen.width(); ++j)
            {
                if (Arrays.toString(targetColor).equals(Arrays.toString(screen.get(i, j))))
                {
                    ret = new Point(j, i);
                    hasFound = true;
                    break;
                }
            }
        }
        File file = new File("findPosition.bmp");
        if (file.exists())
        {
            file.delete();
        }
        return ret;
    }
}
