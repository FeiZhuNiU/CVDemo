package ericyu.chepai.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.recognition.software.jdeskew.ImageUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * this is for color VCode
 */
public class SegMultiColor extends AbstractSegStrategy
{
    @Override
    protected Mat preProcess(Mat src)
    {
        if (src == null)
        {
            return null;
        }
//        src = ImageUtils.equalization(src);
        src = ImageUtils.removeNoise(src,3);
        Mat gray = ImageUtils.color2Gray(src);

//        Mat equalized = new Mat();
//        Imgproc.equalizeHist(gray,equalized);
        gray = ImageUtils.gray2Binary(gray);

        Imgcodecs.imwrite("CodeImage/gray2.bmp",gray);
        return gray;

//        List<Rect> ret = new ArrayList<>();
//
//        List<Integer> countWhitePixels = countWhitePixels(src);
//        adjustCountList(countWhitePixels, 3);
//        int tmp = 0;
//        for (int i : countWhitePixels)
//        {
//            System.out.println(tmp++ + " -> " + i);
//        }
//        List<Integer> indexes = getSegmentationLine(countWhitePixels);
//
//
//        return null;
    }

    @Override
    protected List<Rect> getSegRects(Mat src)
    {
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

    private List<Double> countWhitePixels(Mat src)
    {
        List<Integer> cnt = new ArrayList<>();
        for (int i = 0; i < src.width(); ++i)
        {
            int cur = 0;
            for (int j = 0; j < src.height(); ++j)
            {
                if (src.get(j, i)[0] > 200)
                {
                    ++cur;
                }
            }
            //TODO: optimize
            // first and last 4 cols set 0
            if(i<4 || i >= src.width()-4)
            {
                cnt.add(0);
            }
            else
            {
                cnt.add(cur);
            }
        }
        List<Double> ret = new ArrayList<>();
        //smooth
        for(int i = 0 ; i < cnt.size(); ++i)
        {
            int cur = 0;
            cur += (i-1>=0 ? cnt.get(i-1) : 0);
            cur += cnt.get(i);
            cur += (i+1<cnt.size() ? cnt.get(i+1) : 0);
            ret.add(cur/3.0);
        }

        return ret;
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SegMultiColor segMultiColor = new SegMultiColor();
        // 163616 656998 697144
        Mat test = ImageUtils.readImage("CodeImage\\163616.png");
        Mat gray = segMultiColor.preProcess(test);
        System.out.println(segMultiColor.countWhitePixels(gray));

    }
}
