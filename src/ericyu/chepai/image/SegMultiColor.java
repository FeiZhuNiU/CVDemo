package ericyu.chepai.image;
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
import org.opencv.imgcodecs.Imgcodecs;

import java.util.*;

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
        src = ImageUtils.removeNoise(src,3);
        Mat gray = ImageUtils.color2Gray(src);
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

        ret.add(0,0);
        ret.add(data.size()-1);
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
        SegMultiColor segMultiColor = new SegMultiColor();
        // 163616 656998 697144
        Mat test = ImageUtils.readImage("CodeImage\\697144.png");

        List<Mat> segs = segMultiColor.doSegmentation(test);
        for(Mat mat : segs)
        {
            Imgcodecs.imwrite("CodeImage\\" + segs.indexOf(mat) + ".bmp", mat);
        }


    }
}
