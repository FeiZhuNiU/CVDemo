package eric.demo.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import java.awt.*;
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

    public static void doRobotThing(ArrayList<Integer> numbers)
    {
        if (numbers.size() != 4)
        {
            System.out.println("robot can not work");
        }
        try
        {
            Robot robot = new Robot();
            for (int num : numbers)
            {
                keyPress(robot, keyMap.get(num));
                System.out.println("robot pressed number " + num);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void keyPress(Robot r, int key)
    {
        r.keyPress(key);
        r.keyRelease(key);
        r.delay(100);
    }

}
