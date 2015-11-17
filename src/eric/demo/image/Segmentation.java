package eric.demo.image;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Segmentation
{


    /**
     * generate digit images for recognition
     *
     * @param absolutePathOfPic
     * @param roiRect           ROI (which region of the image want to be recognized)
     * @return
     */
    public static List<Mat> digitSegmentationWithROI(String absolutePathOfPic, Rect roiRect)
    {
        System.out.println("process Segmentation... ");
        Mat src = Imgcodecs.imread(absolutePathOfPic);

        Mat roi = src.submat(roiRect);

        //preprocessing
        Mat preprocessed = ImageUtils.preProcess(roi);
        if (preprocessed == null)
        {
            System.out.println("preprocess failed");
            return null;
        }

        //segmentation
        List<Mat> segments = doSegmentation(preprocessed,new SegByContours());
        if (segments == null)
        {
            System.out.println("doSegmentation failed");
            return null;
        }
        return segments;
    }

    /**
     * @param src a binary image that has been preprocessed
     * @return return mats that contain digits IN ORDER, if fails return null
     */
    private static List<Mat> doSegmentation(Mat src,SegStrategy strategy)
    {
        List<Rect> digitRects = strategy.doSegmentation(src);

        if (digitRects == null || digitRects.size() == 0)
        {
            return null;
        }

        List<Mat> unNormalizedDigits = ImageUtils.getDigitMatsByRects(digitRects, src);

        if (unNormalizedDigits == null || unNormalizedDigits.size() == 0)
        {
            System.out.println("do segmentation fails");
            return null;
        }

        if (ImageUtils.dumpUnNormalizedSamples)
        {
            for (Mat mat : unNormalizedDigits)
            {
//                Mat enlarged = enlargeMat(mat, IMAGE_ENLARGE_SIZE, IMAGE_ENLARGE_SIZE);
                String pathToSave = ImageUtils.unNormalizedDir + File.separator +
                        ImageUtils.dumpPicName.substring(0, ImageUtils.dumpPicName.indexOf(".")) +
                        unNormalizedDigits.indexOf(mat) + "_unNormalized.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }

        List<Mat> normalized = new ArrayList<Mat>();
        for (Mat tmp : unNormalizedDigits)
        {
            normalized.add(ImageUtils.normalization(tmp));
        }

        if (ImageUtils.dumpImg)
        {
            for (Mat mat : normalized)
            {
                String pathToSave = ImageUtils.dumpPicName.substring(0, ImageUtils.dumpPicName.indexOf(
                        ".")) + normalized.indexOf(mat) + "_processed.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return normalized;

    }

    /**
     * if fails , return null
     *
     * @param absolutePathOfPic
     * @return
     */
    public static List<Mat> digitSegmentation(String absolutePathOfPic)
    {
        return digitSegmentationWithROI(absolutePathOfPic, new Rect(3, 3, 105, 26));
    }
}
