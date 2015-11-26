package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import org.opencv.core.Core;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyRobot
{
    FlashPosition flashPosition;

    public MyRobot(FlashPosition flashPosition)
    {
        this.flashPosition = flashPosition;
    }

    public static Map<Integer, Integer> keyMap = new HashMap<Integer, Integer>();

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

    public void enterVerificationCode(ArrayList<Integer> numbers)
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
            r.delay(50);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * left click at given position and move back
     * @param x
     * @param y
     */
    public static void clickAt(int x, int y)
    {
        try
        {
            Point curMousePosition = MouseInfo.getPointerInfo().getLocation();
            Robot r = new Robot();
            r.mouseMove(x, y);
            r.mousePress(InputEvent.BUTTON1_MASK);
            r.mouseRelease(InputEvent.BUTTON1_MASK);
            r.mouseMove(curMousePosition.x,curMousePosition.y);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        checkColor();

//        System.out.println(point.x + "  " + point.y);
//        clickAt(point.x,point.y);
    }

    /**
     * manually get color if background color is changed
     */
    private static void checkColor()
    {
        try
        {
            java.awt.Robot r = new java.awt.Robot();
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

    public void focusOnVerCodeInputBox()
    {
        clickAt(flashPosition.origin.x + PositionConstants.VERIFICATION_INPUT_X,
                flashPosition.origin.y + PositionConstants.VERIFICATION_INPUT_Y);
    }

    public void confirmVerificationCode()
    {
        clickAt(flashPosition.origin.x + PositionConstants.VERIFICATION_CODE_CONFIRM_BUTTON_X,
                flashPosition.origin.y + PositionConstants.VERIFICATION_CODE_CONFIRM_BUTTON_Y);
    }

    public void refreshVerificationCode()
    {
        clickAt(flashPosition.origin.x + PositionConstants.VERIFICATION_REFRESH_BUTTON_X,
                flashPosition.origin.y + PositionConstants.VERIFICATION_REFRESH_BUTTON_Y);
    }
}
