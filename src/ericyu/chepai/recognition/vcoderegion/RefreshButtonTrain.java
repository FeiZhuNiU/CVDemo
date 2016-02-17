package ericyu.chepai.recognition.vcoderegion;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import com.iknow.train.AbstractSampleTrain;
import com.iknow.train.ISampleNamingRule;
import com.iknow.train.eigen.IEigenvectorStrategy;
import ericyu.chepai.recognition.SampleConstants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class RefreshButtonTrain extends AbstractSampleTrain
{


    public RefreshButtonTrain(String sampleDir, String trainDataPath, String trainClassPath, IEigenvectorStrategy eigenvecStrategy, ISampleNamingRule namingRule)
    {
        super(sampleDir, trainDataPath, trainClassPath, eigenvecStrategy, namingRule);
    }

    public RefreshButtonTrain(String sampleDir, ISampleNamingRule namingRule)
    {
        super(sampleDir, namingRule);
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }

    @Override
    public Mat normalization(Mat mat)
    {
        return mat;
    }

    @Override
    protected List<Mat> generateSampleCluster(Mat mat)
    {
        List<Mat> ret = new ArrayList<>();
        for (int i = -10; i <= 10; ++i)
        {
            for (int j = -10; j <= 10; ++j)
            {
                Mat cur = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
                for(int row = 0; row < mat.rows(); ++row)
                {
                    for(int col = 0; col < mat.cols(); ++col)
                    {
                        //TODO???? be ok?
                        cur.put(row,col,mat.get(row+i,col+j));
                    }
                }
                ret.add(cur);
            }
        }
        return ret;
    }
}
