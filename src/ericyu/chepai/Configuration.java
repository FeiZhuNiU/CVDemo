package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.chepai.flash.FlashStatusDetector;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class Configuration
{

    public static String username;
    public static String password;

    /**
     * bid strategy related
     */
    public static int addMoneyRange;
    public static int bidTimeHour;
    public static int bidTimeMinute;
    public static int vCodeConfirmSecond;
    public static int addMoneySecond;
    public static int latestBidTimeSecond;
    public static int bidDiff;

    public static boolean skipVCode;


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

    public static int flashRightPartX;
    public static int flashRightPartY;
    public static int flashRightPartWidth;
    public static int flashRightPartHeight;

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

    public static int startBid;
    public static int exitTimeHour;
    public static int exitTimeMinute;




    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private static final String SKIPVCODE_KEY = "skipVCode";
    /**
     * bid strategy related
     */
    private static final String ADD_MONEY_RANGE_KEY = "addMoneyRange";
    private static final String BID_TIME_HOUR_KEY = "bidTimeHour";
    private static final String BID_TIME_MINUTE_KEY = "bidTimeMinute";
    private static final String V_CODE_CONFIRM_SECOND_KEY = "vCodeConfirmTimeSecond";
    private static final String ADD_MONEY_SECOND_KEY = "addMoneyTimeSecond";
    private static final String LATEST_BID_TIME_SECOND_KEY = "latestBidTimeSecond";
    private static final String BID_DIFF_KEY="bidDiff";

    /**
     * flash related
     */
    private static final String LEFTTOP_COLOR_R_KEY = "leftTopColorR";
    private static final String LEFTTOP_COLOR_G_KEY = "leftTopColorG";
    private static final String LEFTTOP_COLOR_B_KEY = "leftTopColorB";
    private static final String LEFTTOP_COLOR_OFFSET_R_KEY = "leftTopColorOffset_10_10_R";
    private static final String LEFTTOP_COLOR_OFFSET_G_KEY = "leftTopColorOffset_10_10_G";
    private static final String LEFTTOP_COLOR_OFFSET_B_KEY = "leftTopColorOffset_10_10_B";

    private static final String LEFTTOP_X_KEY="lt_x";
    private static final String LEFTTOP_Y_KEY="lt_y";

    private static final String FLASH_WIDTH_KEY="flashWidth";
    private static final String FLASH_HEIGHT_KEY="flashHeight";

    private static final String REGION_FLASH_RIGHT_PART_X_KEY="flashRightPartX";
    private static final String REGION_FLASH_RIGHT_PART_Y_KEY="flashRightPartY";
    private static final String REGION_FLASH_RIGHT_PART_WIDTH_KEY="flashRightPartWidth";
    private static final String REGION_FLASH_RIGHT_PART_HEIGHT_KEY="flashRightPartHeight";

    private static final String BUTTON_VCODE_CONFIRM_X_KEY = "vCodeConfirmButtonX";
    private static final String BUTTON_VCODE_CONFIRM_Y_KEY = "vCodeConfirmButtonY";

    private static final String BUTTON_VCODE_CANCEL_X_KEY = "vCodeCancelButtonX";
    private static final String BUTTON_VCODE_CANCEL_Y_KEY = "vCodeCancelButtonY";

    private static final String REGION_VCODE_X_KEY = "vCodeRegionX";
    private static final String REGION_VCODE_Y_KEY = "vCodeRegionY";
    private static final String REGION_VCODE_WIDTH_KEY = "vCodeRegionWidth";
    private static final String REGION_VCODE_HEIGHT_KEY = "vCodeRegionHeight";

    private static final String INPUT_VCODE_X_KEY = "vCodeInputX";
    private static final String INPUT_VCODE_Y_KEY = "vCodeInputY";

    private static final String BUTTON_VCODE_REFRESH_X_KEY = "vCodeRefreshButtonX";
    private static final String BUTTON_VCODE_REFRESH_Y_KEY = "vCodeRefreshButtonY";

    private static final String INPUT_CUSTOM_ADD_MONEY_X_KEY = "addMoneyInputX";
    private static final String INPUT_CUSTOM_ADD_MONEY_Y_KEY = "addMoneyInputY";

    private static final String BUTTON_ADD_MONEY_X_KEY = "addMoneyButtonX";
    private static final String BUTTON_ADD_MONEY_Y_KEY = "addMoneyButtonY";

    private static final String BUTTON_BID_X_KEY = "bidButtonX";
    private static final String BUTTON_BID_Y_KEY = "bidButtonY";

    private static final String REGION_SYSTEM_NOTIFICATION_X_KEY = "notificationRegionX";
    private static final String REGION_SYSTEM_NOTIFICATION_Y_KEY = "notificationRegionY";
    private static final String REGION_SYSTEM_NOTIFICATION_WIDTH_KEY = "notificationRegionWidth";
    private static final String REGION_SYSTEM_NOTIFICATION_HEIGHT_KEY = "notificationRegionHeight";

    private static final String BUTTON_REBID_CONFIRM_X_KEY = "rebidConfirmButtonX";
    private static final String BUTTON_REBID_CONFIRM_Y_KEY = "rebidConfirmButtonY";

    private static final String BUTTON_RE_ENTER_VCODE_CONFIRM_X_KEY = "reEnterVCodeConfirmButtonX";
    private static final String BUTTON_RE_ENTER_VCODE_CONFIRM_Y_KEY = "reEnterVCodeConfirmButtonY";

    private static final String BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X = "vCodeRequestTooOftenConfirmButtonX";
    private static final String BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y = "vCodeRequestTooOftenConfirmButtonY";

    private static final String REGION_LOWEST_DEAL_X_KEY = "lowestDealRegionX";
    private static final String REGION_LOWEST_DEAL_Y_KEY = "lowestDealRegionY";
    private static final String REGION_LOWEST_DEAL_WIDTH_KEY = "lowestDealRegionWidth";
    private static final String REGION_LOWEST_DEAL_HEIGHT_KEY = "lowestDealRegionHeight";

    private static final String INPUT_USERNAME_X_KEY = "usernameInputX";
    private static final String INPUT_USERNAME_Y_KEY = "usernameInputY";

    private static final String INPUT_PASSWORD_X_KEY = "passwordInputX";
    private static final String INPUT_PASSWORD_Y_KEY = "passwordInputY";

    private static final String BUTTON_LOGIN_X_KEY = "loginButtonX";
    private static final String BUTTON_LOGIN_Y_KEY = "loginButtonY";

    private static final String BUTTON_ADD_300_X_KEY = "add300ButtonX";
    private static final String BUTTON_ADD_300_Y_KEY = "add300ButtonY";

    private static final String START_BID_KEY="startBid";
    private static final String EXIT_TIME_MINUTE_KEY="exitTimeMinute";
    private static final String EXIT_TIME_HOUR_KEY="exitTimeHour";


    public static HashMap<String, String> properties = new HashMap<>();


    static
    {
        Properties property = new Properties();
        try
        {
            property.load(new FileInputStream("strategy.properties"));
            Enumeration keyEumeration = property.propertyNames();
            while (keyEumeration.hasMoreElements())
            {
                String key = (String) keyEumeration.nextElement();
                String value = property.getProperty(key);
                properties.put(key, value);
            }

            username = properties.get(USERNAME_KEY).trim();
            password = properties.get(PASSWORD_KEY).trim();
            skipVCode = Boolean.parseBoolean(properties.get(SKIPVCODE_KEY).trim());

            addMoneyRange = Integer.parseInt(properties.get(ADD_MONEY_RANGE_KEY).trim());
            bidTimeHour = Integer.parseInt(properties.get(BID_TIME_HOUR_KEY).trim());
            bidTimeMinute = Integer.parseInt(properties.get(BID_TIME_MINUTE_KEY).trim());
            vCodeConfirmSecond = Integer.parseInt(properties.get(V_CODE_CONFIRM_SECOND_KEY).trim());
            addMoneySecond = Integer.parseInt(properties.get(ADD_MONEY_SECOND_KEY).trim());
            latestBidTimeSecond = Integer.parseInt(properties.get(LATEST_BID_TIME_SECOND_KEY).trim());
            bidDiff = Integer.parseInt(properties.get(BID_DIFF_KEY).trim());

            lefttopColorR = Integer.parseInt(properties.get(LEFTTOP_COLOR_R_KEY).trim());
            lefttopColorG = Integer.parseInt(properties.get(LEFTTOP_COLOR_G_KEY).trim());
            lefttopColorB = Integer.parseInt(properties.get(LEFTTOP_COLOR_B_KEY).trim());

            lefttopColorOffsetR = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_R_KEY).trim());
            lefttopColorOffsetG = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_G_KEY).trim());
            lefttopColorOffsetB = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_B_KEY).trim());

            leftTopX = Integer.parseInt(properties.get(LEFTTOP_X_KEY).trim());
            leftTopY = Integer.parseInt(properties.get(LEFTTOP_Y_KEY).trim());

            flashWidth = Integer.parseInt(properties.get(FLASH_WIDTH_KEY).trim());
            flashHeight = Integer.parseInt(properties.get(FLASH_HEIGHT_KEY).trim());

            flashRightPartX = Integer.parseInt(properties.get(REGION_FLASH_RIGHT_PART_X_KEY).trim());
            flashRightPartY = Integer.parseInt(properties.get(REGION_FLASH_RIGHT_PART_Y_KEY).trim());
            flashRightPartWidth = Integer.parseInt(properties.get(REGION_FLASH_RIGHT_PART_WIDTH_KEY).trim());
            flashRightPartHeight = Integer.parseInt(properties.get(REGION_FLASH_RIGHT_PART_HEIGHT_KEY).trim());

            vCodeConfirmButtonX = Integer.parseInt(properties.get(BUTTON_VCODE_CONFIRM_X_KEY).trim());
            vCodeConfirmButtonY = Integer.parseInt(properties.get(BUTTON_VCODE_CONFIRM_Y_KEY).trim());

            vCodeCancelButtonX = Integer.parseInt(properties.get(BUTTON_VCODE_CANCEL_X_KEY).trim());
            vCodeCancelButtonY = Integer.parseInt(properties.get(BUTTON_VCODE_CANCEL_Y_KEY).trim());

            vCodeRegionX = Integer.parseInt(properties.get(REGION_VCODE_X_KEY).trim());
            vCodeRegionY = Integer.parseInt(properties.get(REGION_VCODE_Y_KEY).trim());
            vCodeRegionWidth = Integer.parseInt(properties.get(REGION_VCODE_WIDTH_KEY).trim());
            vCodeRegionHeight = Integer.parseInt(properties.get(REGION_VCODE_HEIGHT_KEY).trim());

            vCodeInputX = Integer.parseInt(properties.get(INPUT_VCODE_X_KEY).trim());
            vCodeInputY = Integer.parseInt(properties.get(INPUT_VCODE_Y_KEY).trim());

            vCodeRefreshButtonX = Integer.parseInt(properties.get(BUTTON_VCODE_REFRESH_X_KEY).trim());
            vCodeRefreshButtonY = Integer.parseInt(properties.get(BUTTON_VCODE_REFRESH_Y_KEY).trim());

            addMoneyInputX = Integer.parseInt(properties.get(INPUT_CUSTOM_ADD_MONEY_X_KEY).trim());
            addMoneyInputY = Integer.parseInt(properties.get(INPUT_CUSTOM_ADD_MONEY_Y_KEY).trim());

            addMoneyButtonX = Integer.parseInt(properties.get(BUTTON_ADD_MONEY_X_KEY).trim());
            addMoneyButtonY = Integer.parseInt(properties.get(BUTTON_ADD_MONEY_Y_KEY).trim());

            bidButtonX = Integer.parseInt(properties.get(BUTTON_BID_X_KEY).trim());
            bidButtonY = Integer.parseInt(properties.get(BUTTON_BID_Y_KEY).trim());

            notificationRegionX = Integer.parseInt(properties.get(REGION_SYSTEM_NOTIFICATION_X_KEY).trim());
            notificationRegionY = Integer.parseInt(properties.get(REGION_SYSTEM_NOTIFICATION_Y_KEY).trim());
            notificationRegionWidth = Integer.parseInt(properties.get(REGION_SYSTEM_NOTIFICATION_WIDTH_KEY).trim());
            notificationRegionHeight = Integer.parseInt(properties.get(REGION_SYSTEM_NOTIFICATION_HEIGHT_KEY).trim());

            rebidConfirmButtonX = Integer.parseInt(properties.get(BUTTON_REBID_CONFIRM_X_KEY).trim());
            rebidConfirmButtonY = Integer.parseInt(properties.get(BUTTON_REBID_CONFIRM_Y_KEY).trim());

            reEnterVCodeConfirmButtonX = Integer.parseInt(properties.get(BUTTON_RE_ENTER_VCODE_CONFIRM_X_KEY).trim());
            reEnterVCodeConfirmButtonY = Integer.parseInt(properties.get(BUTTON_RE_ENTER_VCODE_CONFIRM_Y_KEY).trim());

            vCodeRequestTooOftenConfirmButtonX = Integer.parseInt(properties.get(BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_X).trim());
            vCodeRequestTooOftenConfirmButtonY = Integer.parseInt(properties.get(BUTTON_VCODE_REQUEST_TOO_OFTEN_CONFIRM_Y).trim());

            lowestDealRegionX = Integer.parseInt(properties.get(REGION_LOWEST_DEAL_X_KEY).trim());
            lowestDealRegionY = Integer.parseInt(properties.get(REGION_LOWEST_DEAL_Y_KEY).trim());
            lowestDealRegionWidth = Integer.parseInt(properties.get(REGION_LOWEST_DEAL_WIDTH_KEY).trim());
            lowestDealRegionHeight = Integer.parseInt(properties.get(REGION_LOWEST_DEAL_HEIGHT_KEY).trim());

            usernameInputX = Integer.parseInt(properties.get(INPUT_USERNAME_X_KEY).trim());
            usernameInputY = Integer.parseInt(properties.get(INPUT_USERNAME_Y_KEY).trim());

            passwordInputX = Integer.parseInt(properties.get(INPUT_PASSWORD_X_KEY).trim());
            passwordInputY = Integer.parseInt(properties.get(INPUT_PASSWORD_Y_KEY).trim());

            loginButtonX = Integer.parseInt(properties.get(BUTTON_LOGIN_X_KEY).trim());
            loginButtonY = Integer.parseInt(properties.get(BUTTON_LOGIN_Y_KEY).trim());

            add300ButtonX = Integer.parseInt(properties.get(BUTTON_ADD_300_X_KEY).trim());
            add300ButtonY = Integer.parseInt(properties.get(BUTTON_ADD_300_Y_KEY).trim());

            startBid = Integer.parseInt(properties.get(START_BID_KEY).trim());
            exitTimeMinute = Integer.parseInt(properties.get(EXIT_TIME_MINUTE_KEY).trim());
            exitTimeHour = Integer.parseInt(properties.get(EXIT_TIME_HOUR_KEY).trim());


        }
        catch (Exception e)
        {
            Logger.log(Logger.Level.ERROR, FlashStatusDetector.Status.NONE, "property file does not exits!");
        }
    }
}
