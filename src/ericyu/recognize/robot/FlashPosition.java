package ericyu.recognize.robot;

import ericyu.recognize.image.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

/**
 * Created by 麟 on 2015/11/25.
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
