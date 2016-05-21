package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.chepai.utils.DateUtil;
import ericyu.chepai.utils.Logger;

import java.io.*;
import java.util.*;

public class StrategyConfig {
    public static final String STRATEGY_CONFIG_FILE = "strategy.properties";
    public static HashMap<String, String> strategyPropertyMap = new HashMap<>();

    public static String username = "";
    public static String password = "";
    public static int bidTimeHour = 11;
    public static int bidTimeMinute = 29;
    public static int startTimeSecond = 0;
    public static int firstBidSecond = 30;
    public static int firstBidConfirmVCodeSecond = 38;
    public static int addMoneySecond = 46;
    public static int addMoneyRange = 600;
    public static int vCodeConfirmSecond = 52;
    public static int latestBidTimeSecond = 52;
    public static int bidDiff = 400;
    public static boolean semiAuto = true;
    public static int startBid = 80600;
    public static int exitTimeHour = 11;
    public static int exitTimeMinute = 30;

    public enum StrategyPropKey
    {
        USERNAME_KEY("username"),
        PASSWORD_KEY("password"),
        SEMIAUTO_KEY("semiAuto"),
        START_TIME_SECOND_KEY("startTimeSecond"),
        BID_TIME_HOUR_KEY("bidTimeHour"),
        BID_TIME_MINUTE_KEY("bidTimeMinute"),
        FIRST_BID_SECOND("firstBidSecond"),
        FIRST_BID_CONFIRM_SECOND("firstBidConfirmVCodeSecond"),
        ADD_MONEY_SECOND_KEY("addMoneyTimeSecond"),
        ADD_MONEY_RANGE_KEY("addMoneyRange"),
        V_CODE_CONFIRM_SECOND_KEY("vCodeConfirmTimeSecond"),
        LATEST_BID_TIME_SECOND_KEY("latestBidTimeSecond"),
        BID_DIFF_KEY("bidDiff"),
        START_BID_KEY("startBid"),
        EXIT_TIME_MINUTE_KEY("exitTimeMinute"),
        EXIT_TIME_HOUR_KEY("exitTimeHour");

        final public String val;
        StrategyPropKey(String key) {
            val = key;
        }
    }

    public static String getPropertyVal(StrategyPropKey key)
    {
        return strategyPropertyMap.get(key.val);
    }

    public static void setPropertyVal(StrategyPropKey key, String val)
    {
        strategyPropertyMap.put(key.val,val);
    }

    static {
        updatePropMap();
        readPropertyFile();
    }

    private static void updatePropMap() {
        strategyPropertyMap.put(StrategyPropKey.USERNAME_KEY.val, String.valueOf(username));
        strategyPropertyMap.put(StrategyPropKey.PASSWORD_KEY.val, String.valueOf(password));
        strategyPropertyMap.put(StrategyPropKey.SEMIAUTO_KEY.val, String.valueOf(semiAuto));
        strategyPropertyMap.put(StrategyPropKey.START_TIME_SECOND_KEY.val, String.valueOf(startTimeSecond));
        strategyPropertyMap.put(StrategyPropKey.BID_TIME_HOUR_KEY.val, String.valueOf(bidTimeHour));
        strategyPropertyMap.put(StrategyPropKey.BID_TIME_MINUTE_KEY.val, String.valueOf(bidTimeMinute));
        strategyPropertyMap.put(StrategyPropKey.FIRST_BID_SECOND.val, String.valueOf(firstBidSecond));
        strategyPropertyMap.put(StrategyPropKey.FIRST_BID_CONFIRM_SECOND.val, String.valueOf(firstBidConfirmVCodeSecond));
        strategyPropertyMap.put(StrategyPropKey.ADD_MONEY_SECOND_KEY.val, String.valueOf(addMoneySecond));
        strategyPropertyMap.put(StrategyPropKey.ADD_MONEY_RANGE_KEY.val, String.valueOf(addMoneyRange));
        strategyPropertyMap.put(StrategyPropKey.V_CODE_CONFIRM_SECOND_KEY.val, String.valueOf(vCodeConfirmSecond));
        strategyPropertyMap.put(StrategyPropKey.LATEST_BID_TIME_SECOND_KEY.val, String.valueOf(latestBidTimeSecond));
        strategyPropertyMap.put(StrategyPropKey.BID_DIFF_KEY.val, String.valueOf(bidDiff));
        strategyPropertyMap.put(StrategyPropKey.START_BID_KEY.val, String.valueOf(startBid));
        strategyPropertyMap.put(StrategyPropKey.EXIT_TIME_MINUTE_KEY.val, String.valueOf(exitTimeMinute));
        strategyPropertyMap.put(StrategyPropKey.EXIT_TIME_HOUR_KEY.val, String.valueOf(exitTimeHour));
    }

