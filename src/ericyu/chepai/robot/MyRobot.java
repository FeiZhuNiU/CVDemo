package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import ericyu.chepai.image.ImageUtils;
import ericyu.chepai.image.SegSingleColor;
import ericyu.chepai.image.Segmentation;
import ericyu.chepai.recognize.RecogUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.KNearest;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyRobot
{
    private FlashPosition flashPosition;
    private Robot robot;

    public static final String NOTIFICATION_RE_BID_OUT_OF_RANGE="不在修改区间范围内重新";
    public static final String NOTIFICATION_RE_ENTER_VERIFICATION_CODE="输入正确校验码";
    public static final String NOTIFICATION_BID_SUCCESS="成功";

    public MyRobot(Robot robot)
    {
        this.robot = robot;
        findFlashPosition();
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


    /**
     * return until find flash position
     * @return
     */
    public void findFlashPosition()
    {
        while (true)
        {
            flashPosition = new FlashPosition();
            if (flashPosition.origin != null)
            {
                System.out.println("flash origin found: " + flashPosition.origin.x + "," + flashPosition.origin.y);
                break;
            }
            System.out.println("flash not found yet");
        }
    }

    /**
     * verify bid result
     * @return  0   -> bid success
     *          1   -> not in bid range
     *          -1  -> wrong verification code
     */
    public int verifyResult()
    {
        int ret;
        String image = "systemNotification.bmp";
        wait(500);
        // must get a result in three conditions, or the loop will not stop
        while(true)
        {
            ImageUtils.screenCapture(image,
                                     flashPosition.origin.x + PositionConstants.SYSTEM_NOTIFICATION_WINDOW_X,
                                     flashPosition.origin.y + PositionConstants.SYSTEM_NOTIFICATION_WINDOW_Y,
                                     PositionConstants.SYSTEM_NOTIFICATION_WINDOW_WIDTH,
                                     PositionConstants.SYSTEM_NOTIFICATION_WINDOW_HEIGHT);
            String result = OCRUtils.doOCR(image);

            if (isOutOfRangeNotification(result))
            {
                clickReBidConfirmButton();
                ret = 1;
                break;
            } else if (isBidSuccess(result))
            {
                ret = 0;
                break;
            } else if (isReEnterVerificationCode(result))
            {
                clickReEnterVerificationCodeConfirmButton();
                ret = -1;
                break;
            }
        }
        ImageUtils.deleteImage(image);
        return ret;
    }

    /**
     * the method will not return until it has recognized the verification code
     */
    public void recogAndInputVerificationCode()
    {
        ArrayList<Integer> numbers;
        while (true)
        {
            numbers = recogVerificationCode();
            if (numbers != null)
                break;

            clickCancelVerificationCodeButton();
            wait(1000);
            //TODO verify current page
            clickBidButton();
            wait(3500);

        }

        //enter verification code and submit
        focusOnVerCodeInputBox();
        enterVerificationCode(numbers);
    }

    /**
     *
     */
    private  ArrayList<Integer> recogVerificationCode()
    {
        //get classifier
        KNearest kNearest = RecogUtils.getKnnClassifier();
//        ANN_MLP ann_mlp = RecogUtils.getAnnClassifier();

        ArrayList<Integer> ret = new ArrayList<Integer>();

        //get screen shot of flash
        ImageUtils.screenCapture(ImageUtils.screenCaptureImage,
                flashPosition.origin.x,
                flashPosition.origin.y,
                PositionConstants.FLASH_WIDTH,
                PositionConstants.FLASH_HEIGHT);
        Mat src = Imgcodecs.imread(ImageUtils.screenCaptureImage);
        //get images to recognize
        Rect picRect = new Rect(PositionConstants.VERIFICATION_CODE_LT_X,
                           PositionConstants.VERIFICATION_CODE_LT_Y,
                           PositionConstants.VERIFICATION_CODE_WIDTH,
                           PositionConstants.VERIFICATION_CODE_HEIGHT);
        java.util.List<Mat> digitsToRecog = Segmentation.segmentROI(src, picRect, new SegSingleColor());
        //recognize
        if (digitsToRecog != null && digitsToRecog.size() == 4)
        {

            for (Mat mat : digitsToRecog)
            {
                Mat toRecog = RecogUtils.getEigenVec(mat, null);
                int num = (int) kNearest.findNearest(toRecog, 10, new Mat());
//                    int num = (int)ann_mlp.predict(toRecog);
                ret.add(num);
                System.out.println(num);
            }

            return ret;
        }
        return null;
    }

    public boolean enterVerificationCode(ArrayList<Integer> numbers)
    {
        if (numbers.size() != 4)
        {
            System.out.println("there is no 4 numbers, robot can not work");
            return false;
        }

        for (int num : numbers)
        {
            pressNumber(num);
            System.out.println("robot pressed number " + num);
        }
        return true;

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

    public void clickCancelVerificationCodeButton()
    {
        clickAt(PositionConstants.VERIFICATION_CODE_CANCEL_BUTTON_X,
                PositionConstants.VERIFICATION_CODE_CANCEL_BUTTON_Y);
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

    /**
     * verify whether OCRed resuly is target String
     * @param str
     * @return
     */
    public boolean isOutOfRangeNotification(String str)
    {
        System.out.println(NOTIFICATION_RE_BID_OUT_OF_RANGE);
        return isTargetString(str,NOTIFICATION_RE_BID_OUT_OF_RANGE);
    }
    public boolean isReEnterVerificationCode(String str)
    {
        return isTargetString(str,NOTIFICATION_RE_ENTER_VERIFICATION_CODE);
    }
    public boolean isBidSuccess(String str)
    {
        return isTargetString(str,NOTIFICATION_BID_SUCCESS);
    }
    private boolean isTargetString(String str, String target)
    {
        for(int i = 0 ; i < target.length(); ++i)
        {
            if (str.contains(target.substring(i,i+1)))
            {
                return true;
            }
        }
        return false;
    }
}
