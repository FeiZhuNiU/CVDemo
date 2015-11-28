package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import com.recognition.software.jdeskew.ImageUtil;
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
    public static final String NOTIFICATION_RE_ENTER_VERIFICATION_CODE="输入正确";
    public static final String NOTIFICATION_BID_SUCCESS="成功";
    /**
     * we'd better avoid verify this condition by including it to clickCancelVerificationCodeButton()
     */
    public static final String NOTIFICATION_REQUEST_VCODE_TOO_OFTEN="过于频繁";

    public MyRobot(Robot robot)
    {
        this.robot = robot;
        findFlashPosition();
    }

    public static Map<Character, Integer> keyMap = new HashMap<Character, Integer>();

    static
    {
        keyMap.put('0', KeyEvent.VK_0);
        keyMap.put('1', KeyEvent.VK_1);
        keyMap.put('2', KeyEvent.VK_2);
        keyMap.put('3', KeyEvent.VK_3);
        keyMap.put('4', KeyEvent.VK_4);
        keyMap.put('5', KeyEvent.VK_5);
        keyMap.put('6', KeyEvent.VK_6);
        keyMap.put('7', KeyEvent.VK_7);
        keyMap.put('8', KeyEvent.VK_8);
        keyMap.put('9', KeyEvent.VK_9);

        keyMap.put('a', KeyEvent.VK_A);
        keyMap.put('b', KeyEvent.VK_B);
        keyMap.put('c', KeyEvent.VK_C);
        keyMap.put('d', KeyEvent.VK_D);
        keyMap.put('e', KeyEvent.VK_E);
        keyMap.put('f', KeyEvent.VK_F);
        keyMap.put('g', KeyEvent.VK_G);
        keyMap.put('h', KeyEvent.VK_H);
        keyMap.put('i', KeyEvent.VK_I);
        keyMap.put('j', KeyEvent.VK_J);
        keyMap.put('k', KeyEvent.VK_K);
        keyMap.put('l', KeyEvent.VK_L);
        keyMap.put('m', KeyEvent.VK_M);
        keyMap.put('n', KeyEvent.VK_N);
        keyMap.put('o', KeyEvent.VK_O);
        keyMap.put('p', KeyEvent.VK_P);
        keyMap.put('q', KeyEvent.VK_Q);
        keyMap.put('r', KeyEvent.VK_R);
        keyMap.put('s', KeyEvent.VK_S);
        keyMap.put('t', KeyEvent.VK_T);
        keyMap.put('u', KeyEvent.VK_U);
        keyMap.put('v', KeyEvent.VK_V);
        keyMap.put('w', KeyEvent.VK_W);
        keyMap.put('x', KeyEvent.VK_X);
        keyMap.put('y', KeyEvent.VK_Y);
        keyMap.put('z', KeyEvent.VK_Z);


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
    public int verifySystemNotification()
    {
        int ret;
        String image = "systemNotification.bmp";
        wait(500);
        // must get a result in three conditions, or the loop will not stop
        while(true)
        {
            ImageUtils.screenCapture(image,
                                     flashPosition.origin.x + FlashPosition.REGION_SYSTEM_NOTIFICATION_X,
                                     flashPosition.origin.y + FlashPosition.REGION_SYSTEM_NOTIFICATION_Y,
                                     FlashPosition.REGION_SYSTEM_NOTIFICATION_WIDTH,
                                     FlashPosition.REGION_SYSTEM_NOTIFICATION_HEIGHT);
            String result = OCRUtils.doOCR(image);
            System.out.println(result);
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
            wait(300);
            //TODO : lan lag ? verify current page
            clickBidButton();
        }

        //enter verification code and submit
        focusOnVerCodeInputBox();
        enterVerificationCode(numbers);
    }

    /**
     * recognize verification code
     * @return null if fail
     */
    public ArrayList<Integer> recogVerificationCode()
    {
        //get classifier
        KNearest kNearest = RecogUtils.getKnnClassifier();
//        ANN_MLP ann_mlp = RecogUtils.getAnnClassifier();

        ArrayList<Integer> ret = new ArrayList<Integer>();

        //get screen shot of flash
        ImageUtils.screenCapture(ImageUtils.screenCaptureImage,
                flashPosition.origin.x,
                flashPosition.origin.y,
                FlashPosition.FLASH_WIDTH,
                FlashPosition.FLASH_HEIGHT);
        Mat src = Imgcodecs.imread(ImageUtils.screenCaptureImage);
        //get images to recognize
        Rect picRect = new Rect(FlashPosition.REGION_VERIFICATION_CODE_LT_X,
                           FlashPosition.REGION_VERIFICATION_CODE_LT_Y,
                           FlashPosition.REGION_VERIFICATION_CODE_WIDTH,
                           FlashPosition.REGION_VERIFICATION_CODE_HEIGHT);
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
            pressKey((char)(num+48));
            System.out.println("robot pressed number " + num);
        }
        return true;

    }

    /**
     * press number and delay 50 ms
     * @param c
     */
    public void pressKey(Character c)
    {
        boolean isUpper = Character.isUpperCase(c);
        int key = keyMap.get(Character.toLowerCase(c));
        if(isUpper)
        {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
        robot.keyPress(key);
        robot.keyRelease(key);
        if(isUpper)
        {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
        robot.delay(50);
    }

    public void inputString(String str)
    {
        for(int i = 0 ; i < str.length(); ++i)
        {
            pressKey(str.charAt(i));
        }
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
        doubleClickAt(FlashPosition.INPUT_VERIFICATION_X,
                FlashPosition.INPUT_VERIFICATION_Y);
    }

    public void clickConfirmVerificationCodeButton()
    {
        clickAt(FlashPosition.BUTTON_VERIFICATION_CODE_CONFIRM_X,
                FlashPosition.BUTTON_VERIFICATION_CODE_CONFIRM_Y);
    }

    public void clickCancelVerificationCodeButton()
    {
        clickAt(FlashPosition.BUTTON_VERIFICATION_CODE_CANCEL_X,
                FlashPosition.BUTTON_VERIFICATION_CODE_CANCEL_Y);
    }

    public void clickRefreshVerificationCodeButton()
    {
        clickAt(FlashPosition.BUTTON_VERIFICATION_REFRESH_X,
                FlashPosition.BUTTON_VERIFICATION_REFRESH_Y);
    }

    public void focusOnCustomAddMoneyInputBox()
    {
        doubleClickAt(FlashPosition.INPUT_CUSTOM_ADD_MONEY_X,
                FlashPosition.INPUT_CUSTOM_ADD_MONEY_Y);
    }

    public void inputAddMoneyRange(int range)
    {
        String money = Integer.toString(range);
        for(int i = 0 ; i < money.length(); ++i)
        {
            pressKey(money.charAt(i));
        }
    }

    public void clickAddMoneyButton()
    {
        clickAt(FlashPosition.BUTTON_ADD_MONEY_X,
                FlashPosition.BUTTON_ADD_MONEY_Y);
    }

    public void clickBidButton()
    {
        clickAt(FlashPosition.BUTTON_BID_X,
                FlashPosition.BUTTON_BID_Y);
    }

    /**
     * button for out of range
     */
    public void clickReBidConfirmButton()
    {
        clickAt(FlashPosition.BUTTON_REBID_CONFIRM_X,
                FlashPosition.BUTTON_REBID_CONFIRM_Y);
    }

    /**
     * button for wrong verification code
     */
    public void clickReEnterVerificationCodeConfirmButton()
    {
        clickAt(FlashPosition.BUTTON_RE_ENTER_VERIFICATION_CONFIRM_X,
                FlashPosition.BUTTON_RE_ENTER_VERIFICATION_CONFIRM_Y);
    }
    public void clickRequestForVCodeTooOftenConfirmButton()
    {
        clickAt(FlashPosition.BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X,
                FlashPosition.BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y);
    }

    public void wait(int time)
    {
        robot.delay(time);
    }

    public void focusOnUsernameInputBox()
    {
        doubleClickAt(FlashPosition.INPUT_USERNAME_X,
                FlashPosition.INPUT_USERNAME_Y);
    }

    public void focusOnPasswordInputBox()
    {
        doubleClickAt(FlashPosition.INPUT_PASSWORD_X,
                FlashPosition.INPUT_PASSWORD_Y);
    }

    public void clickSubmitUserButton()
    {
        clickAt(FlashPosition.BUTTON_SUBMIT_X,
                FlashPosition.BUTTON_SUBMIT_Y);
    }

    /**
     * verify whether OCRed resuly is target String
     * @param str
     * @return
     */
    public boolean isOutOfRangeNotification(String str)
    {
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
    public boolean isRequestVCodeTooOften(String str)
    {
        return isTargetString(str,NOTIFICATION_REQUEST_VCODE_TOO_OFTEN);
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

    public int getLowestDeal()
    {
        String result = OCRUtils.doOCR(flashPosition,
                                       FlashPosition.REGION_LOWEST_DEAL_X,
                                       FlashPosition.REGION_LOWEST_DEAL_Y,
                                       FlashPosition.REGION_LOWEST_DEAL_WIDTH,
                                       FlashPosition.REGION_LOWEST_DEAL_HEIGHT);
        int ret = 0;
        try
        {
            String fixed_result = result.substring(0, 5);
            ret = Integer.parseInt(fixed_result);
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            System.out.println("get Lowest deal failed!");
        }
        return ret;
    }
}
