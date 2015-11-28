package ericyu.chepai.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;

import java.io.File;
import java.util.*;

public class RecogUtils
{
    public enum Classifier
    {
        KNN, ANN
    }

    public static Map.Entry<Mat, Mat> trainData;

    public static final String TRAIN_SAMPLE_PATH = "resources\\traindata.png";
    public static final String TRAIN_CLASS_PATH = "resources\\trainclasses.png";

    static
    {
        trainData = loadTrainDataAndTrainClasses();
    }

    /**
     * load train data when load class
     *
     * @return
     */
    public static Map.Entry<Mat, Mat> loadTrainDataAndTrainClasses()
    {
        File file1 = new File(TRAIN_SAMPLE_PATH);
        File file2 = new File(TRAIN_CLASS_PATH);

        if (!file1.exists() || !file2.exists())
        {
            System.out.println("train data not prepared");
            return null;
        }

        Mat trainDataColor = Imgcodecs.imread(TRAIN_SAMPLE_PATH);
        Mat trainClassesColor = Imgcodecs.imread(TRAIN_CLASS_PATH);
        Mat trainData = ImageUtils.color2Gray(trainDataColor);
        Mat trainClasses = ImageUtils.color2Gray(trainClassesColor);
        trainData.convertTo(trainData, CvType.CV_32FC1);
        trainClasses.convertTo(trainClasses, CvType.CV_32FC1);
        return new AbstractMap.SimpleEntry<>(trainData, trainClasses);
    }


    public static KNearest getKnnClassifier()
    {
        KNearest kNearest = KNearest.create();
//        Map.Entry<Mat, Mat> trainData = RecogUtils.setTrainDataAndTrainClasses();
        kNearest.train(trainData.getKey(), Ml.ROW_SAMPLE, trainData.getValue());
        return kNearest;
    }

    @Deprecated
    public static ANN_MLP getAnnClassifier()
    {
        ANN_MLP ann_mlp = ANN_MLP.create();
        Mat layOut = new Mat(1, 3, CvType.CV_32S, new Scalar(new double[]{9, 5, 9}));
//        layOut.put(0,0,new int[]{9,5,9});

        ann_mlp.setLayerSizes(layOut);
        ann_mlp.setTrainMethod(ANN_MLP.BACKPROP);
        ann_mlp.setBackpropMomentumScale(0.1);
        ann_mlp.setBackpropWeightScale(0.1);

//        Map.Entry<Mat, Mat> trainData = RecogUtils.setTrainDataAndTrainClasses();
        ann_mlp.train(trainData.getKey(), Ml.ROW_SAMPLE, trainData.getValue());

        return ann_mlp;
    }

    public static Mat getEigenVec(Mat mat, EigenStrategy strategy)
    {
        if (strategy == null)
        {
            int cols = mat.rows() * mat.cols();
            Mat ret = new Mat(1, cols, CvType.CV_32FC1);
            for (int j = 0; j < cols; ++j)
            {
                ret.put(0, j, mat.get(j / mat.cols(), j % mat.cols()));
            }
            return ret;
        } else
        {
            return strategy.getEigenVec(mat);
        }
    }
}
