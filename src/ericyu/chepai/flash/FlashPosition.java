package ericyu.chepai.flash;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.iknow.image.ImageUtils;
import ericyu.chepai.FlashConstants;
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
        topLeftCornerColor = new double[]{FlashConstants.lefttopColorB,
                FlashConstants.lefttopColorG,
                FlashConstants.lefttopColorR};
        topLeftCornerColorOffset_10_10 = new double[]{FlashConstants.lefttopColorOffsetB,
                FlashConstants.lefttopColorOffsetG,
                FlashConstants.lefttopColorOffsetR};
        origin = new Point(FlashConstants.leftTopX, FlashConstants.leftTopY);

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
    public static final int FLASH_WIDTH     = FlashConstants.flashWidth;
    public static final int FLASH_HEIGHT    = FlashConstants.flashHeight;

    /**
     *  right part of the flash
     *  used for detect flash status
     */
    public static final int REGION_FLASH_RIGHT_PART_X = FlashConstants.flashStatusRegionX;
    public static final int REGION_FLASH_RIGHT_PART_Y = FlashConstants.flashStatusRegionY;
    public static final int REGION_FLASH_RIGHT_PART_WIDTH = FlashConstants.flashStatusRegionWidth;
    public static final int REGION_FLASH_RIGHT_PART_HEIGHT = FlashConstants.flashStatusRegionHeight;
    /**
     * verification code confirm button
     */
    public static final int BUTTON_VCODE_CONFIRM_X = FlashConstants.vCodeConfirmButtonX;
    public static final int BUTTON_VCODE_CONFIRM_Y = FlashConstants.vCodeConfirmButtonY;
    /**
     * verification code cancel button
     */
    public static final int BUTTON_VCODE_CANCEL_X = FlashConstants.vCodeCancelButtonX;
    public static final int BUTTON_VCODE_CANCEL_Y = FlashConstants.vCodeCancelButtonY;
    /**
     * verification code image region
     */
    public static final int REGION_VCODE_X = FlashConstants.vCodeRegionX;
    public static final int REGION_VCODE_Y = FlashConstants.vCodeRegionY;
    public static final int REGION_VCODE_WIDTH = FlashConstants.vCodeRegionWidth;
    public static final int REGION_VCODE_HEIGHT = FlashConstants.vCodeRegionHeight;
    /**
     * verification code input box
     */
    public static final int INPUT_VCODE_X = FlashConstants.vCodeInputX;
    public static final int INPUT_VCODE_Y = FlashConstants.vCodeInputY;
    /**
     * verification code refresh button
     */
    public static final int BUTTON_VCODE_REFRESH_X = FlashConstants.vCodeRefreshButtonX;
    public static final int BUTTON_VCODE_REFRESH_Y = FlashConstants.vCodeRefreshButtonY;
    /**
     * custom add money range input box
     */
    public static final int INPUT_CUSTOM_ADD_MONEY_X = FlashConstants.addMoneyInputX;
    public static final int INPUT_CUSTOM_ADD_MONEY_Y = FlashConstants.addMoneyInputY;
    /**
     * add money button
     */
    public static final int BUTTON_ADD_MONEY_X = FlashConstants.addMoneyButtonX;
    public static final int BUTTON_ADD_MONEY_Y = FlashConstants.addMoneyButtonY;
    /**
     * bid (chujia) button
     */
    public static final int BUTTON_BID_X = FlashConstants.bidButtonX;
    public static final int BUTTON_BID_Y = FlashConstants.bidButtonY;
    /**
     * system notification region
     */
    public static final int REGION_SYSTEM_NOTIFICATION_X = FlashConstants.notificationRegionX;
    public static final int REGION_SYSTEM_NOTIFICATION_Y = FlashConstants.notificationRegionY;
    public static final int REGION_SYSTEM_NOTIFICATION_WIDTH = FlashConstants.notificationRegionWidth;
    public static final int REGION_SYSTEM_NOTIFICATION_HEIGHT = FlashConstants.notificationRegionHeight;
    /**
     * rebid confirm button
     */
    public static final int BUTTON_REBID_CONFIRM_X = FlashConstants.rebidConfirmButtonX;
    public static final int BUTTON_REBID_CONFIRM_Y = FlashConstants.rebidConfirmButtonY;
    /**
     * re-enter verification code confirm button
     */
    public static final int BUTTON_RE_ENTER_VCODE_CONFIRM_X = FlashConstants.reEnterVCodeConfirmButtonX;
    public static final int BUTTON_RE_ENTER_VCODE_CONFIRM_Y = FlashConstants.reEnterVCodeConfirmButtonY;
    /**
     * request for verification code too often button
     * same as verification code cancel button
     * BUTTON_VCODE_CANCEL_X
     * BUTTON_VCODE_CANCEL_Y
     */
    public static final int BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X = FlashConstants.vCodeRequestTooOftenConfirmButtonX;
    public static final int BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y = FlashConstants.vCodeRequestTooOftenConfirmButtonY;
    /**
     * lowest deal money region
     */
    public static final int REGION_LOWEST_DEAL_X = FlashConstants.lowestDealRegionX;
    public static final int REGION_LOWEST_DEAL_Y = FlashConstants.lowestDealRegionY;
    public static final int REGION_LOWEST_DEAL_WIDTH = FlashConstants.lowestDealRegionWidth;
    public static final int REGION_LOWEST_DEAL_HEIGHT = FlashConstants.lowestDealRegionHeight;

    /**
     * username input
     */
    public static final int INPUT_USERNAME_X = FlashConstants.usernameInputX;
    public static final int INPUT_USERNAME_Y = FlashConstants.usernameInputY;

    /**
     * password input
     */
    public static final int INPUT_PASSWORD_X = FlashConstants.passwordInputX;
    public static final int INPUT_PASSWORD_Y = FlashConstants.passwordInputY;

    /**
     * username and password submit button
     */
    public static final int BUTTON_LOGIN_X = FlashConstants.loginButtonX;
    public static final int BUTTON_LOGIN_Y = FlashConstants.loginButtonY;

    /**
     * add 300 button
     */
    public static final int BUTTON_ADD_300_X = FlashConstants.add300ButtonX;
    public static final int BUTTON_ADD_300_Y = FlashConstants.add300ButtonY;

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
