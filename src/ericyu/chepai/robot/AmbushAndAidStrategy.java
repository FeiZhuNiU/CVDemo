package ericyu.chepai.robot;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

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
        int moneyAddRange = 900;
        int waitTime = 3000;
        while(true)
        {

            //add money and bid
            addMoneyAndBid(moneyAddRange, waitTime);
            robot.wait(1000);
            //recognize verification code and confirm
            robot.recogAndInputVerificationCode();
            robot.clickConfirmVerificationCodeButton();
            robot.wait(10);

            switch (robot.verifyResult())
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
