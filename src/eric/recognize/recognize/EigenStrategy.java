package eric.recognize.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/31/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/
import org.opencv.core.Mat;

public interface EigenStrategy
{
    Mat getEigenVec(Mat src);
}
