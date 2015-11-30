package ericyu.chepai.train;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/29/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class AllPixelEigenvetorStrategy implements EigenvetorStrategy
{
    @Override
    public Mat getEigenVec(Mat mat)
    {
        int cols = mat.rows() * mat.cols();
        Mat ret = new Mat(1, cols, CvType.CV_32FC1);
        for (int j = 0; j < cols; ++j)
        {
            ret.put(0, j, mat.get(j / mat.cols(), j % mat.cols()));
        }
        return ret;
    }
}
