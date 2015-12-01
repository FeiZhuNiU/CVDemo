package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.Logger;
import ericyu.chepai.robot.MyRobot;

public class AmbushAndAidStrategy extends AbstractBidStrategy
{

    public AmbushAndAidStrategy(User user, MyRobot robot)
    {
        super(user, robot);
    }

    @Override
    public void setStrategy()
    {

        addAction(new FocusOnUsernameInputBox());
        addAction(new InputUserName(user.getUsername()));
        addAction(new FocusOnPasswordInputBox());
        addAction(new InputPassword(user.getPassword()));
        addAction(new ClickLogin());
        addAction(new FocusOnAddRangeBox());
        addAction(new InputAddMoneyRange(500));
        addAction(new ClickAddMoneyButton());
        addAction(new ClickBidButton());
        stop();


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
