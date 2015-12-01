package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import ericyu.chepai.Logger;
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.flash.FlashStatusDetector;
import ericyu.chepai.flash.IStatusObserver;
import ericyu.chepai.image.ImageUtils;
import ericyu.chepai.train.AllPixelEigenvetorStrategy;
import ericyu.chepai.train.RefreshButtonTrain;
import ericyu.chepai.train.SampleConstants;
import ericyu.chepai.recognize.Recognition;
import ericyu.chepai.train.VCodeTrain;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Robot
 */
public class MyRobot implements IStatusObserver
{
    private FlashPosition flashPosition;
    private Robot robot;
    private FlashStatusDetector.Status flashStatus;

    /**
     * Strings for recognize system notifications
     */
    public static final String NOTIFICATION_RE_BID_OUT_OF_RANGE="不在修改区间范围内重新";
    public static final String NOTIFICATION_RE_ENTER_VERIFICATION_CODE="输入正确";
    public static final String NOTIFICATION_BID_SUCCESS="成功";
    /**
     * we currently avoid verify this condition by including it to {@link #clickCancelVerificationCodeButton}
     */
    public static final String NOTIFICATION_REQUEST_VCODE_TOO_OFTEN="过于频繁";

    public MyRobot(Robot robot)
    {
        this.robot = robot;
        this.flashPosition = FlashPosition.getInstance();
        flashStatus = FlashStatusDetector.Status.NONE;
    }

    public static Map<Character, Integer> keyMap = new HashMap<>();

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

    /**
     * manually get color
     * @param args
     */
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        checkColor();
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
                Logger.log(Logger.Level.INFO, null, "x:" + point.x + " y:" + point.y + " color: " + color);
                r.delay(2000);
            }
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * verify bid result
     * @return  0   -> bid success
     *          1   -> not in bid range
     *          2   -> wrong verification code
     *          3   -> handling bid
     *          -1  -> not right status
     */
    public int verifySystemNotification()
    {
        if(flashStatus != FlashStatusDetector.Status.NOTIFICATION)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not verify notification.");
            return -1;
        }
        int ret;
        String image = "systemNotification.bmp";
        // must get a result in three conditions, or the loop will not stop
        while(true)
        {
            ImageUtils.screenCapture(image,
                                     flashPosition.origin.x + FlashPosition.REGION_SYSTEM_NOTIFICATION_X,
                                     flashPosition.origin.y + FlashPosition.REGION_SYSTEM_NOTIFICATION_Y,
                                     FlashPosition.REGION_SYSTEM_NOTIFICATION_WIDTH,
                                     FlashPosition.REGION_SYSTEM_NOTIFICATION_HEIGHT);
            String result = OCRUtils.doOCR(image);
            if (isOutOfRangeNotification(result))
            {
                clickReBidConfirmButton();
                ret = 1;
                break;
            } else if (isBidSuccessNotification(result))
            {
                ret = 0;
                break;
            } else if (isWrongVCodeNotification(result))
            {
                clickReEnterVCodeConfirmButton();
                ret = 2;
                break;
            } else
            {
                ret = 3;
                break;
            }
        }
        ImageUtils.deleteImage(image);
        return ret;
    }

    /**
     * @return  -1  -> not in right status
     *          0   -> not exists
     *          1   -> exists
     */
    public int isRefreshVCodeButtonExist()
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus,"not ready to find refresh button.");
            return -1;
        }
        int ret;
        Mat toReg = ImageUtils.screenCapture(flashPosition.origin.x + FlashPosition.REGION_VERIFICATION_CODE_LT_X,
                                 flashPosition.origin.y + FlashPosition.REGION_VERIFICATION_CODE_LT_Y,
                                 FlashPosition.REGION_VERIFICATION_CODE_WIDTH,
                                 FlashPosition.REGION_VERIFICATION_CODE_HEIGHT);
        Recognition recognition = new Recognition(new RefreshButtonTrain(SampleConstants.REFRESH_BUTTON_SAMPLE_TRAIN_DATA_PATH,
                                                                         SampleConstants.REFRESH_BUTTON_SAMPLE_TRAIN_CLASSES_PATH,
                                                                         new AllPixelEigenvetorStrategy()));
        toReg = recognition.getTrainedData().process(toReg).get(0);
