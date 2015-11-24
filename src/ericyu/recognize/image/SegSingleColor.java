package ericyu.recognize.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SegSingleColor
        implements SegStrategy
{
    /**
     * process the color image to a binary one with least noise
     *
     * @param src
     * @return
     */
    private static Mat preProcess(Mat src)
    {
        Mat mat_noiseMoved = ImageUtils.removeNoise(src, 3);

        Mat mat_colorReduced = ImageUtils.reduceColor(mat_noiseMoved, 128);

        Mat mat_colorReduced_noiseremoved = ImageUtils.removeNoise(mat_colorReduced, 3);

        Mat mat_getTargetColor = ImageUtils.getTargetColor(mat_colorReduced_noiseremoved, 2);
        if (mat_getTargetColor == null)
        {
            System.out.println("get target color failed, there may be no digit");
            return null;
        }

        Mat mat_binary = ImageUtils.color2Binary(mat_getTargetColor);

        Mat mat_binary_noiseRemoved = ImageUtils.removeNoise(mat_binary, 3);

//        Mat mat_binary_noiseRemoved_removeNonDigit = removeSmallPart(mat_binary_noiseRemoved);

        Mat ret = mat_binary_noiseRemoved;

//        Mat ret = erosion(mat_binary_noiseRemoved_removeNonDigit, 3);
//        ret = dilation(ret, 3);

        if (Segmentation.dumpImg)
        {
            Imgcodecs.imwrite(Segmentation.dumpDir + "roi_" + Segmentation.dumpPicName, src);
            Imgcodecs.imwrite(Segmentation.dumpDir + "noiseMoved_" + Segmentation.dumpPicName, mat_noiseMoved);
            Imgcodecs.imwrite(Segmentation.dumpDir + "getTargetColor_" + Segmentation.dumpPicName, mat_getTargetColor);
//            Imgcodecs.imwrite(dumpDir + "binary_noiseRemoved_removeNonDigit_" + dumpPicName, mat_binary_noiseRemoved_removeNonDigit);
            Imgcodecs.imwrite(Segmentation.dumpDir + "binary_noiseRemoved_" + Segmentation.dumpPicName, mat_binary_noiseRemoved);
            Imgcodecs.imwrite(Segmentation.dumpDir + "colorReduced_" + Segmentation.dumpPicName, mat_colorReduced);
            Imgcodecs.imwrite(Segmentation.dumpDir + "binary_" + Segmentation.dumpPicName, mat_binary);
            Imgcodecs.imwrite(Segmentation.dumpDir + "fixed_" + Segmentation.dumpPicName, ret);
        }
        return ret;
    }

    @Override
    public List<Mat> doSegmentation(Mat src)
    {
        Mat preprocessed = preProcess(src);
        if (src == null)
        {
            System.out.println("preprocess failed");
            return null;
        }
        List<Rect> digitRects = getDigitRects(preprocessed);
        return ImageUtils.getOrderedMatsByRects(digitRects, preprocessed);
    }

    /**
     * return bound rects each contains one digits
     *
     * @param src make sure not change src
     * @return need not be sorted and size should be 4
     */
    private List<Rect> getDigitRects(Mat src)
    {
        if (src == null)
        {
            return null;
        }
        List<MatOfPoint> contours = ImageUtils.findContours(src);

        List<Rect> boundRects = new ArrayList<>();

        for (int i = 0; i < contours.size(); ++i)
        {
            //get rect bound of contour
            Rect rect = Imgproc.boundingRect(contours.get(i));
            //give up small rects which we assert they are not digits
            if (rect.width + rect.height > 15)
                boundRects.add(rect);
        }

        checkAndSplitBounds(boundRects);
        return boundRects;
    }

    /**
     * split bounds if digits are connected
     * make sure there will be at least 4 bounds
     *
     * @param boundRects
     */
    private void checkAndSplitBounds(List<Rect> boundRects)
    {
        int rectCount = boundRects.size();
        if (rectCount >= 4 || rectCount == 0)
        {
            return;
        }
        Collections.sort(boundRects, new Comparator<Rect>()
        {
            @Override
            public int compare(Rect o1, Rect o2)
            {
                return ((Integer) o1.width).compareTo(o2.width);
            }
        });

        //����Rect������ ��ճ�������ֽ��зָ�
        List<Rect> rectsSplited;
        if (rectCount == 3)
        {
            rectsSplited = ImageUtils.splitRect(boundRects.get(2), 2);
            boundRects.remove(2);
            boundRects.addAll(rectsSplited);
        } else if (rectCount == 2)
        {
            //��������� һ����22����ճ�� ��һ������������ճ��
            if (boundRects.get(0).width > boundRects.get(1).width / 2)
            {
                List<Rect> rectsSplited1 = ImageUtils.splitRect(boundRects.get(0), 2);
                List<Rect> rectsSplited2 = ImageUtils.splitRect(boundRects.get(1), 2);
                boundRects.clear();
                boundRects.addAll(rectsSplited1);
                boundRects.addAll(rectsSplited2);
            }
            //��������ճ�������
            else
            {
                rectsSplited = ImageUtils.splitRect(boundRects.get(1), 3);
                boundRects.remove(1);
                boundRects.addAll(rectsSplited);
            }
        } else if (rectCount == 1)
        {
            rectsSplited = ImageUtils.splitRect(boundRects.get(0), 4);
            boundRects.remove(0);
            boundRects.addAll(rectsSplited);
        }
    }


}
