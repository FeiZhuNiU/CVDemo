package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.robot.MyRobot;

public class AmbushAndAidStrategy extends AbstractBidStrategy
{

    public AmbushAndAidStrategy(User user, MyRobot robot)
    {
        super(user, robot);
    }

    @Override
    public void execute()
    {
        robot.wait(2000);

        while(!user.login(robot))
        {
            System.out.println("login failed!!");
        }

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
                    System.out.println("bid success!!");
                    System.exit(0);
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


}
