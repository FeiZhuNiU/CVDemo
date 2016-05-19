package ericyu.chepai;

import ericyu.chepai.utils.Logger;

import java.awt.*;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by lliyu on 2016-05-19.
 */
public class FlashConstants {
    public static final String FLASH_CONFIG_FILE = "flash.properties";
    /**
     * flash related
     */
    static final String LEFTTOP_COLOR_R_KEY = "leftTopColorR";
    static final String LEFTTOP_COLOR_G_KEY = "leftTopColorG";
    static final String LEFTTOP_COLOR_B_KEY = "leftTopColorB";
    static final String LEFTTOP_COLOR_OFFSET_R_KEY = "leftTopColorOffset_10_10_R";
    static final String LEFTTOP_COLOR_OFFSET_G_KEY = "leftTopColorOffset_10_10_G";
    static final String LEFTTOP_COLOR_OFFSET_B_KEY = "leftTopColorOffset_10_10_B";
    //    private static final String LEFTTOP_X_KEY="lt_x";
    static final String LEFTTOP_Y_KEY="lt_y";
    static final String FLASH_WIDTH_KEY="flashWidth";
    static final String FLASH_HEIGHT_KEY="flashHeight";
    static final String REGION_FLASH_STATUS_X_KEY = "flashStatusRegionX";
    static final String REGION_FLASH_STATUS_Y_KEY = "flashStatusRegionY";
    static final String REGION_FLASH_STATUS_WIDTH_KEY = "flashStatusRegionWidth";
    static final String REGION_FLASH_STATUS_HEIGHT_KEY = "flashStatusRegionHeight";
    static final String BUTTON_VCODE_CONFIRM_X_KEY = "vCodeConfirmButtonX";
    static final String BUTTON_VCODE_CONFIRM_Y_KEY = "vCodeConfirmButtonY";
    static final String BUTTON_VCODE_CANCEL_X_KEY = "vCodeCancelButtonX";
    static final String BUTTON_VCODE_CANCEL_Y_KEY = "vCodeCancelButtonY";
    static final String REGION_VCODE_X_KEY = "vCodeRegionX";
    static final String REGION_VCODE_Y_KEY = "vCodeRegionY";
    static final String REGION_VCODE_WIDTH_KEY = "vCodeRegionWidth";
    static final String REGION_VCODE_HEIGHT_KEY = "vCodeRegionHeight";
    static final String INPUT_VCODE_X_KEY = "vCodeInputX";
    static final String INPUT_VCODE_Y_KEY = "vCodeInputY";
    static final String BUTTON_VCODE_REFRESH_X_KEY = "vCodeRefreshButtonX";
    static final String BUTTON_VCODE_REFRESH_Y_KEY = "vCodeRefreshButtonY";
    static final String INPUT_CUSTOM_ADD_MONEY_X_KEY = "addMoneyInputX";
    static final String INPUT_CUSTOM_ADD_MONEY_Y_KEY = "addMoneyInputY";
    static final String BUTTON_ADD_MONEY_X_KEY = "addMoneyButtonX";
    static final String BUTTON_ADD_MONEY_Y_KEY = "addMoneyButtonY";
    static final String BUTTON_BID_X_KEY = "bidButtonX";
    static final String BUTTON_BID_Y_KEY = "bidButtonY";
    static final String REGION_SYSTEM_NOTIFICATION_X_KEY = "notificationRegionX";
    static final String REGION_SYSTEM_NOTIFICATION_Y_KEY = "notificationRegionY";
    static final String REGION_SYSTEM_NOTIFICATION_WIDTH_KEY = "notificationRegionWidth";
    static final String REGION_SYSTEM_NOTIFICATION_HEIGHT_KEY = "notificationRegionHeight";
    static final String BUTTON_REBID_CONFIRM_X_KEY = "rebidConfirmButtonX";
    static final String BUTTON_REBID_CONFIRM_Y_KEY = "rebidConfirmButtonY";
    static final String BUTTON_RE_ENTER_VCODE_CONFIRM_X_KEY = "reEnterVCodeConfirmButtonX";
    static final String BUTTON_RE_ENTER_VCODE_CONFIRM_Y_KEY = "reEnterVCodeConfirmButtonY";
    static final String BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X = "vCodeRequestTooOftenConfirmButtonX";
    static final String BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y = "vCodeRequestTooOftenConfirmButtonY";
    static final String REGION_LOWEST_DEAL_X_KEY = "lowestDealRegionX";
    static final String REGION_LOWEST_DEAL_Y_KEY = "lowestDealRegionY";
    static final String REGION_LOWEST_DEAL_WIDTH_KEY = "lowestDealRegionWidth";
    static final String REGION_LOWEST_DEAL_HEIGHT_KEY = "lowestDealRegionHeight";
    static final String INPUT_USERNAME_X_KEY = "usernameInputX";
    static final String INPUT_USERNAME_Y_KEY = "usernameInputY";
    static final String INPUT_PASSWORD_X_KEY = "passwordInputX";
    static final String INPUT_PASSWORD_Y_KEY = "passwordInputY";
    static final String BUTTON_LOGIN_X_KEY = "loginButtonX";
    static final String BUTTON_LOGIN_Y_KEY = "loginButtonY";
    static final String BUTTON_ADD_300_X_KEY = "add300ButtonX";
    static final String BUTTON_ADD_300_Y_KEY = "add300ButtonY";
    /**
     * flash position related
     */
    public static int lefttopColorR;
    public static int lefttopColorG;
    public static int lefttopColorB;
    public static int lefttopColorOffsetR;
    public static int lefttopColorOffsetG;
    public static int lefttopColorOffsetB;
    public static int leftTopX;
    public static int leftTopY;
    public static int flashWidth;
    public static int flashHeight;
    public static int flashStatusRegionX;
    public static int flashStatusRegionY;
    public static int flashStatusRegionWidth;
    public static int flashStatusRegionHeight;
    public static int vCodeConfirmButtonX;
    public static int vCodeConfirmButtonY;
    public static int vCodeCancelButtonX;
    public static int vCodeCancelButtonY;
    public static int vCodeRegionX;
    public static int vCodeRegionY;
    public static int vCodeRegionWidth;
    public static int vCodeRegionHeight;
    public static int vCodeInputX;
    public static int vCodeInputY;
    public static int vCodeRefreshButtonX;
    public static int vCodeRefreshButtonY;
    public static int addMoneyInputX;
    public static int addMoneyInputY;
    public static int addMoneyButtonX;
    public static int addMoneyButtonY;
    public static int bidButtonX;
    public static int bidButtonY;
    public static int notificationRegionX;
    public static int notificationRegionY;
    public static int notificationRegionWidth;
    public static int notificationRegionHeight;
    public static int rebidConfirmButtonX;
    public static int rebidConfirmButtonY;
    public static int reEnterVCodeConfirmButtonX;
    public static int reEnterVCodeConfirmButtonY;
    public static int vCodeRequestTooOftenConfirmButtonX;
    public static int vCodeRequestTooOftenConfirmButtonY;
    public static int lowestDealRegionX;
    public static int lowestDealRegionY;
    public static int lowestDealRegionWidth;
    public static int lowestDealRegionHeight;
    public static int usernameInputX;
    public static int usernameInputY;
    public static int passwordInputX;
    public static int passwordInputY;
    public static int loginButtonX;
    public static int loginButtonY;
    public static int add300ButtonX;
    public static int add300ButtonY;
    public static HashMap<String, String> flashProperties = new HashMap<>();
    
