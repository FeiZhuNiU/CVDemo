package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.robot.MyRobot;

abstract public class AbstractBidStrategy
{
    protected User user;
    protected MyRobot robot;

    public AbstractBidStrategy(User user, MyRobot robot)
    {
        this.user = user;
        this.robot = robot;
    }

    public abstract void execute();

    public void addMoneyAndBid(int addMoneyRange, int waitBetweenAddAndBid)
    {
        robot.focusOnCustomAddMoneyInputBox();
        robot.inputAddMoneyRange(addMoneyRange);
        robot.clickAddMoneyButton();
        robot.wait(waitBetweenAddAndBid);
        robot.clickBidButton();
    }
}
