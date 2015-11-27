package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/25/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import java.awt.*;
import java.util.Arrays;


/**
 * Flash position, and all button positions on that flash
 *
 * Constant naming rule:
 * INPUT_...
 * REGION_...
 * BUTTON_...
 *
 * It will try to find the position in the default constructor by pre-configured (topLeftCornerColor)
 * if fails, the origin will be null
 */
public class FlashPosition
{
    /**
     * the order should be b/g/r
     */
    private static final double[] topLeftCornerColor = {43, 31.0, 25};

    /**
     * coordinate of top left corner
     */
    public Point origin;

    /**
     * This constructor meant to get to know the position where the flash locate on screen
     */
    public FlashPosition()
    {
        origin = findPositionOfTargetColor(topLeftCornerColor);
    }

    /**
     * flash
     */
    public static final int FLASH_WIDTH     = 900;
    public static final int FLASH_HEIGHT    = 700;
    /**
     * verification code confirm button
     */
    public static final int BUTTON_VERIFICATION_CODE_CONFIRM_X = 555;
    public static final int BUTTON_VERIFICATION_CODE_CONFIRM_Y = 465;
    /**
     * verification code cancel button
     */
    public static final int BUTTON_VERIFICATION_CODE_CANCEL_X = 750;
    public static final int BUTTON_VERIFICATION_CODE_CANCEL_Y = 465;
    /**
     * verification code image region
     */
    public static final int REGION_VERIFICATION_CODE_LT_X = 747;
    public static final int REGION_VERIFICATION_CODE_LT_Y = 365;
    public static final int REGION_VERIFICATION_CODE_WIDTH = 105;
    public static final int REGION_VERIFICATION_CODE_HEIGHT = 28;
    /**
     * verification code input box
     */
    public static final int INPUT_VERIFICATION_X = 660;
    public static final int INPUT_VERIFICATION_Y = 380;
    /**
     * verification code refresh button
     */
    public static final int BUTTON_VERIFICATION_REFRESH_X = 800;
    public static final int BUTTON_VERIFICATION_REFRESH_Y = 375;
    /**
     * custom add money range input box
     */
    public static final int INPUT_CUSTOM_ADD_MONEY_X = 690;
    public static final int INPUT_CUSTOM_ADD_MONEY_Y = 298;
    /**
     * add money button
     */
    public static final int BUTTON_ADD_MONEY_X = 800;
    public static final int BUTTON_ADD_MONEY_Y = 296;
    /**
     * bid (chujia) button
     */
    public static final int BUTTON_BID_X = 800;
    public static final int BUTTON_BID_Y = 400;
    /**
     * system notification region
     */
    public static final int REGION_SYSTEM_NOTIFICATION_X = 450;
    public static final int REGION_SYSTEM_NOTIFICATION_Y = 295;
    public static final int REGION_SYSTEM_NOTIFICATION_WIDTH = 400;
    public static final int REGION_SYSTEM_NOTIFICATION_HEIGHT = 100;
    /**
     * rebid confirm button
     */
    public static final int BUTTON_REBID_CONFIRM_X = 663;
    public static final int BUTTON_REBID_CONFIRM_Y = 460;
    /**
     * re-enter verification code confirm button
     */
    public static final int BUTTON_RE_ENTER_VERIFICATION_CONFIRM_X = 663;
    public static final int BUTTON_RE_ENTER_VERIFICATION_CONFIRM_Y = 460;
    /**
     * lowest deal money region
     */
    public static final int REGION_LOWEST_DEAL_X = 640;
    public static final int REGION_LOWEST_DEAL_Y = 245;
    public static final int REGION_LOWEST_DEAL_WIDTH = 55;
    public static final int REGION_LOWEST_DEAL_HEIGHT = 23;

    /**
     * username input
     */
    public static final int INPUT_USERNAME_X = 600;
    public static final int INPUT_USERNAME_Y = 204;

    /**
     * password input
     */
    public static final int INPUT_PASSWORD_X = 600;
    public static final int INPUT_PASSWORD_Y = 275;

    /**
     * username and password submit button
     */
    public static final int BUTTON_SUBMIT_X = 660;
    public static final int BUTTON_SUBMIT_Y = 345;


    /**
     * return null if target color is not found
     * @return
     */
    private Point findPositionOfTargetColor(double[] targetColor)
    {
        Point ret = null;
        String image = "findPosition.bmp";

        ImageUtils.screenCapture(image);
        Mat screen = Imgcodecs.imread(image);

        boolean hasFound = false;
        for (int i = 0; i < screen.height(); ++i)
        {
            if (hasFound)
            {
                break;
            }
            for (int j = 0; j < screen.width(); ++j)
            {
                if (Arrays.toString(targetColor).equals(Arrays.toString(screen.get(i, j))))
                {
                    ret = new Point(j, i);
                    hasFound = true;
                    break;
                }
            }
        }
        ImageUtils.deleteImage(image);

        origin = ret;
        return ret;
    }
}
