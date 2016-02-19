package ericyu.chepai.flash;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.iknow.image.ImageUtils;
import ericyu.chepai.Configuration;
import ericyu.chepai.utils.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.awt.*;
import java.util.Arrays;


/**
 * contains all button positions on that flash
 *
 * Constant naming rule:
 * INPUT_...
 * REGION_...
 * BUTTON_...
 *
// * It will try to find the position in {@link #setOrigin}
// * by pre-configured color:{@link #topLeftCornerColor}
 */
public class FlashPosition
{

    /**
     * coordinate of top left corner
     */
    public static Point origin;

    /**
     * the order should be b/g/r
     */
    private static final double[] topLeftCornerColor;
    private static final double[] topLeftCornerColorOffset_10_10;
    private static int detectRate = 100; // ms
    static
    {
        topLeftCornerColor = new double[]{Configuration.lefttopColorB,
                Configuration.lefttopColorG,
                Configuration.lefttopColorR};
        topLeftCornerColorOffset_10_10 = new double[]{Configuration.lefttopColorOffsetB,
                Configuration.lefttopColorOffsetG,
                Configuration.lefttopColorOffsetR};
        origin = new Point(Configuration.leftTopX,Configuration.leftTopY);

    }

    @Deprecated
    public static class FlashPositionDetector implements Runnable
    {

