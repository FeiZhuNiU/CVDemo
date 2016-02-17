package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.FlashStatusDetector;

public interface IStatusObserver
{
    void flashStatusChanged(FlashStatusDetector.Status status);
}
