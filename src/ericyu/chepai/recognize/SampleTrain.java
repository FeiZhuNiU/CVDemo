package ericyu.chepai.recognize;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/28/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/
import ericyu.chepai.image.ImageUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * trainData and trainClass would be set in constructor
 */
public abstract class SampleTrain
{
    /**
     * path of samples
     */
    protected String[] srcImages;
    /**
     * key      - sampleData
     * value    - class
     */
    protected List<Map.Entry<Mat, Integer>> sampleEntries;
    protected Mat trainData;
    protected Mat trainClass;
    public String trainDataPath;
    public String trainClassPath;

    /**
     * load trained data
     * @param trainDataPath
     * @param trainClassPath
     */
    public SampleTrain(String trainDataPath, String trainClassPath)
    {
        if(trainDataPath == null ||
                trainClassPath == null ||
                !(new File(trainDataPath).exists()) ||
                !(new File(trainClassPath).exists()))
        {
            System.out.println("Trained data does not exist! SampleTrain init failed! Use other constructor first.");
            return;
        }
        this.trainDataPath = trainDataPath;
        this.trainClassPath = trainClassPath;
        trainData = loadTrainedDataFromFileSystem(trainDataPath);
        trainClass = loadTrainedDataFromFileSystem(trainClassPath);
    }

    /**
     * load and train all samples in srcImages
     * @param srcImages
     */
    public SampleTrain(String[] srcImages)
    {
        this.srcImages = srcImages;
        train();
    }

    /**
     * load and train all samples in dir
     * @param dir under where all files are samples
     */
    public SampleTrain(String dir)
    {
        File file = new File(dir);
        this.srcImages = file.list();
        train();
    }

    /**
     * set following fields
     * @see SampleTrain#sampleEntries
     * @see SampleTrain#trainData
     * @see SampleTrain#trainClass
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

    /**
     * set
     * @see SampleTrain#sampleEntries
     * @return
     */
    abstract protected void setSampleEntries();

    /**
     * set train data and classes according to samples
     * @return <trainData , trainClass>
     */
    protected void setTrainDataAndTrainClasses()
    {
        trainData = new Mat(sampleEntries.size(), sampleEntries.get(0).getKey().rows() * sampleEntries.get(0).getKey().cols(),
                CvType.CV_32FC1);
        trainClass = new Mat(sampleEntries.size(), 1, CvType.CV_32FC1);

        for (int i = 0; i < sampleEntries.size(); ++i)
        {
            int curVal = sampleEntries.get(i).getValue();
            Mat curMat = sampleEntries.get(i).getKey();
            trainClass.put(i, 0, curVal);
            for (int j = 0; j < trainData.cols(); ++j)
            {
                trainData.put(i, j, curMat.get(j / curMat.cols(), j % curMat.cols())[0]);
            }
        }
    }

    /**
     * create dir recursively if not exist
     * @param file
     */
    public static void mkDir(File file)
    {
        file = file.getAbsoluteFile();
        if (file.getParentFile().exists())
        {
            file.mkdir();
        } else
        {
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }

    public static void writeObjectToFile(Object obj, String dst)
    {
        File file = new File(dst);
        FileOutputStream out;
        try
        {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (Exception e)
        {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }

    public void dumpTrainData(String trainDataPath, String trainClassesPath)
    {
        ImageUtils.deleteImage(trainDataPath);
        ImageUtils.deleteImage(trainClassesPath);

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
            System.out.println("train data not prepared. please run train() in SampleTrain.");
            return null;
        }

        Mat data = ImageUtils.readImage(dataPath);
        Mat ret = ImageUtils.color2Gray(data);
        ret.convertTo(ret, CvType.CV_32FC1);
        return ret;
    }
}
