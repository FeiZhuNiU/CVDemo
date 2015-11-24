package ericyu.recognize.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RobotUtils
{

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
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    public static void clickAt(int x, int y)
    {
        try
        {
            Robot r = new Robot();
            r.mouseMove(x,y);
            r.mousePress(InputEvent.BUTTON1_MASK);
            r.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    public static Point findPosition()
    {
        try
        {
            Robot robot = new Robot();
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            for(int i = 0; i <dimension.height; ++i)
            {
                for(int j = 0 ; j < dimension.width-1; ++j)
                {
                    if(robot.getPixelColor(j,i) == new Color(25,31,43)/* && robot.getPixelColor(j+1,i) == new Color(25,31,45) && robot.getPixelColor(j,i+1) == new Color(24,33,48)*/)
                    {
                        return new Point(j,i);
                    }
                }
            }
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
    {
//        try
//        {
//            Robot r = new Robot();
//            while(true)
//            {
//                Point point = MouseInfo.getPointerInfo().getLocation();
//                Color color = r.getPixelColor(point.x, point.y);
//                System.out.println("x:" + point.x + " y:" + point.y + " color: " + color);
//                r.delay(1000);
//            }
//        } catch (AWTException e)
//        {
//            e.printStackTrace();
//        }
        Point point = findPosition();
        System.out.println(point.x + "  " + point.y);
        clickAt(point.x,point.y);
    }

}
