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

public class Recognition
{
    public static String screenCaptureImage = "screenCapture.bmp";
    private AbstractSampleTrain trainedData;

    public AbstractSampleTrain getTrainedData()
    {
        return trainedData;
    }

    public enum Classifier
    {
        KNN, ANN
    }

    public Recognition(AbstractSampleTrain trainedData)
    {
        this.trainedData = trainedData;
    }

    private KNearest getKnnClassifier()
    {
        KNearest kNearest = KNearest.create();
        kNearest.train(trainedData.getTrainData(), Ml.ROW_SAMPLE, trainedData.getTrainClass());
        return kNearest;
    }

    /**
     * @param toRecog generally, this param should be the return value of
     *                @see AbstractSampleTrain#process
     * @param accuracy
     * @return
     */
    public int recognize(Mat toRecog, int accuracy)
    {
        Mat eigen = getTrainedData().getEigenvetorStrategy().getEigenVec(toRecog);
        int num = (int) getKnnClassifier().findNearest(eigen, accuracy, new Mat());
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
        ann_mlp.train(trainedData.getTrainData(), Ml.ROW_SAMPLE, trainedData.getTrainClass());

        return ann_mlp;
    }

}
