package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.Configuration;
import ericyu.chepai.DateUtil;
import ericyu.chepai.robot.MyRobot;

public class AmbushAndAidStrategy extends AbstractBidStrategy
{

    public AmbushAndAidStrategy(User user, MyRobot robot)
    {
        super(user, robot);
        latestBidTime = DateUtil.getDateLongValue(DateUtil.getCurrentHour(),
                                                  DateUtil.getCurrentMinute(),
                                                  Configuration.LATEST_BID_TIME);
    }

    @Override
    public void execute()
    {
        //--------------------------------------------------
        //login
        //0
        addAction(new FocusOnUsernameInputBox());
        //1
        addAction(new InputUserName(user.getUsername()));
        //2
        addAction(new FocusOnPasswordInputBox());
        //3
        addAction(new InputPassword(user.getPassword()));
        //4
        addAction(new ClickLogin());
        //----------------------------------------------------
        //bid
        //5
        addAction(new FocusOnAddRangeBox());
        //6
        addAction(new InputAddMoneyRange(Configuration.ADD_MONEY_RANGE));


        addAction(new WaitUntilTargetTime(DateUtil.getDateLongValue(DateUtil.getCurrentHour(),
                                                                    DateUtil.getCurrentMinute(),
                                                                    Configuration.ADD_MONEY_SECOND)));
        //7
        addAction(new ClickAddMoneyButton());
        //8
        addAction(new ClickBidButton());

        //--------------------------------------------------
        //V-code
        //9
        addAction(new RecogAndEnterVCode());


//        addAction(new WaitUntil(DateUtil.getDateLongValue(BidStrategyConstants.V_CODE_CONFIRM_HOUR,
//                                                          BidStrategyConstants.V_CODE_CONFIRM_MINUTE,
//                                                          BidStrategyConstants.V_CODE_CONFIRM_SECOND)));
        addAction(new WaitUntilTargetTime(DateUtil.getDateLongValue(DateUtil.getCurrentHour(),
                                                                    DateUtil.getCurrentMinute(),
                                                                    Configuration.V_CODE_CONFIRM_SECOND)));

        //TODO: LATEST_BID_TIME
        addAction(new WaitUntilBidDiffLessThan(400));

        addAction(new ClickVCodeConfirmButton());

        addAction(new RecogResult());


//        try
//        {
//            System.out.println(results.get(0).get().getValue());
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        } catch (ExecutionException e)
//        {
//            e.printStackTrace();
//        }


//        stop();


//        robot.wait(5000);
//        int moneyAddRange = 600;
//        int waitTime = 3000;
//        while(true)
//        {
//
//            //add money and bid
//            addMoneyAndBid(moneyAddRange, waitTime);
//            //TODO: how to  make sure it's on verification code view
//            robot.wait(1000);
//            //recognize verification code and confirm
//            robot.recogAndInputVerificationCode();
//            robot.clickConfirmVCodeButton();
//            robot.wait(1000);
//
//            switch (robot.verifySystemNotification())
//            {
//                //success
//                case 0:
//                    Logger.log(Logger.Level.INFO, flashStatus, "bid success!!");
//                    System.exit(0);
//                //not in bid range
//                case 1:
//                    Logger.log(Logger.Level.INFO, flashStatus,"bid out of range");
//                    moneyAddRange = 300;
//                    waitTime = 10;
//                    break;
//                //wrong verification code
//                case 2:
//                    Logger.log(Logger.Level.INFO, flashStatus,"wrong verification code!!");
//                    waitTime = 10;
//                    break;
//            }
//        }
    }


}
