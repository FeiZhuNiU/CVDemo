package com.iknow.train.eigen;
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
    /**
     * region
     */
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

        // TODO:
        for (int i = 0 ; i < row-1 ; ++i)
        {
            for (int j = 0; j < col-1; ++j)
            {
                if(src.get(i,j)[0] > 0)
                {
                    ++count[i*rows/row][j*cols/col];
                }
            }
        }

        for (int i = 0 ; i < rows ; ++i)
        {
            for (int j = 0; j < cols; ++j)
            {
                ret.put(0,i*rows + j ,count[i][j]);
            }
        }
        return ret;

    }
}
