package ericyu.recognize.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class SegByGap
        implements SegStrategy
{
    @Override
    public List<Mat> doSegmentation(Mat src)
    {
        if (src == null || src.type() != CvType.CV_8UC1)
        {
            System.out.println("the input Mat is null or its type is not CvType.CV_8UC1");
            return null;
        }
        List<Rect> ret = new ArrayList<Rect>();

        List<Integer> countNonZeros = countNonZeros(src);
        adjustCountList(countNonZeros, 3);
        int tmp = 0;
        for (int i : countNonZeros)
        {
            System.out.println(tmp++ + " -> " + i);
        }
        List<Integer> indexes = getSegmentationLine(countNonZeros);


        return null;
    }


    /**
     * the input should be zero - nonezero where zeros represent gaps
     *
     * @param countNonZeros
     * @return
     */
    private List<Integer> getSegmentationLine(List<Integer> countNonZeros)
    {
        if (countNonZeros == null || countNonZeros.size() == 0)
        {
            return null;
        }

        List<Integer> ret = new ArrayList<>();
        int len = countNonZeros.size();

        int zeroStart = 0;
        boolean inZero = false;

        for (int i = 0; i < len; ++i)
        {
            if (countNonZeros.get(i) > 0)
            {
                if (inZero)
                {
                    ret.add((i - zeroStart) / 2);
                    inZero = false;
                }
            } else
            {
                if (!inZero)
                {
                    zeroStart = i;
                    inZero = true;
                }
            }
        }
        //remove the first and last index
        ret.remove(ret.size() - 1);
        ret.remove(0);

        //TODO

        return null;
    }

    private void adjustCountList(List<Integer> countNonZeros, int threshold)
    {
        for (int i = 0; i < countNonZeros.size(); ++i)
        {
            if (countNonZeros.get(i) <= threshold)
            {
                countNonZeros.set(i, 0);
            } else
            {
                countNonZeros.set(i, 1);
            }
        }
    }

    private List<Integer> countNonZeros(Mat src)
    {
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < src.width(); ++i)
        {
            int cur = 0;
            for (int j = 0; j < src.height(); ++j)
            {
                if (src.get(j, i)[0] > 0)
                {
                    ++cur;
                }
            }
            ret.add(cur);
        }
        return ret;
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SegByGap segByGap = new SegByGap();
        Mat test = Imgcodecs.imread("dump\\fixed_.png");
        Mat test1 = ImageUtils.color2Gray(test);
        segByGap.doSegmentation(test1);
    }
}
