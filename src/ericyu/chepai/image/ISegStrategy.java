package ericyu.chepai.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.Mat;

import java.util.List;

public interface ISegStrategy
{

    /**
     * @param src color image
     * @return return mats should be ordered
     */
    List<Mat> doSegmentation(Mat src);
}
