package ericyu.chepai.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Segmentation
{
    public static final int NORMALIZATION_WIDTH = 35;
    public static final int NORMALIZATION_HEIGHT = 35;
    public static final int IMAGE_ENLARGE_SIZE = 10;
    public static String unNormalizedDir = "dump\\unNormalized";
    public static boolean dumpImg = true;
    public static String dumpDir = "dump\\";
    public static String dumpPicName = ".png";
    public static boolean dumpUnNormalizedSamples = false;
    public static String sampleImageFormat = "png";
    public static String normalizedSkeletonDir = "dump\\NormalizedSkeleton";

    /**
     * segment src images into small ones for recognition
     *
     * @param src      image to segment
     * @param roiRect  ROI (which region of the image want to be recognized)
     * @param strategy segmentation stategy
     * @return normalized mats
     */
    public static List<Mat> segmentROI(Mat src, Rect roiRect, SegStrategy strategy)
    {
        Mat roi = src.submat(roiRect);
        return segment(roi, strategy);
    }

    /**
     * main logic for segmentation
     *
     * @param src      image to segment
     * @param strategy ROI (which region of the image want to be recognized)
     * @return return null if fails
     */
    public static List<Mat> segment(Mat src, SegStrategy strategy)
    {
        System.out.println("process Segmentation... ");

        List<Mat> unNormalizedDigits = strategy.doSegmentation(src);

        if (unNormalizedDigits == null || unNormalizedDigits.size() == 0)
        {
            System.out.println("doSegmentation in strategy fails");
            return null;
        }

        if (dumpUnNormalizedSamples)
        {
            for (Mat mat : unNormalizedDigits)
            {
                //06093_unNormailized.png   where 3 represents that current digit is 0609[3]
                String pathToSave = unNormalizedDir + File.separator +
                        dumpPicName.substring(0, dumpPicName.indexOf(".")) +
                        unNormalizedDigits.indexOf(mat) + "_unNormalized.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }

        List<Mat> normalized = new ArrayList<>();
        for (Mat tmp : unNormalizedDigits)
        {
            normalized.add(normalization(tmp));
        }

        if (dumpImg)
        {
            for (Mat mat : normalized)
            {
                //06093_normalized.png
                String pathToSave = dumpPicName.substring(0, dumpPicName.indexOf(
                        ".")) + normalized.indexOf(mat) + "_normalized.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return normalized;

    }

    /**
     * process digit mats
     * 1.   cut
     * 2.   enlarge
     * 3.   resize
     * 4.   binaryzation
     *
     * @param src must be gray image
     * @return
     */
    public static Mat normalization(Mat src)
    {
        if (src == null)
        {
            return null;
        }
        Mat cut = ImageUtils.cutImage(src);
        Mat enlarged = ImageUtils.enlargeMat(cut, IMAGE_ENLARGE_SIZE, IMAGE_ENLARGE_SIZE);
        Mat resized = new Mat();
        Imgproc.resize(enlarged, resized, new Size(NORMALIZATION_WIDTH, NORMALIZATION_HEIGHT));
        Mat binary = new Mat();
        Imgproc.threshold(resized, binary, 90, 255, Imgproc.THRESH_BINARY);
        Mat ret = binary;

        return ret;
    }
}
