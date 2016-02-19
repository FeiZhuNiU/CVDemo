package ericyu.chepai.recognition.vcode;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import com.iknow.image.AbstractSegStrategy;
import com.iknow.image.ImageUtils;
import ericyu.chepai.utils.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.*;

/**
 * this is for color VCode
 */
public class SegMultiColor extends AbstractSegStrategy
{

    public SegMultiColor(Mat mat)
    {
        super(mat);
    }

    @Override
    public Mat preProcess()
    {
        if (src == null)
        {
            return null;
        }
        Mat noiseremoved = ImageUtils.removeNoise(src,3);
        Mat gray = ImageUtils.color2Gray(noiseremoved);
        Mat binary = ImageUtils.gray2Binary(gray);

        Imgcodecs.imwrite("CodeImage/gray2.bmp",binary);
        return binary;
    }

    @Override
    protected List<Rect> getSegRects(Mat src)
    {
        List<Rect> ret = new ArrayList<>();

        List<Double> data = countWhitePixels(src);
        List<Integer> segLines = getSegmentationLine(data);
        for(int i = 0 ; i < segLines.size()-1; ++i)
        {
            int x = segLines.get(i) ;
            int y = 0;
            int width = segLines.get(i+1) - segLines.get(i);
            int height = src.height();
            ret.add(new Rect(x,y,width,height ));
        }
        return ret;
    }


    @Override
    public List<Mat> doSegmentation()
    {
        Mat preprocessed = preProcess();
        if (preprocessed == null)
        {
            Logger.log(Logger.Level.WARNING, null,"preprocess failed");
            return null;
        }
        List<Rect> segRects = getSegRects(preprocessed);
        System.out.println(segRects);
        List<Mat> unNormalized = new ArrayList<>();
        for(Rect rect : segRects)
        {
//            unNormalized.add();
            Imgcodecs.imwrite("CodeImage\\processed" + segRects.indexOf(rect) + ".bmp", src.submat(rect));
            Mat mat = processRect(src,rect);
            unNormalized.add(mat);

        }
        return unNormalized;

    }

    /**
     * process each rect
     * @param src color image
     * @param rect roi
     * @return
     */
    private Mat processRect(Mat src, Rect rect)
    {
        Mat roi = src.submat(rect);
        Imgcodecs.imwrite("roi.bmp",roi);

        Mat noiseRemoved = ImageUtils.removeNoise(roi, 3);
        Imgcodecs.imwrite("noiseMoved.bmp",noiseRemoved);

        Mat gray = ImageUtils.color2Gray(noiseRemoved);
        Imgcodecs.imwrite("gray.bmp",gray);

        Mat binary = ImageUtils.gray2Binary(gray);
        Imgcodecs.imwrite("binary.bmp", binary);

        Mat smallPartRemoved = ImageUtils.removeSmallPart(binary,15);
        Imgcodecs.imwrite("removesmallpart.bmp", smallPartRemoved);

        Mat erosed = ImageUtils.erosion(smallPartRemoved,3);
        Imgcodecs.imwrite("erosed.bmp", erosed);

        smallPartRemoved = ImageUtils.removeSmallPart(erosed,20);
        Imgcodecs.imwrite("removesmallpart2.bmp", smallPartRemoved);

//        Mat dilated = ImageUtils.dilation(smallPartRemoved,3);
//        Imgcodecs.imwrite("dilated.bmp", dilated);
        return smallPartRemoved;


//        Mat colorReduced = ImageUtils.reduceColor(noiseRemoved, 128);
//        Imgcodecs.imwrite("colorReduced.bmp",colorReduced);

//        Mat mat_enhanced = ImageUtils.equalization(colorReduced);
//        Imgcodecs.imwrite("enhanced.bmp",mat_enhanced);

//        Mat mat_colorReduced_noiseremoved = ImageUtils.removeNoise(mat_enhanced, 3);
//        Imgcodecs.imwrite("colorReduced_noiseremoved.bmp",mat_colorReduced_noiseremoved);
//
//        Mat mat_getTargetColor = ImageUtils.getTargetColor(mat_colorReduced_noiseremoved, 3);
//        Imgcodecs.imwrite("getTargetColor.bmp",mat_getTargetColor);

//        if (mat_getTargetColor == null)
//        {
//            Logger.log(Logger.Level.WARNING, null, "get target color failed, there may be no digit");
//            return null;
//        }

//        Mat mat_binary = ImageUtils.color2Binary(mat_getTargetColor);
//        Imgcodecs.imwrite("binary.bmp",mat_binary);
//
//        Mat mat_binary_noiseRemoved = ImageUtils.removeNoise(mat_binary, 3);
//        Imgcodecs.imwrite("binary_noiseRemoved.bmp",mat_binary_noiseRemoved);
////        Mat mat_binary_noiseRemoved_removeNonDigit = removeSmallPart(mat_binary_noiseRemoved);
//
//        Mat ret = mat_binary_noiseRemoved;
//        return ret;
    }

    /**
     * get index of seg lines
     * including two edge
     * @param data
     * @return
     */
    private List<Integer> getSegmentationLine(List<Double> data)
    {
        List<Integer> ret;
        List<Map.Entry<Double,Integer>> peaks = new ArrayList<>();
        for(int i = 1; i < data.size()-1; ++i)
        {
            if(data.get(i) < data.get(i-1))
            {
                if (data.get(i) < data.get(i+1))
                {
                    peaks.add(new AbstractMap.SimpleEntry<>(data.get(i), i));
                }
                else if (Math.abs(data.get(i)-data.get(i+1)) <= 0.0001)
                {
                    int j = i+1;
                    while(j < data.size() && Math.abs(data.get(j)-data.get(i)) <= 0.0001 )
                    {
                        ++j;
                    }
                    if (j>=data.size())
                        break;
                    else if ( data.get(j) > data.get(i))
                    {
                        peaks.add(new AbstractMap.SimpleEntry<>(data.get(i),(j+i)/2));
                    }
                    else
                    {
                        i = j;
                    }
                }
            }
        }
//        System.out.println("peaks: " + peaks);
//        System.out.println("peak size: " + peaks.size());
        ret = findBottom(peaks,5);
//        System.out.println("top 5 : " + ret);

        ret.add(0,3);
        ret.add(data.size()-4);
//        System.out.println("all edge: " + ret);
        return ret;
    }

    private List<Integer> findBottom(List<Map.Entry<Double, Integer>> data, int n)
    {
        List<Integer> ret = new ArrayList<>();

        if (data.size()<=n)
        {
            for(Map.Entry<Double,Integer> entry : data)
            {
                ret.add(entry.getValue());
            }
        }
        else
        {
            Collections.sort(data, new Comparator<Map.Entry<Double, Integer>>()
            {
                @Override
                public int compare(Map.Entry<Double, Integer> o1, Map.Entry<Double, Integer> o2)
                {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            for (Map.Entry<Double, Integer> aSorted : data)
            {
                if (ret.size() < n)
                {
                    ret.add(aSorted.getValue());
                } else
                {
                    break;
                }
            }
        }
        Collections.sort(ret);
        return ret;

    }

    /**
     * count how many white pixels in each col
     * @param src
     * @return
     */
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
        //smooth
        List<Double> ret = new ArrayList<>();
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
    }
}
