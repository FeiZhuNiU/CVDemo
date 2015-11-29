package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.robot.MyRobot;

public class AmbushAndAidStrategy implements IBidStrategy
{
    private MyRobot robot;

    public AmbushAndAidStrategy(MyRobot robot)
    {
        this.robot = robot;
    }

    @Override
    public void execute()
    {
        User user = new User(robot,"12345678","123456");
        user.login();

        robot.wait(2000);
        int moneyAddRange = 600;
        int waitTime = 3000;
        while(true)
        {

            //add money and bid
            addMoneyAndBid(moneyAddRange, waitTime);
            //TODO: how to  make sure it's on verification code view
            robot.wait(1000);
            //recognize verification code and confirm
            robot.recogAndInputVerificationCode();
            robot.clickConfirmVCodeButton();
            robot.wait(1000);

            switch (robot.verifySystemNotification())
            {
                //success
                case 0:
                    return;
                //not in bid range
                case 1:
                    moneyAddRange = 300;
                    waitTime = 10;
                    break;
                //wrong verification code
                case -1:
                    waitTime = 10;
                    break;
            }
        }
    }

    public void addMoneyAndBid(int addMoneyRange, int waitBetweenAddAndBid)
    {
        robot.focusOnCustomAddMoneyInputBox();
        robot.inputAddMoneyRange(addMoneyRange);
        robot.clickAddMoneyButton();
        robot.wait(waitBetweenAddAndBid);
        robot.clickBidButton();
    }
}
