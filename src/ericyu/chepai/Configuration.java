package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.chepai.Logger;
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
    private static final String LATEST_BID_SECOND_KEY = "latestBidTimeSecond";
    private static final String LEFTTOP_COLOR_R_KEY = "teftTopColorR";
    private static final String LEFTTOP_COLOR_G_KEY = "teftTopColorG";
    private static final String LEFTTOP_COLOR_B_KEY = "teftTopColorB";
    private static final String LEFTTOP_COLOR_OFFSET_R_KEY = "teftTopColorOffset_10_10_R";
    private static final String LEFTTOP_COLOR_OFFSET_G_KEY = "teftTopColorOffset_10_10_G";
    private static final String LEFTTOP_COLOR_OFFSET_B_KEY = "teftTopColorOffset_10_10_B";
    private static final String LATEST_BID_TIME_KEY = "latestBidTime";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";


    public static int ADD_MONEY_RANGE;
    public static int V_CODE_CONFIRM_HOUR;
    public static int V_CODE_CONFIRM_MINUTE;
    public static int V_CODE_CONFIRM_SECOND;
    public static int ADD_MONEY_SECOND;
    public static int LATEST_BID_SECOND;
    public static int LEFTTOP_COLOR_R;
    public static int LEFTTOP_COLOR_G;
    public static int LEFTTOP_COLOR_B;
    public static int LEFTTOP_COLOR_OFFSET_R;
    public static int LEFTTOP_COLOR_OFFSET_G;
    public static int LEFTTOP_COLOR_OFFSET_B;
    public static int LATEST_BID_TIME;
    public static String USERNAME;
    public static String PASSWORD;


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

            ADD_MONEY_RANGE = Integer.parseInt(properties.get(ADD_MONEY_RANGE_KEY).trim());
            V_CODE_CONFIRM_HOUR = Integer.parseInt(properties.get(V_CODE_CONFIRM_HOUR_KEY).trim());
            V_CODE_CONFIRM_MINUTE = Integer.parseInt(properties.get(V_CODE_CONFIRM_MINUTE_KEY).trim());
            V_CODE_CONFIRM_SECOND = Integer.parseInt(properties.get(V_CODE_CONFIRM_SECOND_KEY).trim());
            ADD_MONEY_SECOND = Integer.parseInt(properties.get(ADD_MONEY_SECOND_KEY).trim());
            LATEST_BID_SECOND = Integer.parseInt(properties.get(LATEST_BID_SECOND_KEY).trim());

            LATEST_BID_TIME = Integer.parseInt(properties.get(LATEST_BID_TIME_KEY).trim());

            LEFTTOP_COLOR_R = Integer.parseInt(properties.get(LEFTTOP_COLOR_R_KEY).trim());
            LEFTTOP_COLOR_G = Integer.parseInt(properties.get(LEFTTOP_COLOR_G_KEY).trim());
            LEFTTOP_COLOR_B = Integer.parseInt(properties.get(LEFTTOP_COLOR_B_KEY).trim());

            LEFTTOP_COLOR_OFFSET_R = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_R_KEY).trim());
            LEFTTOP_COLOR_OFFSET_G = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_G_KEY).trim());
            LEFTTOP_COLOR_OFFSET_B = Integer.parseInt(properties.get(LEFTTOP_COLOR_OFFSET_B_KEY).trim());

            USERNAME = properties.get(USERNAME_KEY).trim();
            PASSWORD = properties.get(PASSWORD_KEY).trim();

        }
        catch (Exception e)
        {
            Logger.log(Logger.Level.ERROR, FlashStatusDetector.Status.NONE, "property file does not exits!");
        }
    }
}
