package eric.demo.image;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

public interface SegStrategy
{
    List<Rect> doSegmentation(Mat src);
}