    public static void readPropertyFile() {
        Properties strategyProperty = new Properties();
        try {
            strategyProperty.load(new FileInputStream(STRATEGY_CONFIG_FILE));

            Enumeration keyEnumeration = strategyProperty.propertyNames();
            while (keyEnumeration.hasMoreElements()) {
                String key = (String) keyEnumeration.nextElement();
                String value = strategyProperty.getProperty(key);
                strategyPropertyMap.put(key, value);
            }

            username = strategyPropertyMap.get(StrategyPropKey.USERNAME_KEY.val).trim();
            password = strategyPropertyMap.get(StrategyPropKey.PASSWORD_KEY.val).trim();
            semiAuto = Boolean.parseBoolean(strategyPropertyMap.get(StrategyPropKey.SEMIAUTO_KEY.val).trim());

//            startTimeHour = Integer.parseInt(flashProperties.get(START_TIME_HOUR_KEY).trim());
//            startTimeMinute = Integer.parseInt(flashProperties.get(START_TIME_MINUTE_KEY).trim());
            int bidTimeHour_tmp = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.BID_TIME_HOUR_KEY.val).trim());
            int bidTimeMinute_tmp = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.BID_TIME_MINUTE_KEY.val).trim());
            bidTimeHour = bidTimeHour_tmp == -1 ? DateUtil.getCurrentHour() : bidTimeHour_tmp;
            bidTimeMinute = bidTimeMinute_tmp == -1 ? DateUtil.getCurrentMinute() : bidTimeMinute_tmp;
            startTimeSecond = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.START_TIME_SECOND_KEY.val).trim());
            startBid = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.START_BID_KEY.val).trim());
            int exitTimeHour_tmp = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.EXIT_TIME_HOUR_KEY.val).trim());
            exitTimeHour = exitTimeHour_tmp == -1 ? bidTimeHour : exitTimeHour_tmp;
            int exitTimeMinute_tmp = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.EXIT_TIME_MINUTE_KEY.val).trim());
            exitTimeMinute = exitTimeMinute_tmp == -1 ? bidTimeMinute + 1 : exitTimeMinute_tmp;

            firstBidSecond = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.FIRST_BID_SECOND.val).trim());
            firstBidConfirmVCodeSecond = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.FIRST_BID_CONFIRM_SECOND.val).trim());

            addMoneySecond = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.ADD_MONEY_SECOND_KEY.val).trim());
            addMoneyRange = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.ADD_MONEY_RANGE_KEY.val).trim());
            vCodeConfirmSecond = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.V_CODE_CONFIRM_SECOND_KEY.val).trim());

            latestBidTimeSecond = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.LATEST_BID_TIME_SECOND_KEY.val).trim());
            bidDiff = Integer.parseInt(strategyPropertyMap.get(StrategyPropKey.BID_DIFF_KEY.val).trim());


        } catch (Exception e) {
            Logger.log(Logger.Level.WARNING, FlashStatusDetector.Status.NONE, "property file does not exits!", e);
        }
    }
    public static void writePropertyFile()
    {
        updatePropMap();
        try {
            File file = new File(STRATEGY_CONFIG_FILE);
            if (!file.exists())
            {
                file.createNewFile();
                Logger.log(Logger.Level.INFO, null, "strategy.properties does not exists. Create One");
            }
            OutputStream fos = new FileOutputStream(STRATEGY_CONFIG_FILE);
            Properties strategyProperty = new Properties();
            Set<Map.Entry<String, String>> entries = strategyPropertyMap.entrySet();
            for (Map.Entry<String, String> curProp : entries) {
                strategyProperty.setProperty(curProp.getKey(), curProp.getValue());
            }
            strategyProperty.store(fos, "Updated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        writePropertyFile();
    }
}
