package eric.recognize.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

public interface SegStrategy
{
    List<Rect> doSegmentation(Mat src);
}
