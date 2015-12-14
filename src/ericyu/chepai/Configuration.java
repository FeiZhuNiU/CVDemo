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
    private static final String ADD_MONEY_RANGE_KEY = "addMoneyRange";
    private static final String V_CODE_CONFIRM_HOUR_KEY = "vCodeConfirmTimeHour";
    private static final String V_CODE_CONFIRM_MINUTE_KEY = "vCodeConfirmTimeMinute";
    private static final String V_CODE_CONFIRM_SECOND_KEY = "vCodeConfirmTimeSecond";
    private static final String ADD_MONEY_SECOND_KEY = "addMoneyTimeSecond";
    private static final String LEFTTOP_COLOR_R_KEY = "teftTopColorR";
    private static final String LEFTTOP_COLOR_G_KEY = "teftTopColorG";
    private static final String LEFTTOP_COLOR_B_KEY = "teftTopColorB";
    private static final String LEFTTOP_COLOR_OFFSET_R_KEY = "teftTopColorOffset_10_10_R";
    private static final String LEFTTOP_COLOR_OFFSET_G_KEY = "teftTopColorOffset_10_10_G";
    private static final String LEFTTOP_COLOR_OFFSET_B_KEY = "teftTopColorOffset_10_10_B";
    private static final String LATEST_BID_TIME_SECOND_KEY = "latestBidTimeSecond";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String LEFTTOP_X_KEY="lt_x";
    private static final String LEFTTOP_Y_KEY="lt_y";


    public static int leftTopX;
    public static int leftTopY;

    public static int addMoneyRange;
    public static int vCodeConfirmHour;
    public static int vCodeConfirmMinute;
    public static int vCodeConfirmSecond;
    public static int addMoneySecond;
    public static int lefttopColorR;
    public static int lefttopColorG;
    public static int lefttopColorB;
    public static int lefttopColorOffsetR;
    public static int lefttopColorOffsetG;
    public static int lefttopColorOffsetB;
    public static int latestBidTimeSecond;
    public static String username;
    public static String password;


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

            addMoneyRange = Integer.parseInt(properties.get(ADD_MONEY_RANGE_KEY).trim());
            vCodeConfirmHour = Integer.parseInt(properties.get(V_CODE_CONFIRM_HOUR_KEY).trim());
            vCodeConfirmMinute = Integer.parseInt(properties.get(V_CODE_CONFIRM_MINUTE_KEY).trim());
            vCodeConfirmSecond = Integer.parseInt(properties.get(V_CODE_CONFIRM_SECOND_KEY).trim());
            addMoneySecond = Integer.parseInt(properties.get(ADD_MONEY_SECOND_KEY).trim());

            latestBidTimeSecond = Integer.parseInt(properties.get(LATEST_BID_TIME_SECOND_KEY).trim());

            lefttopColorR = Integer.parseInt(properties.get(LEFTTOP_COLOR_R_KEY).trim());
            lefttopColorG = Integer.parseInt(properties.get(LEFTTOP_COLOR_G_KEY).trim());
            lefttopColorB = Integer.parseInt(properties.get(LEFTTOP_COLOR_B_KEY).trim());

            lefttopColorOffsetR = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_R_KEY).trim());
            lefttopColorOffsetG = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_G_KEY).trim());
            lefttopColorOffsetB = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_B_KEY).trim());

            username = properties.get(USERNAME_KEY).trim();
            password = properties.get(PASSWORD_KEY).trim();

            leftTopX = Integer.parseInt(properties.get(LEFTTOP_X_KEY).trim());
            leftTopY = Integer.parseInt(properties.get(LEFTTOP_Y_KEY).trim());

        }
        catch (Exception e)
        {
            Logger.log(Logger.Level.ERROR, FlashStatusDetector.Status.NONE, "property file does not exits!");
        }
    }
}
