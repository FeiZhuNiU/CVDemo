package ericyu.chepai.robot.bidstrategy;
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

public class BidStrategyConstants
{
    private static final String ADD_MONEY_RANGE_KEY = "addMoneyRange";
    private static final String V_CODE_CONFIRM_HOUR_KEY = "vCodeConfirmTimeHour";
    private static final String V_CODE_CONFIRM_MINUTE_KEY = "vCodeConfirmTimeMinute";
    private static final String V_CODE_CONFIRM_SECOND_KEY = "vCodeConfirmTimeSecond";


    public static int ADD_MONEY_RANGE;
    public static int V_CODE_CONFIRM_HOUR;
    public static int V_CODE_CONFIRM_MINUTE;
    public static int V_CODE_CONFIRM_SECOND;
    public static HashMap<String,String> properties = new HashMap<>();

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
                properties.put(key,value);
            }

            ADD_MONEY_RANGE = Integer.parseInt(properties.get(ADD_MONEY_RANGE_KEY));
            V_CODE_CONFIRM_HOUR = Integer.parseInt(properties.get(V_CODE_CONFIRM_HOUR_KEY));
            V_CODE_CONFIRM_MINUTE = Integer.parseInt(properties.get(V_CODE_CONFIRM_MINUTE_KEY));
            V_CODE_CONFIRM_SECOND = Integer.parseInt(properties.get(V_CODE_CONFIRM_SECOND_KEY));

        }
        catch (Exception e)
        {
            Logger.log(Logger.Level.ERROR, FlashStatusDetector.Status.NONE, "property file does not exits!");
        }
    }
}
