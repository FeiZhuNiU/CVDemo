package ericyu.recognize.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.recognize.image.ImageUtils;
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
    private static Map.Entry<Mat, Mat> loadTrainDataAndTrainClasses()
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

    /**
     * @param samples a list of samples
     * @return <trainData , trainClasses>
     */
    public static Map.Entry<Mat, Mat> loadSamplesToTrainDataAndTrainClasses(List<Map.Entry<Mat, Integer>> samples)
    {
        Mat trainData = null;
        Mat trainClasses = null;

        trainData = new Mat(samples.size(), samples.get(0).getKey().rows() * samples.get(0).getKey().cols(),
                CvType.CV_32FC1);
        trainClasses = new Mat(samples.size(), 1, CvType.CV_32FC1);

        for (int i = 0; i < samples.size(); ++i)
        {
            int curVal = samples.get(i).getValue();
            Mat curMat = samples.get(i).getKey();
            trainClasses.put(i, 0, curVal);
            for (int j = 0; j < trainData.cols(); ++j)
            {
                trainData.put(i, j, curMat.get(j / curMat.cols(), j % curMat.cols())[0]);
            }
        }

        Map.Entry<Mat, Mat> ret = new AbstractMap.SimpleEntry<>(trainData, trainClasses);
        RecogUtils.trainData = loadTrainDataAndTrainClasses();
        return ret;
    }


    public static KNearest getKnnClassifier()
    {
        KNearest kNearest = KNearest.create();
//        Map.Entry<Mat, Mat> trainData = RecogUtils.loadSamplesToTrainDataAndTrainClasses();
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

//        Map.Entry<Mat, Mat> trainData = RecogUtils.loadSamplesToTrainDataAndTrainClasses();
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
