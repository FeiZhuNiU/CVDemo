package ericyu.chepai.recognize;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by 麟 on 2015/11/29.
 */
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
