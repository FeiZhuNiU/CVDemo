package ericyu.chepai.train;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/28/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/
import ericyu.chepai.FileUtils;
import ericyu.chepai.Logger;
import ericyu.chepai.image.AbstractSegStrategy;
import ericyu.chepai.image.ImageUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class for train samples and process src image for recognition {@link #process}
 *
 * 0. use {@link DumpSample#main} to generate sample
 *
 * 1. use following two constructors to train samples
 * @see #AbstractSampleTrain(String[] images, IEigenvectorStrategy eigenvetorStrategy, String trainDataPath, String trainClassPath)
 * @see #AbstractSampleTrain(String dir, IEigenvectorStrategy eigenvetorStrategy, String trainDataPath, String trainClassPath)
 *
 * 2. use {@link #train()} to generate train data
 * 3. use {@link #dumpTrainData()} to dump train data to local file
 *
 * 4. use following construtor to recognize
 * @see #AbstractSampleTrain(String trainDataPath, String trainClassPath, IEigenvectorStrategy eigenvetorStrategy)
 */
public abstract class AbstractSampleTrain
{
    /**
     * path of samples
     */
    protected List<File> srcImages;
    private IEigenvectorStrategy eigenvetorStrategy;
    /**
     * key      - sampleData (channel should be 1)
     * value    - class
     */
    protected List<Map.Entry<Mat, Integer>> sampleEntries;
    protected Mat trainData;
    protected Mat trainClass;

    protected String trainDataPath;
    protected String trainClassesPath;

    /**
     * load trained data
     * @param trainDataPath
     * @param trainClassPath
     * @param eigenvectorStrategy
     */
    public AbstractSampleTrain(String trainDataPath, String trainClassPath, IEigenvectorStrategy eigenvectorStrategy)
    {
        if(trainDataPath == null ||
                trainClassPath == null ||
                !(new File(trainDataPath).exists()) ||
                !(new File(trainClassPath).exists()))
        {
            Logger.log(Logger.Level.ERROR, null, "Trained data does not exist! SampleTrain init failed! Use other constructor first.");
            return;
        }
        this.eigenvetorStrategy = eigenvectorStrategy;
        trainData = loadTrainedDataFromFileSystem(trainDataPath);
        trainClass = loadTrainedDataFromFileSystem(trainClassPath);
    }

    /**
     * load and train all samples in srcImages
     * @param images
     * @param eigenvectorStrategy
     */
    public AbstractSampleTrain(String[] images, IEigenvectorStrategy eigenvectorStrategy, String trainDataPath, String trainClassPath)
    {
        init(eigenvectorStrategy,trainDataPath,trainClassPath);
        this.srcImages = new ArrayList<>();
        for(String str : images)
        {
            srcImages.add(new File(str));
        }
    }

    /**
     * load and train all samples in dir
     * @param dir under where all files are samples
     * @param eigenvecStrategy
     */
    public AbstractSampleTrain(String dir, IEigenvectorStrategy eigenvecStrategy, String trainDataPath, String trainClassPath)
    {
        init(eigenvecStrategy,trainDataPath,trainClassPath);
        File file = new File(dir);
        this.srcImages = Arrays.asList(file.listFiles());
    }

    /**
     * only for dump sample !
     */
    @Deprecated
    public AbstractSampleTrain()
    {
    }

    private void init(IEigenvectorStrategy eigenvetorStrategy, String trainDataPath, String trainClassPath)
    {
        this.trainDataPath = trainDataPath;
        this.trainClassesPath = trainClassPath;
        this.eigenvetorStrategy = eigenvetorStrategy;
    }

    /**
     * set following fields
     * @see AbstractSampleTrain#sampleEntries
     * @see AbstractSampleTrain#trainData
     * @see AbstractSampleTrain#trainClass
     */
    protected void train()
    {
        setSampleEntries();
        setTrainDataAndTrainClasses();
    }

    public Mat getTrainData()
    {
        return trainData;
    }

    public Mat getTrainClass()
    {
        return trainClass;
    }

    public IEigenvectorStrategy getEigenvetorStrategy()
    {
        return eigenvetorStrategy;
    }

    /**
     * set
     * @see AbstractSampleTrain#sampleEntries
     * @return
     */
    abstract protected void setSampleEntries();

    /**
     * set train data and classes according to samples
     * @return <trainData , trainClass>
     */
    protected void setTrainDataAndTrainClasses()
    {
        Mat tmp = sampleEntries.get(0).getKey();
        int eigenLength = eigenvetorStrategy.getEigenVec(tmp).cols();
        trainData = new Mat(sampleEntries.size(), eigenLength, CvType.CV_32FC1);
        trainClass = new Mat(sampleEntries.size(), 1, CvType.CV_32FC1);

        for (int i = 0; i < sampleEntries.size(); ++i)
        {
            Mat curSample = sampleEntries.get(i).getKey();
            int curClass = sampleEntries.get(i).getValue();
            //put trainData
            Mat curEigen = eigenvetorStrategy.getEigenVec(curSample);
            for (int j = 0; j < eigenLength; ++j)
            {
                trainData.put(i, j, curEigen.get(0,j)[0]);
            }
            //put trainClass
            trainClass.put(i, 0, curClass);
        }
    }

    public void dumpTrainData()
    {
        ImageUtils.deleteImage(trainDataPath);
        ImageUtils.deleteImage(trainClassesPath);

        FileUtils.mkDir(new File(SampleConstants.TRAIN_DATA_DIR));

        Imgcodecs.imwrite(trainDataPath, trainData);
        Imgcodecs.imwrite(trainClassesPath, trainClass);
    }


    /**
     * @param dataPath
     */
    private Mat loadTrainedDataFromFileSystem(String dataPath)
    {
        File file = new File(dataPath);
        if (!file.exists())
        {
            Logger.log(Logger.Level.ERROR, null, "train data not prepared. please run train() in SampleTrain.");
            return null;
        }

        Mat data = ImageUtils.readImage(dataPath);
        Mat ret = ImageUtils.color2Gray(data);
        ret.convertTo(ret, CvType.CV_32FC1);
        return ret;
    }

    /**
     * process src image for recognition
     *
     * should be overrided
     * @param src
     * @param segStrategy segmentation strategy if needed
     * @return
     */
    public List<Mat> process(Mat src, AbstractSegStrategy segStrategy)
    {
        List<Mat> ret = new ArrayList<>();
        Mat temp = ImageUtils.reduceColor(src,1);
        Mat gray = ImageUtils.color2Gray(temp);
        Mat binary = ImageUtils.gray2Binary(gray);
        ret.add(binary);
        return ret;
    }
}
