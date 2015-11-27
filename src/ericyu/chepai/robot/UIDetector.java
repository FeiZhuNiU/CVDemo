package ericyu.chepai.robot;

/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/
public class UIDetector implements Runnable
{
    private UIStatus status;
    public enum UIStatus {
        NONE, LOGIN,
    }

    public UIDetector()
    {
        status = UIStatus.NONE;
    }

    @Override
    public void run()
    {

    }
}
