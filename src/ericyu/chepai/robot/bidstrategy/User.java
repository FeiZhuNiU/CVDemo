package ericyu.chepai.robot.bidstrategy;

import ericyu.chepai.robot.MyRobot;
import org.opencv.core.Core;

import java.awt.*;

/**
 * Created by 麟 on 2015/11/27.
 */
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
