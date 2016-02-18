package com.iknow.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.iknow.train.AbstractSampleTrain;
import com.iknow.train.ClassificationConsts;
import org.opencv.core.Mat;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;

/**
 * Recognition
 * core API:
 * @see #recognize
 */
public class Recognition
{
    private AbstractSampleTrain training;

    public AbstractSampleTrain getTraining()
    {
        return training;
    }

    private KNearest kNearest;

    public Recognition(AbstractSampleTrain training)
    {
        this.training = training;
        kNearest = KNearest.create();
        // this will cause memory leak
        kNearest.train(training.getTrainDataMat(), Ml.ROW_SAMPLE, training.getTrainClassMat());
    }

    /**
     * @param accuracy a param for KNN classifier
     * @return
     */
    public char recognize(Mat toRecog, int accuracy)
    {
        Mat eigen = getTraining().getEigenvetorStrategy().getEigenVec(toRecog);
        int num = (int) (kNearest.findNearest(eigen, accuracy, new Mat()));
        return ClassificationConsts.classList.get(num);
    }

}
