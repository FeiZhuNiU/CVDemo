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
    Robot robot;

    public MyRobot(Robot robot, FlashPosition flashPosition)
    {
        this.robot = robot;
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
            pressNumber(num);
            System.out.println("robot pressed number " + num);
        }

    }

    /**
     * press number and delay 50 ms
     * @param num
     */
    public void pressNumber(int num)
    {
        int key = keyMap.get(num);
        robot.keyPress(key);
        robot.keyRelease(key);
        robot.delay(50);
    }

    /**
     * left click at given (relative) position and move back and wait 100 ms
     * @param x
     * @param y
     */
    public void clickAt(int x, int y)
    {
        Point curMousePosition = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(x + flashPosition.origin.x, y + flashPosition.origin.y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mouseMove(curMousePosition.x, curMousePosition.y);
        robot.delay(100);
    }

    /**
     * double click and wait 100 ms
     * @param x
     * @param y
     */
    public void doubleClickAt(int x, int y)
    {
        Point curMousePosition = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(x + flashPosition.origin.x, y + flashPosition.origin.y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mouseMove(curMousePosition.x, curMousePosition.y);
        robot.delay(100);
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
        clickAt(PositionConstants.VERIFICATION_INPUT_X,
                PositionConstants.VERIFICATION_INPUT_Y);
    }

    public void clickConfirmVerificationCodeButton()
    {
        clickAt(PositionConstants.VERIFICATION_CODE_CONFIRM_BUTTON_X,
                PositionConstants.VERIFICATION_CODE_CONFIRM_BUTTON_Y);
    }

    public void clickRefreshVerificationCodeButton()
    {
        clickAt(PositionConstants.VERIFICATION_REFRESH_BUTTON_X,
                PositionConstants.VERIFICATION_REFRESH_BUTTON_Y);
    }

    public void focusOnCustomAddMoneyInputBox()
    {
        doubleClickAt(PositionConstants.CUSTOM_ADD_MONEY_INPUT_X,
                      PositionConstants.CUSTOM_ADD_MONEY_INPUT_Y);
    }

    public void inputAddMoneyRange(int range)
    {
        String money = Integer.toString(range);
        for(int i = 0 ; i < money.length(); ++i)
        {
            pressNumber(Integer.parseInt(money.substring(i, i + 1)));
        }
    }

    public void clickAddMoneyButton()
    {
        clickAt(PositionConstants.ADD_MONEY_BUTTON_X,
                PositionConstants.ADD_MONEY_BUTTON_Y);
    }

    public void clickBidButton()
    {
        clickAt(PositionConstants.BID_BUTTON_X,
                PositionConstants.BID_BUTTON_Y);
    }

    public void clickReBidConfirmButton()
    {
        clickAt(PositionConstants.REBID_CONFIRM_BUTTON_X,
                PositionConstants.REBID_CONFIRM_BUTTON_Y);
    }

    public void clickReEnterVerificationCodeConfirmButton()
    {
        clickAt(PositionConstants.RE_ENTER_VERIFICATION_CONFIRM_BUTTON_X,
                PositionConstants.RE_ENTER_VERIFICATION_CONFIRM_BUTTON_Y);
    }
    public void wait(int time)
    {
        robot.delay(time);
    }
}
