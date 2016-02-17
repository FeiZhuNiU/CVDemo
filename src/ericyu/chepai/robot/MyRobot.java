package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/2/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import com.iknow.image.ImageUtils;
import com.iknow.recognize.Recognition;
import ericyu.chepai.Logger;
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.FlashStatusDetector;
import ericyu.chepai.IStatusObserver;
import ericyu.chepai.Configuration;
import ericyu.chepai.recognition.vcode.SegSingleColor;
import ericyu.chepai.recognition.vcode.VCodeTrain;
import org.opencv.core.Core;
import org.opencv.core.Mat;

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
    private Robot robot;
    private FlashStatusDetector.Status flashStatus;

    private Recognition vCodeRecognition;
    private Recognition vCodeRegionRecognition;

    public void setFlashStatus(FlashStatusDetector.Status flashStatus)
    {
        this.flashStatus = flashStatus;
    }

    public FlashStatusDetector.Status getFlashStatus()
    {
        return flashStatus;
    }

    /**
     * bid money. set when click add money range button
     */
    private int bidMoney;

    public int getBidMoney()
    {
        return bidMoney;
    }

    /**
     * Strings for recognize system notifications
     */
    public static final String NOTIFICATION_RE_BID_OUT_OF_RANGE="不在修改区间范围内重新";
    public static final String NOTIFICATION_RE_ENTER_VERIFICATION_CODE="输正确";
    public static final String NOTIFICATION_BID_SUCCESS="成功";
    public static final String NOTIFICATION_WAITING_BID_QUEUE="等待进队列";
    /**
     * we currently avoid verify this condition by including it to {@link #clickCancelVCodeButton}
     */
    public static final String NOTIFICATION_REQUEST_VCODE_TOO_OFTEN="过于频繁";

    public MyRobot(Robot robot)
    {
        this.robot = robot;
        flashStatus = FlashStatusDetector.Status.NONE;
//        vCodeRecognition = new Recognition(new VCodeTrain(
//                SampleConstants.V_CODE_SAMPLE_TRAIN_DATA_PATH,
//                SampleConstants.V_CODE_SAMPLE_TRAIN_CLASSES_PATH,
//                new AllPixelEigenvectorStrategy()));
//        vCodeRegionRecognition = new Recognition(new RefreshButtonTrain(
//                SampleConstants.REFRESH_BUTTON_SAMPLE_TRAIN_DATA_PATH,
//                SampleConstants.REFRESH_BUTTON_SAMPLE_TRAIN_CLASSES_PATH,
//                new RegionPixelEigenVecStrategy(2,10)));
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
    public static void main(String[] args) throws AWTException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        checkColor();

        if(args.length==1)
        {
            if (args[0].equals("testVCode"))
            {
                MyRobot robot = new MyRobot(new Robot());
                robot.setFlashStatus(FlashStatusDetector.Status.VCODE);
                while(true)
                {
                    robot.recogVerificationCode();
                }
            }
            else if (args[0].equals("testLowestBid"))
            {
                MyRobot robot = new MyRobot(new Robot());
                robot.setFlashStatus(FlashStatusDetector.Status.VCODE);
                Thread thread = new Thread(robot.new LowestBidDetector());
                thread.start();
            }
            else if (args[0].equals("testRefreshButton"))
            {
                MyRobot robot = new MyRobot(new Robot());
                robot.setFlashStatus(FlashStatusDetector.Status.VCODE);
                while(true)
                {
                    System.out.println(robot.getVCodeRegionStatus());

                }
            }
        }

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

    // currently, the value lag around 1 second
    private int currentLowestBid = Configuration.startBid;

    public int getCurrentLowestBid()
    {
        return currentLowestBid;
    }

    public class LowestBidDetector implements Runnable
    {
        /**
         * use tesseract to recognize current lowest bid money
         * @return  0   -> ocr failed
         */
        public int recogCurrentLowestDeal()
        {
            //comment when test
            if(flashStatus != FlashStatusDetector.Status.BID && flashStatus != FlashStatusDetector.Status.VCODE)
            {
                return 0;
            }
            String result = OCRUtils.doOCR(FlashPosition.REGION_LOWEST_DEAL_X,
                    FlashPosition.REGION_LOWEST_DEAL_Y,
                    FlashPosition.REGION_LOWEST_DEAL_WIDTH,
                    FlashPosition.REGION_LOWEST_DEAL_HEIGHT);
            int ret = 0;
            try
            {
                String fixed_result = result.substring(0, 5);
                ret = Integer.parseInt(fixed_result.trim());
            }
            catch (Exception e)
            {
//                Logger.log(Logger.Level.WARNING, flashStatus, "get current Lowest deal failed!");
            }
//            Logger.log(Logger.Level.INFO, flashStatus, "current Lowest deal: " + ret);
            if(ret != 0)
            {
                if (ret < 80000)
                {
                    ret += ((8 - ret/10000))*10000;
                }
            }
            return ret;
        }

        @Override
        public void run()
        {
            int lastRecognized = 0;
            int diffCnt = 0;

            while (true) {
                try {
                    int bak = currentLowestBid;
                    int recognized = recogCurrentLowestDeal();
                    System.out.println(recognized);
                    if (recognized != 0) {
                        if (recognized != lastRecognized) {
                            int next = recognized;
                            if (recognized > currentLowestBid + 100) {
                                diffCnt++;
                                if (diffCnt == 2) {
                                    next = recognized;
                                    diffCnt = 0;
                                } else {
                                    next = 0;
                                }
                            }
                            currentLowestBid = Math.max(currentLowestBid + 100, next);
                        }
                    } else {
                        if (lastRecognized != 0) {
                            currentLowestBid += 100;
                        }
                    }
                    lastRecognized = recognized;

                    if (currentLowestBid != bak) {
                        Logger.log(Logger.Level.INFO, flashStatus, "Current lowest bid changed to " + currentLowestBid);
                    }
                }
                catch (Exception e)
                {
                    // do nothing
                }

            }

        }
    }

    /**
     * verify bid result
     * @return  0   -> bid success
     *          1   -> not in bid range
     *          2   -> wrong verification code
     *          3   -> waiting bid queue
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
                    FlashPosition.origin.x + FlashPosition.REGION_SYSTEM_NOTIFICATION_X,
                    FlashPosition.origin.y + FlashPosition.REGION_SYSTEM_NOTIFICATION_Y,
                    FlashPosition.REGION_SYSTEM_NOTIFICATION_WIDTH,
                    FlashPosition.REGION_SYSTEM_NOTIFICATION_HEIGHT);
            String result = OCRUtils.doOCR(image);
            if (isOutOfRangeNotification(result))
            {
//                clickReBidConfirmButton();
                ret = 1;
                break;
            } else if (isBidSuccessNotification(result))
            {
                ret = 0;
                break;
            } else if (isWrongVCodeNotification(result))
            {
//                clickReEnterVCodeConfirmButton();
                ret = 2;
                break;
            } else if (isWaitingBidQueueNotification(result))
            {
                ret = 3;
                break;
            } else
            {
                // do nothing
                //is handling bid. go on loop
            }
        }
        ImageUtils.deleteImage(image);
        return ret;
    }

    /**
     * @return  -1  -> not in right flash status
     *          1   -> exists
     *          2   -> cross sign
     *          3   -> blank
     */
    public int getVCodeRegionStatus()
    {
        if(flashStatus != FlashStatusDetector.Status.VCODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus,"not ready to find refresh button.");
            return -1;
        }
        int ret = 0;
        Mat toReg = ImageUtils.screenCapture(FlashPosition.origin.x + FlashPosition.REGION_VCODE_X,
                                 FlashPosition.origin.y + FlashPosition.REGION_VCODE_Y,
                                 FlashPosition.REGION_VCODE_WIDTH,
                                 FlashPosition.REGION_VCODE_HEIGHT);

//        toReg = vCodeRegionRecognition.getTraining().process(toReg,null).get(0);
//        Imgcodecs.imwrite("dump.bmp",toReg);

        //TODO: magic number (1 -> refresh button exists)
        int result = vCodeRegionRecognition.recognize(toReg,1);
        if(result == 1)
        {
            Logger.log(Logger.Level.INFO, flashStatus, "refresh button exists.");
            ret = 1;
        }
        else if (result == 2)
        {
            Logger.log(Logger.Level.INFO, flashStatus, "cross sign in VCode region.");
            ret = 2;
        }
        else if (result == 3)
        {
            Logger.log(Logger.Level.INFO, flashStatus, "blank in VCode region.");
            ret = 3;
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

            clickCancelVCodeButton();
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
        if(flashStatus != FlashStatusDetector.Status.VCODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not get verification Code.");
            return null;
        }

        ArrayList<Integer> ret = new ArrayList<>();

        long start = System.currentTimeMillis();
//        Recognition recognition = new Recognition(VCodeTrain.getInstance());
        long mid = System.currentTimeMillis();
        System.out.println("load sample consumed: " + (mid - start) / 1000.0);

//        ImageUtils.screenCapture(ImageUtils.screenCaptureImage,
//                                 FlashPosition.origin.x + FlashPosition.REGION_VCODE_X,
//                                 FlashPosition.origin.y + FlashPosition.REGION_VCODE_Y,
//                                 FlashPosition.REGION_VCODE_WIDTH,
//                                 FlashPosition.REGION_VCODE_HEIGHT);

//        Mat toRecog = ImageUtils.readImage(ImageUtils.screenCaptureImage);

//        java.util.List<Mat> digitsToRecog = vCodeRecognition.getTraining().process(toRecog, new SegSingleColor());
        java.util.List<Mat> digitsToRecog =null;

        long mid2 = System.currentTimeMillis();
        System.out.println("segmentation consumed: " + (mid2 - mid) / 1000.0);
        //recognize
        if (digitsToRecog != null && digitsToRecog.size() == 4)
        {

            for (Mat mat : digitsToRecog)
            {

                int num = vCodeRecognition.recognize(mat,10);
//                    int num = (int)ann_mlp.predict(target);
                ret.add(num);
            }
            Logger.log(Logger.Level.INFO, flashStatus, "recognized : " + ret);
            long mid3 = System.currentTimeMillis();
            System.out.println("recognition consumed: " + (mid3 - mid2) / 1000.0);
            return ret;
        }
        return null;
    }

    public boolean enterVerificationCode(ArrayList<Integer> numbers)
    {
        if(flashStatus != FlashStatusDetector.Status.VCODE)
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
        robot.mouseMove(x + FlashPosition.origin.x, y + FlashPosition.origin.y);
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
        robot.mouseMove(x + FlashPosition.origin.x, y + FlashPosition.origin.y);
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
        if(flashStatus != FlashStatusDetector.Status.VCODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not focus on V-code Input box.");
            return false;
        }
        doubleClickAt(FlashPosition.INPUT_VCODE_X,
                      FlashPosition.INPUT_VCODE_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot focused on V-code input box");
        return true;
    }

    public boolean clickConfirmVCodeButton()
    {
        if(flashStatus != FlashStatusDetector.Status.VCODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click on V-code confirm button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VCODE_CONFIRM_X,
                FlashPosition.BUTTON_VCODE_CONFIRM_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked V-code confirm button");

        return true;
    }

    public boolean clickCancelVCodeButton()
    {
        if(flashStatus != FlashStatusDetector.Status.VCODE &&
                flashStatus != FlashStatusDetector.Status.NOTIFICATION)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click on V-code cancel button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VCODE_CANCEL_X,
                FlashPosition.BUTTON_VCODE_CANCEL_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked V-code cancel button");
        return true;
    }

    public boolean clickRefreshVCodeButton()
    {
        if(flashStatus != FlashStatusDetector.Status.VCODE)
        {
            Logger.log(Logger.Level.WARNING, flashStatus, "can not click on V-code refresh button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_VCODE_REFRESH_X,
                FlashPosition.BUTTON_VCODE_REFRESH_Y);
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
        //set bid money when click add money range button
        bidMoney = getCurrentLowestBid() + Configuration.addMoneyRange;
        Logger.log(Logger.Level.INFO, flashStatus, "bid money : " + bidMoney);
        return true;
    }

    public boolean clickAdd300Button()
    {
        if(flashStatus != FlashStatusDetector.Status.BID)
        {
//            Logger.log(Logger.Level.WARNING, flashStatus, "can not click add 300 Button.");
            return false;
        }
        clickAt(FlashPosition.BUTTON_ADD_300_X,
                FlashPosition.BUTTON_ADD_300_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked add 300 button");
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
        clickAt(FlashPosition.BUTTON_RE_ENTER_VCODE_CONFIRM_X,
                FlashPosition.BUTTON_RE_ENTER_VCODE_CONFIRM_Y);
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
        clickAt(FlashPosition.BUTTON_LOGIN_X,
                FlashPosition.BUTTON_LOGIN_Y);
        Logger.log(Logger.Level.INFO, flashStatus, "robot clicked on login button.");
        return true;
    }

    public void wait(int time)
    {
        robot.delay(time);
//        Logger.log(Logger.Level.INFO, flashStatus, "robot waited " + time + "ms.");
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
    public boolean isWaitingBidQueueNotification(String str)
    {
        return isTargetString(str,NOTIFICATION_WAITING_BID_QUEUE);
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


    @Override
    public void flashStatusChanged(FlashStatusDetector.Status status)
    {
        flashStatus = status;
    }
}
