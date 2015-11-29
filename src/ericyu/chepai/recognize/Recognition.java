package ericyu.chepai.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;

public class Recognition
{
    private SampleTrain samples;

    public SampleTrain getSamples()
    {
        return samples;
    }

    public enum Classifier
    {
        KNN, ANN
    }

    public Recognition(SampleTrain samples)
    {
        this.samples = samples;
    }

    private KNearest getKnnClassifier()
    {
        KNearest kNearest = KNearest.create();
        kNearest.train(samples.getTrainData(), Ml.ROW_SAMPLE, samples.getTrainClass());
        return kNearest;
    }
    public int recognize(Mat toRecog)
    {
        int num = (int) getKnnClassifier().findNearest(toRecog, 10, new Mat());
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
        ann_mlp.train(samples.getTrainData(), Ml.ROW_SAMPLE, samples.getTrainClass());

        return ann_mlp;
    }

}
