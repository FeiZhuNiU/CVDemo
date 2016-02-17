package com.iknow.train;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/28/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.iknow.image.ImageUtils;
import com.iknow.train.eigen.AllPixelEigenvectorStrategy;
import com.iknow.train.eigen.IEigenvectorStrategy;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for train samples
 * Notice: sample image should be named like XXXX.png, where all 'X's represent the vcode content by consequence
 */
public abstract class AbstractSampleTrain
{
    protected final String sampleDir;
    protected final String trainDataPath;
    protected final String trainClassPath;
    protected final IEigenvectorStrategy eigenvetorStrategy;
    protected final ISampleNamingRule namingRule;
//    /**
//     * key      - sampleData (channel should be 1)
//     * value    - class
//     */
//    protected List<File> srcImages;


    /**
     * load and train all samples in sampleDir
     * @param sampleDir
     * @param trainDataPath
     * @param trainClassPath
     * @param eigenvecStrategy
     * @param namingRule
     */
    public AbstractSampleTrain(String sampleDir,
                               String trainDataPath,
                               String trainClassPath,
                               IEigenvectorStrategy eigenvecStrategy,
                               ISampleNamingRule namingRule)
    {
        this.sampleDir = sampleDir;
        this.trainDataPath = trainDataPath;
        this.trainClassPath = trainClassPath;
        this.eigenvetorStrategy = eigenvecStrategy;
        this.namingRule = namingRule;
    }

    public AbstractSampleTrain(String sampleDir, ISampleNamingRule namingRule)
    {
        this.sampleDir = sampleDir;
        this.namingRule = namingRule;
        this.trainDataPath = SampleConstants.TRAIN_DATA_PATH;
        this.trainClassPath = SampleConstants.TRAIN_CLASSES_PATH;
        this.eigenvetorStrategy = new AllPixelEigenvectorStrategy();
    }


    public Mat getTrainDataMat()
    {
        return loadTrainedDataFromFileSystem(trainDataPath);
    }

    public Mat getTrainClassMat()
    {
        return loadTrainedDataFromFileSystem(trainClassPath);
    }

    public abstract Mat normalization(Mat mat);

    public IEigenvectorStrategy getEigenvetorStrategy()
    {
        return eigenvetorStrategy;
    }

//    public void train()
//    {
//        fillSampleEntries();
//        generateTrainDataAndTrainClassFile();
//    }

    /**
     * @param classifiedSamples should not be null
     */
    protected void fillSampleEntries(Map<Mat,Integer> classifiedSamples)
    {
        if(classifiedSamples == null)
        {
            System.out.println("classifiedSamples to be filled should not be null!");
            return;
        }
        File sampleDirec = new File(sampleDir);
        File[] samples = sampleDirec.listFiles();
        for(File sample : samples)
        {
            Mat mat = ImageUtils.readImage(sample.getAbsolutePath());
            List<Mat> sampleClusterMat = generateSampleCluster(mat);

            char ch =namingRule.getClassification(sample.getName());
//            String sampleName = sample.getName().substring(0,sample.getName().indexOf("."));
////            int index = Integer.parseInt(String.valueOf(sampleName.charAt(sampleName.length()-1)));
////            char ch = sampleName.charAt(index);
//            char ch= sampleName.charAt(0);

            for(Mat sampleMat : sampleClusterMat)
            {
                classifiedSamples.put(sampleMat, ClassificationConsts.classList.indexOf(ch));
            }
        }
    }

    /**
     * generate related samples according to current mat
     * @param mat
     * @return
     */
    protected abstract List<Mat> generateSampleCluster(Mat mat);

    /**
     * set train data and classes according to samples
     *
     * @return <trainData , trainClass>
     */
    public void generateTrainDataAndTrainClassFile()
    {
        Map<Mat,Integer> classifiedSamples = new HashMap<>();
        fillSampleEntries(classifiedSamples);

        Mat trainData = null;
        Mat trainClass = new Mat(classifiedSamples.size(), 1, CvType.CV_32FC1);
        //TODO: not good, should remove i here.
        int i = 0;
        for (Map.Entry<Mat, Integer> entry : classifiedSamples.entrySet())
        {
            Mat curSample = entry.getKey();
            int curClass = entry.getValue();
            //put trainData
            Mat curEigen = eigenvetorStrategy.getEigenVec(curSample);
            if(trainData == null)
            {
                trainData = new Mat(classifiedSamples.size(), curEigen.cols(), CvType.CV_32FC1);
            }
            for (int j = 0; j < curEigen.cols(); ++j)
            {
                trainData.put(i, j, curEigen.get(0, j)[0]);
            }
            //put trainClass
            trainClass.put(i, 0, curClass);
            ++i;
        }

        ImageUtils.deleteImage(trainDataPath);
        ImageUtils.deleteImage(trainClassPath);
        Imgcodecs.imwrite(trainDataPath, trainData);
        Imgcodecs.imwrite(trainClassPath, trainClass);
    }

    /**
     * @param dataPath
     */
    private Mat loadTrainedDataFromFileSystem(String dataPath)
    {
        File file = new File(dataPath);
        if (!file.exists())
        {
            return null;
        }

        Mat data = ImageUtils.readImage(dataPath);
        Mat ret = ImageUtils.color2Gray(data);
        ret.convertTo(ret, CvType.CV_32FC1);
        return ret;
    }

//    /**
//     * process src image for recognition
//     * <p>
//     * should be overrided
//     *
//     * @param src
//     * @param segStrategy segmentation strategy if needed
//     * @return
//     */
//    public List<Mat> process(Mat src, AbstractSegStrategy segStrategy)
//    {
//        List<Mat> ret = new ArrayList<>();
//        Mat temp = ImageUtils.reduceColor(src, 1);
//        Mat gray = ImageUtils.color2Gray(temp);
//        Mat binary = ImageUtils.gray2BinaryReverse(gray);
//        ret.add(binary);
//        return ret;
//    }
}
