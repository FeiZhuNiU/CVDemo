package ericyu.recognize.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.Core;
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

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String imageFile = (args.length == 0 ? "CodeImage\\0687.jpg" : args[0]);
        ImageUtils.dumpPicName = new File(imageFile).getName();

        long startTime = System.currentTimeMillis();

        Mat src = Imgcodecs.imread(imageFile);
        Segmentation.segment(src, new SegSingleColor());
        long endTime = System.currentTimeMillis();
        System.out.println("seg time: " + (endTime - startTime) / 1000.0);

    }

    /**
     * segment src images into small ones for recognition
     *
     * @param src      image to segment
     * @param roiRect  ROI (which region of the image want to be recognized)
     * @param strategy segmentation stategy
     * @return
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

//        List<Mat> unNormalizedDigits = ImageUtils.getOrderedMatsByRects(segs, src);
//
//        if (unNormalizedDigits == null || unNormalizedDigits.size() == 0)
//        {
//            System.out.println("do segmentation fails");
//            return null;
//        }
//
        if (ImageUtils.dumpUnNormalizedSamples)
        {
            for (Mat mat : unNormalizedDigits)
            {
//                Mat enlarged = enlargeMat(mat, IMAGE_ENLARGE_SIZE, IMAGE_ENLARGE_SIZE);
                String pathToSave = unNormalizedDir + File.separator +
                        ImageUtils.dumpPicName.substring(0, ImageUtils.dumpPicName.indexOf(".")) +
                        unNormalizedDigits.indexOf(mat) + "_unNormalized.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }

        List<Mat> normalized = new ArrayList<>();
        for (Mat tmp : unNormalizedDigits)
        {
            normalized.add(normalization(tmp));
        }

//        if (ImageUtils.dumpImg)
//        {
//            for (Mat mat : normalized)
//            {
//                String pathToSave = ImageUtils.dumpPicName.substring(0, ImageUtils.dumpPicName.indexOf(
//                        ".")) + normalized.indexOf(mat) + "_processed.png";
//                Imgcodecs.imwrite(pathToSave, mat);
//            }
//        }
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
