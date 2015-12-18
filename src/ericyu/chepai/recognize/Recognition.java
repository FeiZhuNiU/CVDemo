package ericyu.chepai.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.train.AbstractSampleTrain;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.ml.ANN_MLP;
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
        kNearest.train(training.getTrainData(), Ml.ROW_SAMPLE, training.getTrainClass());
    }

    /**
     * @param toRecog generally, this param should be the return value of {@link AbstractSampleTrain#process}
     * @param accuracy a param for KNN classifier
     * @return
     */
    public int recognize(Mat toRecog, int accuracy)
    {
        Mat eigen = getTraining().getEigenvetorStrategy().getEigenVec(toRecog);
        int num = (int) (kNearest.findNearest(eigen, accuracy, new Mat()));
        return num;
    }

    @Deprecated
    public ANN_MLP getAnnClassifier()
    {
        ANN_MLP ann_mlp = ANN_MLP.create();
        Mat layOut = new Mat(1, 3, CvType.CV_32S, new Scalar(new double[]{9, 5, 9}));
//        layOut.put(0,0,new int[]{9,5,9});

        ann_mlp.setLayerSizes(layOut);
        ann_mlp.setTrainMethod(ANN_MLP.BACKPROP);
        ann_mlp.setBackpropMomentumScale(0.1);
        ann_mlp.setBackpropWeightScale(0.1);

//        Map.Entry<Mat, Mat> trainData = RecogUtils.setTrainDataAndTrainClasses();
        ann_mlp.train(training.getTrainData(), Ml.ROW_SAMPLE, training.getTrainClass());

        return ann_mlp;
    }

}
