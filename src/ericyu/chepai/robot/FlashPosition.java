package ericyu.chepai.robot;

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

/**
 * This class's goal is to generate a instance that know the position where the flash locate on screen
 *
 * It will try to find the position in the default constructor by pre-configured (topLeftCornerColor)
 * if fails, the origin will be null
 */
public class FlashPosition
{
    /**
     * the order should be b/g/r
     */
    private static double[] topLeftCornerColor = {43, 31.0, 25};

    /**
     * coordinate of top left corner
     */
    public Point origin;

    public FlashPosition()
    {
        origin = findPositionOfTargetColor(topLeftCornerColor);
    }

    /**
     * return null if target color is not found
     * @return
     */
    private Point findPositionOfTargetColor(double[] targetColor)
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
        origin = ret;
        return ret;
    }
}
