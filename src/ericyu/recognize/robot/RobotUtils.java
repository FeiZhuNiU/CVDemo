package ericyu.recognize.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import ericyu.recognize.image.ImageUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RobotUtils
{

    public static Map<Integer, Integer> keyMap = new HashMap<Integer, Integer>();

    /**
     * the order should be b/g/r
     */
    private static double[] topLeftCornerColor = {43, 31.0, 25};

    static
    {
        keyMap.put(0, KeyEvent.VK_0);
        keyMap.put(1, KeyEvent.VK_1);
        keyMap.put(2, KeyEvent.VK_2);
        keyMap.put(3, KeyEvent.VK_3);
        keyMap.put(4, KeyEvent.VK_4);
        keyMap.put(5, KeyEvent.VK_5);
        keyMap.put(6, KeyEvent.VK_6);
        keyMap.put(7, KeyEvent.VK_7);
        keyMap.put(8, KeyEvent.VK_8);
        keyMap.put(9, KeyEvent.VK_9);
    }

    public static void enterVerificationCode(ArrayList<Integer> numbers)
    {
        if (numbers.size() != 4)
        {
            System.out.println("there is no 4robot can not work");
        }

        for (int num : numbers)
        {
            pressKey(keyMap.get(num));
            System.out.println("robot pressed number " + num);
        }

    }

    public static void pressKey(int key)
    {
        try
        {
            Robot r = new Robot();
            r.keyPress(key);
            r.keyRelease(key);
            r.delay(100);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    public static void clickAt(int x, int y)
    {
        try
        {
            Robot r = new Robot();
            r.mouseMove(x, y);
            r.mousePress(InputEvent.BUTTON1_MASK);
            r.mouseRelease(InputEvent.BUTTON1_MASK);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    public static Point findPosition()
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
                if (Arrays.toString(topLeftCornerColor).equals(Arrays.toString(screen.get(i, j))))
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

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        checkColor();

        Point point = findPosition();
//        System.out.println(point.x + "  " + point.y);
//        clickAt(point.x,point.y);
    }

    private static void checkColor()
    {
        try
        {
            Robot r = new Robot();
            while (true)
            {
                Point point = MouseInfo.getPointerInfo().getLocation();
                Color color = r.getPixelColor(point.x, point.y);
                System.out.println("x:" + point.x + " y:" + point.y + " color: " + color);
                r.delay(1000);
            }
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

}