        @Override
        public void run()
        {
            while (true)
            {
                Point detected = findLeftTopPosition();
                if(detected != null)
                {
                    if(detected.x != origin.x || detected.y != origin.y)
                    {
                        origin = detected;
                        Logger.log(Logger.Level.INFO, null, "flash position has changed to " + origin.x + "," + origin.y + "!");
                    }
                }
                try
                {
                    Thread.sleep(detectRate);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * set origin until origin is found
     */
    private static void setOrigin()
    {
        Logger.log(Logger.Level.INFO, null,"finding Flash window ... ");
        while ((origin = findLeftTopPosition()) == null);
        Logger.log(Logger.Level.INFO, null,"find the flash window : left top at (" + origin.x + "," + origin.y + ")");
    }

    /**
     * flash
     */
    public static final int FLASH_WIDTH     = Configuration.flashWidth;
    public static final int FLASH_HEIGHT    = Configuration.flashHeight;

    /**
     *  right part of the flash
     *  used for detect flash status
     */
    public static final int REGION_FLASH_RIGHT_PART_X = Configuration.flashStatusRegionX;
    public static final int REGION_FLASH_RIGHT_PART_Y = Configuration.flashStatusRegionY;
    public static final int REGION_FLASH_RIGHT_PART_WIDTH = Configuration.flashStatusRegionWidth;
    public static final int REGION_FLASH_RIGHT_PART_HEIGHT = Configuration.flashStatusRegionHeight;
    /**
     * verification code confirm button
     */
    public static final int BUTTON_VCODE_CONFIRM_X = Configuration.vCodeConfirmButtonX;
    public static final int BUTTON_VCODE_CONFIRM_Y = Configuration.vCodeConfirmButtonY;
    /**
     * verification code cancel button
     */
    public static final int BUTTON_VCODE_CANCEL_X = Configuration.vCodeCancelButtonX;
    public static final int BUTTON_VCODE_CANCEL_Y = Configuration.vCodeCancelButtonY;
    /**
     * verification code image region
     */
    public static final int REGION_VCODE_X = Configuration.vCodeRegionX;
    public static final int REGION_VCODE_Y = Configuration.vCodeRegionY;
    public static final int REGION_VCODE_WIDTH = Configuration.vCodeRegionWidth;
    public static final int REGION_VCODE_HEIGHT = Configuration.vCodeRegionHeight;
    /**
     * verification code input box
     */
    public static final int INPUT_VCODE_X = Configuration.vCodeInputX;
    public static final int INPUT_VCODE_Y = Configuration.vCodeInputY;
    /**
     * verification code refresh button
     */
    public static final int BUTTON_VCODE_REFRESH_X = Configuration.vCodeRefreshButtonX;
    public static final int BUTTON_VCODE_REFRESH_Y = Configuration.vCodeRefreshButtonY;
    /**
     * custom add money range input box
     */
    public static final int INPUT_CUSTOM_ADD_MONEY_X = Configuration.addMoneyInputX;
    public static final int INPUT_CUSTOM_ADD_MONEY_Y = Configuration.addMoneyInputY;
    /**
     * add money button
     */
    public static final int BUTTON_ADD_MONEY_X = Configuration.addMoneyButtonX;
    public static final int BUTTON_ADD_MONEY_Y = Configuration.addMoneyButtonY;
    /**
     * bid (chujia) button
     */
    public static final int BUTTON_BID_X = Configuration.bidButtonX;
    public static final int BUTTON_BID_Y = Configuration.bidButtonY;
    /**
     * system notification region
     */
    public static final int REGION_SYSTEM_NOTIFICATION_X = Configuration.notificationRegionX;
    public static final int REGION_SYSTEM_NOTIFICATION_Y = Configuration.notificationRegionY;
    public static final int REGION_SYSTEM_NOTIFICATION_WIDTH = Configuration.notificationRegionWidth;
    public static final int REGION_SYSTEM_NOTIFICATION_HEIGHT = Configuration.notificationRegionHeight;
    /**
     * rebid confirm button
     */
    public static final int BUTTON_REBID_CONFIRM_X = Configuration.rebidConfirmButtonX;
    public static final int BUTTON_REBID_CONFIRM_Y = Configuration.rebidConfirmButtonY;
    /**
     * re-enter verification code confirm button
     */
    public static final int BUTTON_RE_ENTER_VCODE_CONFIRM_X = Configuration.reEnterVCodeConfirmButtonX;
    public static final int BUTTON_RE_ENTER_VCODE_CONFIRM_Y = Configuration.reEnterVCodeConfirmButtonY;
    /**
     * request for verification code too often button
     * same as verification code cancel button
     * BUTTON_VCODE_CANCEL_X
     * BUTTON_VCODE_CANCEL_Y
     */
    public static final int BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X = Configuration.vCodeRequestTooOftenConfirmButtonX;
    public static final int BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y = Configuration.vCodeRequestTooOftenConfirmButtonY;
    /**
     * lowest deal money region
     */
    public static final int REGION_LOWEST_DEAL_X = Configuration.lowestDealRegionX;
    public static final int REGION_LOWEST_DEAL_Y = Configuration.lowestDealRegionY;
    public static final int REGION_LOWEST_DEAL_WIDTH = Configuration.lowestDealRegionWidth;
    public static final int REGION_LOWEST_DEAL_HEIGHT = Configuration.lowestDealRegionHeight;

    /**
     * username input
     */
    public static final int INPUT_USERNAME_X = Configuration.usernameInputX;
    public static final int INPUT_USERNAME_Y = Configuration.usernameInputY;

    /**
     * password input
     */
    public static final int INPUT_PASSWORD_X = Configuration.passwordInputX;
    public static final int INPUT_PASSWORD_Y = Configuration.passwordInputY;

    /**
     * username and password submit button
     */
    public static final int BUTTON_LOGIN_X = Configuration.loginButtonX;
    public static final int BUTTON_LOGIN_Y = Configuration.loginButtonY;

    /**
     * add 300 button
     */
    public static final int BUTTON_ADD_300_X = Configuration.add300ButtonX;
    public static final int BUTTON_ADD_300_Y = Configuration.add300ButtonY;

    /**
     * return null if target color is not found
     * @return
     */
    private static Point findLeftTopPosition()
    {
        Point ret = null;
        Mat screen = ImageUtils.screenCapture();

        boolean hasFound = false;
        for (int i = 0; i < screen.height(); ++i)
        {
            if (hasFound)
            {
                break;
            }
            for (int j = 0; j < screen.width(); ++j)
            {
                if (Arrays.toString(topLeftCornerColor).equals(Arrays.toString(screen.get(i, j))) &&
                        Arrays.toString(topLeftCornerColorOffset_10_10).equals(Arrays.toString(screen.get(i+10, j+10))))
                {
                    ret = new Point(j, i);
                    hasFound = true;
                    break;
                }
            }
        }

        origin = ret;
        return ret;
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FlashPositionDetector detector = new FlashPosition.FlashPositionDetector();
        Thread flashPositionDetector = new Thread(detector);
        flashPositionDetector.start();
    }
}
