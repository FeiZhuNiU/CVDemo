package ericyu.chepai.robot.bidstrategy;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/

import org.opencv.core.Core;

import java.awt.*;

public class User
{
    private String username;
    private String password;

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

//    public boolean login(MyRobot robot)
//    {
//        boolean ret;
//        ret = robot.focusOnUsernameInputBox();
//        if(ret)
//        {
//            robot.inputString(username);
//            ret = robot.focusOnPasswordInputBox();
//        }
//        if(ret)
//        {
//            robot.inputString(password);
//            ret = robot.clickLoginButton();
//        }
//        return ret;
//    }

    public static void main(String[] args) throws AWTException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        User user = new User("12345678","123456");
//        user.login(new MyRobot(new Robot()));
    }
}
