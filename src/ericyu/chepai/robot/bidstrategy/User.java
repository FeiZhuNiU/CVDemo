package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/

import ericyu.chepai.robot.MyRobot;
import org.opencv.core.Core;

import java.awt.*;

public class User
{
    private String username;
    private String password;
    private MyRobot robot;

    public User(MyRobot robot, String username, String password)
    {
        this.robot = robot;
        this.username = username;
        this.password = password;
    }

    public void login()
    {
        robot.focusOnUsernameInputBox();
        robot.inputString(username);
        robot.focusOnPasswordInputBox();
        robot.inputString(password);
        robot.clickSubmitUserButton();
    }

    public static void main(String[] args) throws AWTException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        User user = new User(new MyRobot(new Robot()),"12345678","123456");
        user.login();
    }
}
