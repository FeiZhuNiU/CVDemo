package ericyu.chepai.flash;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

public interface IStatusObserver
{
    void flashStatusChanged(FlashStatusDetector.Status status);
}