//        Imgcodecs.imwrite("dump.bmp",toReg);

        int result = recognition.recognize(toReg,1);
        if(result == 1)
        {
            Logger.log(Logger.Level.INFO, flashStatus, "refresh button exists");
            ret = 1;
        }
        else
        {
            Logger.log(Logger.Level.INFO, flashStatus, "refresh button does not exist");
            ret = 0;
        }

        return ret;
    }

    /**
     * the method will not return until it has recognized the verification code
     */
    @Deprecated
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
        focusOnVCodeInputBox();
        enterVerificationCode(numbers);
    }

    /**
     * recognize verification code
     * @return null if fail
     */
    public ArrayList<Integer> recogVerificationCode()
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not get verification Code.");
            return null;
        }

        ArrayList<Integer> ret = new ArrayList<>();

        Recognition recognition = new Recognition(
                new VCodeTrain(
                    SampleConstants.V_CODE_SAMPLE_TRAIN_DATA_PATH,
                    SampleConstants.V_CODE_SAMPLE_TRAIN_CLASSES_PATH,
                    new AllPixelEigenvetorStrategy()));

        ImageUtils.screenCapture(ImageUtils.screenCaptureImage,
                                 flashPosition.origin.x + FlashPosition.REGION_VERIFICATION_CODE_LT_X,
                                 flashPosition.origin.y + FlashPosition.REGION_VERIFICATION_CODE_LT_Y,
                                 FlashPosition.REGION_VERIFICATION_CODE_WIDTH,
                                 FlashPosition.REGION_VERIFICATION_CODE_HEIGHT);

        Mat toRecog = ImageUtils.readImage(ImageUtils.screenCaptureImage);

        java.util.List<Mat> digitsToRecog = recognition.getTrainedData().process(toRecog);
        //recognize
        if (digitsToRecog != null && digitsToRecog.size() == 4)
        {

            for (Mat mat : digitsToRecog)
            {

                int num = recognition.recognize(mat,10);
//                    int num = (int)ann_mlp.predict(target);
                ret.add(num);
            }
            Logger.log(Logger.Level.INFO, flashStatus, "recognized : " + ret);
            return ret;
        }
        return null;
    }

    public boolean enterVerificationCode(ArrayList<Integer> numbers)
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not enter verification code.");
            return false;
        }

        if (numbers.size() != 4)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "there is no 4 numbers, robot can not work.");
            return false;
        }

        for (int num : numbers)
        {
            pressKey((char) (num + 48));
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
        Logger.log(Logger.Level.INFO, flashStatus,"robot pressed " + c);
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
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked at (" + x + "," + y + ")");
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
        Logger.log(Logger.Level.INFO, flashStatus, "robot double clicked at (" + x + "," + y + ")");
        robot.delay(10);
    }

    public boolean focusOnVCodeInputBox()
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not focus on V-code Input box.");
            return false;
        }
        doubleClickAt(FlashPosition.INPUT_VERIFICATION_X,
                FlashPosition.INPUT_VERIFICATION_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot focused on V-code input box");
        return true;
    }

    public boolean clickConfirmVCodeButton()
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click on V-code confirm button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VERIFICATION_CODE_CONFIRM_X,
                FlashPosition.BUTTON_VERIFICATION_CODE_CONFIRM_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked V-code confirm button");

        return true;
    }

    public boolean clickCancelVerificationCodeButton()
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE &&
                flashStatus != FlashStatusDetector.Status.NOTIFICATION)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click on V-code cancel button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VERIFICATION_CODE_CANCEL_X,
                FlashPosition.BUTTON_VERIFICATION_CODE_CANCEL_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked V-code cancel button");
        return true;
    }

    public boolean clickRefreshVCodeButton()
    {
        if(flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click on V-code refresh button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VERIFICATION_REFRESH_X,
                FlashPosition.BUTTON_VERIFICATION_REFRESH_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked refresh V-code button");
        return true;
    }

    public boolean focusOnCustomAddMoneyInputBox()
    {
        if(flashStatus != FlashStatusDetector.Status.BID)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not focus on custom add money input box.");
            return false;
        }
        doubleClickAt(FlashPosition.INPUT_CUSTOM_ADD_MONEY_X,
                      FlashPosition.INPUT_CUSTOM_ADD_MONEY_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot focused on custom add money range input box.");
        return true;
    }

    public boolean inputAddMoneyRange(int range)
    {
        if(flashStatus != FlashStatusDetector.Status.BID)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not input add money range.");
            return false;
        }
        String money = Integer.toString(range);
        for(int i = 0 ; i < money.length(); ++i)
        {
            pressKey(money.charAt(i));
        }
        Logger.log(Logger.Level.INFO, flashStatus, "robot has input added money range: " + money);
        return true;
    }

    public boolean clickAddMoneyButton()
    {
        if(flashStatus != FlashStatusDetector.Status.BID)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click AddMoney Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_ADD_MONEY_X,
                FlashPosition.BUTTON_ADD_MONEY_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked add money button");
        return true;
    }

    public boolean clickBidButton()
    {
        if(flashStatus != FlashStatusDetector.Status.BID)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click bid Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_BID_X,
                FlashPosition.BUTTON_BID_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked bid button");
        return true;
    }

    /**
     * button for out of range
     */
    public boolean clickReBidConfirmButton()
    {
        if(flashStatus != FlashStatusDetector.Status.NOTIFICATION)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click ReBid Confirm Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_REBID_CONFIRM_X,
                FlashPosition.BUTTON_REBID_CONFIRM_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked rebid confirm button");
        return true;
    }

    /**
     * button for wrong verification code
     */
    public boolean clickReEnterVCodeConfirmButton()
    {
        if(flashStatus != FlashStatusDetector.Status.NOTIFICATION)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click ReEnter V-Code Confirm Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_RE_ENTER_VERIFICATION_CONFIRM_X,
                FlashPosition.BUTTON_RE_ENTER_VERIFICATION_CONFIRM_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked re-enter V-code confirm button");
        return true;
    }
    public boolean clickRequestForVCodeTooOftenConfirmButton()
    {
        if(flashStatus != FlashStatusDetector.Status.NOTIFICATION)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click RequestForVCodeTooOften Confirm Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X,
                FlashPosition.BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked request for V-code too often confirm button");
        return true;
    }

    public boolean focusOnUsernameInputBox()
    {
        if(flashStatus != FlashStatusDetector.Status.LOGIN)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not focus On Username Input Box.");
            return false;
        }
        doubleClickAt(FlashPosition.INPUT_USERNAME_X,
                      FlashPosition.INPUT_USERNAME_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot focused on username input box.");
        return true;
    }

    public boolean focusOnPasswordInputBox()
    {
        if(flashStatus != FlashStatusDetector.Status.LOGIN)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not focus On password Input Box.");
            return false;
        }
        doubleClickAt(FlashPosition.INPUT_PASSWORD_X,
                      FlashPosition.INPUT_PASSWORD_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot focused on password input box.");
        return true;
    }

    public boolean clickLoginButton()
    {
        if(flashStatus != FlashStatusDetector.Status.LOGIN)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click Submit User Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_SUBMIT_X,
                FlashPosition.BUTTON_SUBMIT_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked on login button.");
        return true;
    }

    public void wait(int time)
    {
        robot.delay(time);
        Logger.log(Logger.Level.INFO, flashStatus, "robot waited " + time + "ms.");
    }

    /**
     * verify whether OCRed result is target String
     * @param str
     * @return
     */
    public boolean isOutOfRangeNotification(String str)
    {
        return isTargetString(str,NOTIFICATION_RE_BID_OUT_OF_RANGE);
    }
    public boolean isWrongVCodeNotification(String str)
    {
        return isTargetString(str,NOTIFICATION_RE_ENTER_VERIFICATION_CODE);
    }
    public boolean isBidSuccessNotification(String str)
    {
        return isTargetString(str,NOTIFICATION_BID_SUCCESS);
    }
    @Deprecated
    public boolean isRequestVCodeTooOftenNotification(String str)
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

    /**
     * use tesseract to recognize current lowest bid money
     * @return 0 if failed
     */
    public int getCurrentLowestDeal()
    {
        if(flashStatus != FlashStatusDetector.Status.BID && flashStatus != FlashStatusDetector.Status.V_CODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not get lowest deal.");
            return 0;
        }
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
            Logger.log(Logger.Level.WARNING, flashStatus, "get current Lowest deal failed!");
        }
        return ret;
    }

    @Override
    public void flashStatusChanged(FlashStatusDetector.Status status)
    {
        flashStatus = status;
    }
}