    static {
        Properties flashProperty = new Properties();
        try
        {
            flashProperty.load(new FileInputStream(FLASH_CONFIG_FILE));

            Enumeration keyEumeration = flashProperty.propertyNames();
            while (keyEumeration.hasMoreElements())
            {
                String key = (String) keyEumeration.nextElement();
                String value = flashProperty.getProperty(key);
                flashProperties.put(key, value);
            }


            lefttopColorR = Integer.parseInt(flashProperties.get(LEFTTOP_COLOR_R_KEY).trim());
            lefttopColorG = Integer.parseInt(flashProperties.get(LEFTTOP_COLOR_G_KEY).trim());
            lefttopColorB = Integer.parseInt(flashProperties.get(LEFTTOP_COLOR_B_KEY).trim());

            lefttopColorOffsetR = Integer.parseInt(flashProperties.get(LEFTTOP_COLOR_OFFSET_R_KEY).trim());
            lefttopColorOffsetG = Integer.parseInt(flashProperties.get(LEFTTOP_COLOR_OFFSET_G_KEY).trim());
            lefttopColorOffsetB = Integer.parseInt(flashProperties.get(LEFTTOP_COLOR_OFFSET_B_KEY).trim());

            flashWidth = Integer.parseInt(flashProperties.get(FLASH_WIDTH_KEY).trim());
            flashHeight = Integer.parseInt(flashProperties.get(FLASH_HEIGHT_KEY).trim());

            int screenWidth= Toolkit.getDefaultToolkit().getScreenSize().width;
            leftTopX = (screenWidth - flashWidth)/2;
            leftTopY = Integer.parseInt(flashProperties.get(LEFTTOP_Y_KEY).trim());



            flashStatusRegionX = Integer.parseInt(flashProperties.get(REGION_FLASH_STATUS_X_KEY).trim());
            flashStatusRegionY = Integer.parseInt(flashProperties.get(REGION_FLASH_STATUS_Y_KEY).trim());
            flashStatusRegionWidth = Integer.parseInt(flashProperties.get(REGION_FLASH_STATUS_WIDTH_KEY).trim());
            flashStatusRegionHeight = Integer.parseInt(flashProperties.get(REGION_FLASH_STATUS_HEIGHT_KEY).trim());

            vCodeConfirmButtonX = Integer.parseInt(flashProperties.get(BUTTON_VCODE_CONFIRM_X_KEY).trim());
            vCodeConfirmButtonY = Integer.parseInt(flashProperties.get(BUTTON_VCODE_CONFIRM_Y_KEY).trim());

            vCodeCancelButtonX = Integer.parseInt(flashProperties.get(BUTTON_VCODE_CANCEL_X_KEY).trim());
            vCodeCancelButtonY = Integer.parseInt(flashProperties.get(BUTTON_VCODE_CANCEL_Y_KEY).trim());

            vCodeRegionX = Integer.parseInt(flashProperties.get(REGION_VCODE_X_KEY).trim());
            vCodeRegionY = Integer.parseInt(flashProperties.get(REGION_VCODE_Y_KEY).trim());
            vCodeRegionWidth = Integer.parseInt(flashProperties.get(REGION_VCODE_WIDTH_KEY).trim());
            vCodeRegionHeight = Integer.parseInt(flashProperties.get(REGION_VCODE_HEIGHT_KEY).trim());

            vCodeInputX = Integer.parseInt(flashProperties.get(INPUT_VCODE_X_KEY).trim());
            vCodeInputY = Integer.parseInt(flashProperties.get(INPUT_VCODE_Y_KEY).trim());

            vCodeRefreshButtonX = Integer.parseInt(flashProperties.get(BUTTON_VCODE_REFRESH_X_KEY).trim());
            vCodeRefreshButtonY = Integer.parseInt(flashProperties.get(BUTTON_VCODE_REFRESH_Y_KEY).trim());

            addMoneyInputX = Integer.parseInt(flashProperties.get(INPUT_CUSTOM_ADD_MONEY_X_KEY).trim());
            addMoneyInputY = Integer.parseInt(flashProperties.get(INPUT_CUSTOM_ADD_MONEY_Y_KEY).trim());

            addMoneyButtonX = Integer.parseInt(flashProperties.get(BUTTON_ADD_MONEY_X_KEY).trim());
            addMoneyButtonY = Integer.parseInt(flashProperties.get(BUTTON_ADD_MONEY_Y_KEY).trim());

            bidButtonX = Integer.parseInt(flashProperties.get(BUTTON_BID_X_KEY).trim());
            bidButtonY = Integer.parseInt(flashProperties.get(BUTTON_BID_Y_KEY).trim());

            notificationRegionX = Integer.parseInt(flashProperties.get(REGION_SYSTEM_NOTIFICATION_X_KEY).trim());
            notificationRegionY = Integer.parseInt(flashProperties.get(REGION_SYSTEM_NOTIFICATION_Y_KEY).trim());
            notificationRegionWidth = Integer.parseInt(flashProperties.get(REGION_SYSTEM_NOTIFICATION_WIDTH_KEY).trim());
            notificationRegionHeight = Integer.parseInt(flashProperties.get(REGION_SYSTEM_NOTIFICATION_HEIGHT_KEY).trim());

            rebidConfirmButtonX = Integer.parseInt(flashProperties.get(BUTTON_REBID_CONFIRM_X_KEY).trim());
            rebidConfirmButtonY = Integer.parseInt(flashProperties.get(BUTTON_REBID_CONFIRM_Y_KEY).trim());

            reEnterVCodeConfirmButtonX = Integer.parseInt(flashProperties.get(BUTTON_RE_ENTER_VCODE_CONFIRM_X_KEY).trim());
            reEnterVCodeConfirmButtonY = Integer.parseInt(flashProperties.get(BUTTON_RE_ENTER_VCODE_CONFIRM_Y_KEY).trim());

            vCodeRequestTooOftenConfirmButtonX = Integer.parseInt(flashProperties.get(BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X).trim());
            vCodeRequestTooOftenConfirmButtonY = Integer.parseInt(flashProperties.get(BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y).trim());

            lowestDealRegionX = Integer.parseInt(flashProperties.get(REGION_LOWEST_DEAL_X_KEY).trim());
            lowestDealRegionY = Integer.parseInt(flashProperties.get(REGION_LOWEST_DEAL_Y_KEY).trim());
            lowestDealRegionWidth = Integer.parseInt(flashProperties.get(REGION_LOWEST_DEAL_WIDTH_KEY).trim());
            lowestDealRegionHeight = Integer.parseInt(flashProperties.get(REGION_LOWEST_DEAL_HEIGHT_KEY).trim());

            usernameInputX = Integer.parseInt(flashProperties.get(INPUT_USERNAME_X_KEY).trim());
            usernameInputY = Integer.parseInt(flashProperties.get(INPUT_USERNAME_Y_KEY).trim());

            passwordInputX = Integer.parseInt(flashProperties.get(INPUT_PASSWORD_X_KEY).trim());
            passwordInputY = Integer.parseInt(flashProperties.get(INPUT_PASSWORD_Y_KEY).trim());

            loginButtonX = Integer.parseInt(flashProperties.get(BUTTON_LOGIN_X_KEY).trim());
            loginButtonY = Integer.parseInt(flashProperties.get(BUTTON_LOGIN_Y_KEY).trim());

            add300ButtonX = Integer.parseInt(flashProperties.get(BUTTON_ADD_300_X_KEY).trim());
            add300ButtonY = Integer.parseInt(flashProperties.get(BUTTON_ADD_300_Y_KEY).trim());

        }
        catch (Exception e)
        {
            Logger.log(Logger.Level.ERROR, FlashStatusDetector.Status.NONE, "property file does not exits!", e);
        }
    }
    
}
