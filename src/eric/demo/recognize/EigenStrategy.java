package eric.demo.recognize;

import org.opencv.core.Mat;

/**
 * Created by 麟 on 2015/10/31.
 */
public interface EigenStrategy
{
    Mat getEigenVec(Mat src);
}
