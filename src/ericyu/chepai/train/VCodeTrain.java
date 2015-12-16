package ericyu.chepai.train;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/31/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.Logger;
import ericyu.chepai.image.ImageUtils;
import ericyu.chepai.image.SegSingleColor;
import ericyu.chepai.image.Segmentation;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * this class is for generate train data and classes
 */
public class VCodeTrain extends AbstractSampleTrain
{
    private static VCodeTrain vCodeTrain;

    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        vCodeTrain = new VCodeTrain(
                SampleConstants.V_CODE_SAMPLE_TRAIN_DATA_PATH,
                SampleConstants.V_CODE_SAMPLE_TRAIN_CLASSES_PATH,
                new AllPixelEigenvectorStrategy());
    }

    public static VCodeTrain getInstance()
    {
        return vCodeTrain;
    }
    //TODO: why this constructor will cause memory leak
    private VCodeTrain(String trainDataPath, String trainClassPath, IEigenvectorStrategy eigenvetorStrategy)
    {
        super(trainDataPath, trainClassPath, eigenvetorStrategy);
    }

    public VCodeTrain(String[] images, IEigenvectorStrategy eigenvetorStrategy, String trainDataPath,
                      String trainClassPath)
    {
        super(images, eigenvetorStrategy, trainDataPath, trainClassPath);
    }

    public VCodeTrain(String dir, IEigenvectorStrategy eigenvetorStrategy, String trainDataPath,
                      String trainClassPath)
    {
        super(dir, eigenvetorStrategy, trainDataPath, trainClassPath);
    }


    /**
     * generate rotated samples (normalized)
     *
     * @param imagePath input image (should NOT be normalized AND NOT be enlarged!)
     * @return          list where save the generated samples
     */
    private List<Map.Entry<Mat, Integer>> generateRotatedSamples(String imagePath)
    {
        List<Map.Entry<Mat, Integer>> samples = new ArrayList<>();
        Point[] offsets = new Point[]{
                new Point(0, 0),
                new Point(-1, -1),
                new Point(-1, 0),
                new Point(-1, 1),
                new Point(0, -1),
                new Point(0, 1),
                new Point(1, -1),
                new Point(1, 0),
                new Point(1, 1)
        };

        Mat src = ImageUtils.readImage(imagePath);
        Mat digit = ImageUtils.color2Gray(src);

        /**
         * generate samples
         * 0. enlarge by size(2,2) and offset
         * 1. rotate
         * 2. cut with new contour
         * 3. enlarge
         */

        String curFileName = new File(imagePath).getName();
//        String curFileNameWithNoSuffix = curFileName.substring(0, curFileName.lastIndexOf("."));
        Integer curClass = Integer.valueOf(curFileName.substring(0, 1));


        for (Point offset : offsets)
        {
            //offset
            Mat offsetImage = ImageUtils.transition(digit, (int) offset.y, (int) offset.x);
            //enlarge
            Mat enlarged = ImageUtils.enlargeMat(offsetImage, 10, 10);

            for (int i = -16; i <= 16; ++i)
            {
                //rotate
                Mat rotated = ImageUtils.rotateMat(enlarged, i * 5);
                Mat normalized = Segmentation.normalization(rotated);
//                Imgcodecs.imwrite(dstDir + File.separator + curFileNameWithNoSuffix +
//                                          "_rotated_" + i * 5 +
//                                          "_x_" + offset.x +
//                                          "_y_" + offset.y +
//                                          ".bmp", normalized);
                samples.add(new AbstractMap.SimpleEntry<>(normalized, curClass));
            }
        }
        return samples;
    }

    /**
     * rename unNormalizedImages according to its name
     * <p/>
     * e.g.
     * <p/>
     * 21182_xxx.bmp   ->   1_21182.bmp
     * 1 is "2118".charAt(2)
     */
    private void renameUnNormalizedImage()
    {
        File file = new File(Segmentation.unNormalizedDir);
        File[] images = file.listFiles();
        for (File image : images)
        {
            String curName = image.getName();
            String numbers = curName.substring(0, 4);
            String index = curName.substring(4, 5);
            char targetNumber = numbers.charAt(Integer.parseInt(index));
            image.renameTo(
                    new File(Segmentation.unNormalizedDir + "\\" + String.valueOf(
                            targetNumber) + "_" + numbers + index + ".bmp"));
        }
    }

    private void generateUnNormalizedSample()
    {
        File unNormalizedFile = new File(Segmentation.unNormalizedDir);
        //clear origin unnormalized samples
        if (unNormalizedFile.exists())
        {
            File[] files = unNormalizedFile.listFiles();
            for (File cur : files)
            {
                cur.delete();
            }
        }
        mkDir(unNormalizedFile);
        Boolean bak = Segmentation.dumpImg;
        Segmentation.dumpUnNormalizedSamples = true;
        Segmentation.dumpImg = false;
        for (File file : srcImages)
        {
            segmentationDemo(new String[]{file.toString()});
        }
        Segmentation.dumpUnNormalizedSamples = false;
        Segmentation.dumpImg = bak;
    }

    /**
     * this is a demo shows how to train samples
     * @param args
     */
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String[] sampleImages = new String[]{
                "CodeImage\\0069.jpg",
                "CodeImage\\0441.jpg",
                "CodeImage\\0472.jpg",
                "CodeImage\\0593.jpg",
                "CodeImage\\0687.jpg",
                "CodeImage\\0709.jpg",
                "CodeImage\\1132.jpg",
                "CodeImage\\1345.jpg",
                "CodeImage\\1347.jpg",
                "CodeImage\\1367.jpg",
                "CodeImage\\1587.jpg",
                "CodeImage\\1697.jpg",
                "CodeImage\\1932.jpg",
                "CodeImage\\2118.jpg",
                "CodeImage\\2781.jpg",
                "CodeImage\\3014.jpg",
                "CodeImage\\3436.jpg",
                "CodeImage\\3535.jpg",
                "CodeImage\\3797.jpg",
                "CodeImage\\4150.jpg",
                "CodeImage\\4212.jpg",
                "CodeImage\\5103.jpg",
                "CodeImage\\6399.jpg",
                "CodeImage\\6863.jpg",
                "CodeImage\\8429.jpg",
                "CodeImage\\8502.jpg",
                "CodeImage\\8983.jpg",
                "CodeImage\\9149.jpg",
                "CodeImage\\9566.jpg",
        };

        VCodeTrain train = new VCodeTrain(sampleImages,
                                          new AllPixelEigenvectorStrategy(),
                                          SampleConstants.V_CODE_SAMPLE_TRAIN_DATA_PATH,
                                          SampleConstants.V_CODE_SAMPLE_TRAIN_CLASSES_PATH);
        train.train();
        train.dumpTrainData();

    }

    protected void setSampleEntries()
    {

        /*
        if you want to remove some unnormalized images that not included in samples manually
        you can run 1 and 2 first, do the clean work and then run following job.
         */

//        1.
        generateUnNormalizedSample();

//        2.
        renameUnNormalizedImage();

        sampleEntries = new ArrayList<>();
        File file = new File(Segmentation.unNormalizedDir);
        File[] files = file.listFiles();
        //if no images in Segmentation.unNormalizedDir, return null
        if(files == null)
        {
            return;
        }
        for (File image : files)
        {
            String path = image.getAbsolutePath();
            sampleEntries.addAll(generateRotatedSamples(path));
        }
    }

    @Override
    public List<Mat> process(Mat src)
    {
        return Segmentation.segment(src, new SegSingleColor());
    }

    private void segmentationDemo(String[] args)
    {
        String imageFile = (args.length == 0 ? "CodeImage\\0687.jpg" : args[0]);
        Segmentation.dumpPicName = new File(imageFile).getName();
        long startTime = System.currentTimeMillis();
        Mat src = ImageUtils.readImage(imageFile);
        Segmentation.segment(src, new SegSingleColor());
        long endTime = System.currentTimeMillis();
        Logger.log(Logger.Level.INFO, null, "seg time: " + (endTime - startTime) / 1000.0);
    }
}
