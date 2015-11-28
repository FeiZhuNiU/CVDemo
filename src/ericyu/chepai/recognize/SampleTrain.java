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
 * trainData and trainClasses would be set in constructor
 */
public abstract class SampleTrain
{
    /**
     * path of samples
     */
    protected String[] sampleImages;
    /**
     * key      - sampleData
     * value    - class
     */
    protected List<Map.Entry<Mat, Integer>> samples;
    protected Mat trainData;
    protected Mat trainClasses;

    public SampleTrain(String[] sampleImages)
    {
        this.sampleImages = sampleImages;
        train();
    }

    /**
     * @param dir under where all files are samples
     */
    public SampleTrain(String dir)
    {
        File file = new File(dir);
        this.sampleImages = file.list();
        train();
    }

    protected void train()
    {
        samples = generateSamplesClassMap();
        setTrainDataAndTrainClasses();
    }

    public Mat getTrainData()
    {
        return trainData;
    }

    public Mat getTrainClasses()
    {
        return trainClasses;
    }

    /**
     * generate list of <sample, class> pairs
     * @return
     */
    abstract protected List<Map.Entry<Mat, Integer>> generateSamplesClassMap();

    /**
     * @return <trainData , trainClasses>
     */
    protected void setTrainDataAndTrainClasses()
    {
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
        Imgcodecs.imwrite(trainClassesPath, trainClasses);
    }
}
