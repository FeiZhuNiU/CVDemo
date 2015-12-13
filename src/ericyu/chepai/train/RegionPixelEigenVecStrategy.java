package ericyu.chepai.train;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 12/13/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class RegionPixelEigenVecStrategy implements IEigenvectorStrategy
{
    private int rows;
    private int cols;

    public RegionPixelEigenVecStrategy(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Mat getEigenVec(Mat src)
    {
        if(src == null)
            return null;

        Mat ret = new Mat(1, rows*cols, CvType.CV_32FC1);
        int row = src.rows();
        int col = src.cols();

        int[][] count = new int[rows][cols];

        for (int i = 0 ; i < row ; ++i)
        {
            for (int j = 0; j < col; ++j)
            {
                if(src.get(i,j)[0] > 0)
                {
                    ++count[i/rows][j/cols];
                }
            }
        }

        for (int i = 0 ; i < rows ; ++i)
        {
            for (int j = 0; j < cols; ++j)
            {
                ret.put(i,j,count[i][j]);
            }
        }
        return ret;

    }
}
