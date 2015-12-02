package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.DateUtil;
import ericyu.chepai.Logger;
import ericyu.chepai.flash.FlashStatusDetector;
import ericyu.chepai.flash.IStatusObserver;
import ericyu.chepai.robot.MyRobot;


import java.util.*;
import java.util.concurrent.*;

abstract public class AbstractBidStrategy implements IStatusObserver
{
    /**
     * used for timer task
     */
    protected long startTime;
    protected User user;
    protected MyRobot robot;

    /**
     * observer
     */
    protected FlashStatusDetector.Status flashStatus;

    protected ExecutorService strategy;
    /**
     * Boolean  -> whether the action successfully done
     * Object   -> return value if needed
     */
    protected List<Future<Map.Entry<Boolean,Object>>> results;

    public AbstractBidStrategy(User user, MyRobot robot)
    {
        strategy = Executors.newSingleThreadExecutor();
        results = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.user = user;
        this.robot = robot;
        setStrategy();
    }

    protected void addAction(Callable<Map.Entry<Boolean,Object>> action)
    {
        results.add(strategy.submit(action));
        Logger.log(Logger.Level.INFO, flashStatus,
                   "prepare strategy: add action [" + action.getClass().getSimpleName() + "]");
    }
    public void stop()
    {
        strategy.shutdown(); //avoid submit
    }



    /**
     * add actions here
     */
    public abstract void setStrategy();


    protected class WaitUntil implements Callable<Map.Entry<Boolean,Object>>
    {
        private long targetTime;

        public WaitUntil(long targetTime)
        {
            this.targetTime = targetTime;
            Logger.log(Logger.Level.INFO, flashStatus, "wait until " + DateUtil.formatLongValueToDate(targetTime));
        }

        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while(true)
            {
                if(System.currentTimeMillis() >= targetTime)
                    break;
                Logger.log(Logger.Level.INFO, flashStatus,(targetTime - System.currentTimeMillis()) + "ms to go");
                robot.wait(100);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class FocusOnUsernameInputBox implements Callable<Map.Entry<Boolean,Object>>
    {
        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while(true)
            {
                if(robot.focusOnUsernameInputBox())
                    break;
                robot.wait(1000);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class InputUserName implements Callable<Map.Entry<Boolean,Object>>
    {
        private String username;

        public InputUserName(String username)
        {
            this.username = username;
        }

        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            robot.inputString(username);
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class FocusOnPasswordInputBox implements Callable<Map.Entry<Boolean,Object>>
    {
        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while(true)
            {
                if(robot.focusOnPasswordInputBox())
                    break;
                robot.wait(1000);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class InputPassword implements Callable<Map.Entry<Boolean,Object>>
    {
        private String password;

        public InputPassword(String password)
        {
            this.password = password;
        }

        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            robot.inputString(password);
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class ClickLogin implements Callable<Map.Entry<Boolean,Object>>
    {
        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while(true)
            {
                if(robot.clickLoginButton())
                    break;
                robot.wait(100);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class FocusOnAddRangeBox implements Callable<Map.Entry<Boolean,Object>>
    {

        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while(true)
            {
                if(robot.focusOnCustomAddMoneyInputBox())
                    break;
                robot.wait(1000);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class InputAddMoneyRange implements Callable<Map.Entry<Boolean,Object>>
    {
        private int addedMoney;

        public InputAddMoneyRange(int addedMoney)
        {
            this.addedMoney = addedMoney;
        }
        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while(true)
            {
                if(robot.inputAddMoneyRange(addedMoney))
                    break;
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class ClickAddMoneyButton implements Callable<Map.Entry<Boolean,Object>>
    {

        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while (true)
            {
                if(robot.clickAddMoneyButton())
                    break;
                robot.wait(100);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class ClickBidButton implements Callable<Map.Entry<Boolean,Object>>
    {

        @Override
        public Map.Entry<Boolean,Object> call() throws Exception
        {
            while (true)
            {
                if (robot.clickBidButton())
                    break;
                robot.wait(100);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    /**
     * including refresh condition
     */
    protected class RecogAndEnterVCode implements Callable<Map.Entry<Boolean,Object>>
    {
        @Override
        public Map.Entry<Boolean, Object> call() throws Exception
        {
            ArrayList<Integer> vcode;
            // in case the load of v-code costs some time
            robot.wait(1001);
            while (true)
            {
                vcode = robot.recogVerificationCode();
                if(vcode == null || vcode.size() ==0)
                {
                    switch (robot.isRefreshVCodeButtonExist())
                    {
                        //not in right status
                        case -1:
                            robot.wait(201);
                            break;
                        //not exist
                        case 0:
                            while(!robot.clickCancelVerificationCodeButton());
                            while(true)
                            {
                                while(!robot.clickBidButton());
                                robot.wait(101);
                                if(flashStatus == FlashStatusDetector.Status.NOTIFICATION)
                                {
                                    while(!robot.clickRequestForVCodeTooOftenConfirmButton());
                                }
                                else
                                {
                                    break;
                                }
                            }

                            break;
                        //exist
                        case 1:
                            robot.clickRefreshVCodeButton();
                            robot.wait(202);
                            break;
                    }

                }
                else
                {
                    robot.focusOnVCodeInputBox();
                    robot.enterVerificationCode(vcode);
                    break;
                }
            }

            return new AbstractMap.SimpleEntry<Boolean, Object>(true,vcode);
        }
    }

    protected class ClickVCodeConfirmButton implements Callable<Map.Entry<Boolean,Object>>
    {

        @Override
        public Map.Entry<Boolean, Object> call() throws Exception
        {
            while (true)
            {
                if (robot.clickConfirmVCodeButton())
                    break;
                robot.wait(102);
            }
            return new AbstractMap.SimpleEntry<>(true, null);
        }
    }

    protected class RecogResult implements Callable<Map.Entry<Boolean,Object>>
    {
        @Override
        public Map.Entry<Boolean, Object> call() throws Exception
        {
            Integer result = null;
            boolean done = false;
            while (true)
            {
                if(done)
                    break;
                switch (robot.verifySystemNotification())
                {
                    // not right status
                    case -1:
                        robot.wait(203);
                        break;
                    //bid success
                    case 0:
                        System.exit(0);
                        break;
                    // not in bid range
                    case 1:
                        done = true;
                        result = 1;
                        break;
                    //wrong v-code
                    case 2:
                        done = true;
                        result = 2;
                        break;
                }
            }
            return new AbstractMap.SimpleEntry<Boolean,Object>(true, result);
        }
    }



    @Override
    public void flashStatusChanged(FlashStatusDetector.Status status)
    {
        flashStatus = status;
    }
}
