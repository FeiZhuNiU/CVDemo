package eric.demo.image;
/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (lin.yu@oracle.com)              |
 +===========================================================================*/

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SegByContours
        implements SegStrategy
{
    @Override
    public List<Rect> doSegmentation(Mat src)
    {
        List<Rect> digitRects = getDigitRects(src);
        return digitRects;

    }

    /**
     * return bound rects each contains one digits
     *
     * @param src make sure not change src
     * @return need not be sorted and size should be 4
     */
    private static List<Rect> getDigitRects(Mat src)
    {
        List<MatOfPoint> contours = ImageUtils.findContours(src);

        if (contours.size() > 4)
        {
            //TODO: not good solution (MatOfPoint is the mat of contour points)
            Collections.sort(contours, new Comparator<MatOfPoint>()
            {
                @Override
                public int compare(MatOfPoint o1, MatOfPoint o2)
                {
                    if (o1.rows() + o1.cols() - o2.rows() - o2.cols() > 0)
                    {
                        return 1;
                    }
                    return -1;
                }
            });
            while (contours.size() > 4)
            {
                contours.remove(0);
            }
        }

        List<Rect> boundRects = new ArrayList<Rect>();
        for (int i = 0; i < contours.size(); ++i)
        {
            //get rect bound of contour
            Rect rect = Imgproc.boundingRect(contours.get(i));
            boundRects.add(rect);
//            Imgproc.drawContours(eroded_bak, contours, i, new Scalar(0, 0, 255));
//            Imgproc.rectangle(eroded_bak,rect.tl(),rect.br(),new Scalar(0, 0, 255));
        }

        checkAndSplitBounds(boundRects);
        return boundRects;

    }

    /**
     * split bounds if digits are connected
     *
     * @param boundRects
     */
    private static void checkAndSplitBounds(List<Rect> boundRects)
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

        //根据Rect的数量 对粘连的数字进行分割
        List<Rect> rectsSplited;
        if (rectCount == 3)
        {
            rectsSplited = ImageUtils.splitRect(boundRects.get(2), 2);
            boundRects.remove(2);
            boundRects.addAll(rectsSplited);
        }
        else if (rectCount == 2)
        {
            //分两种情况 一种是22数字粘连 另一种是三个数字粘连
            if (boundRects.get(0).width > boundRects.get(1).width / 2)
            {
                List<Rect> rectsSplited1 = ImageUtils.splitRect(boundRects.get(0), 2);
                List<Rect> rectsSplited2 = ImageUtils.splitRect(boundRects.get(1), 2);
                boundRects.clear();
                boundRects.addAll(rectsSplited1);
                boundRects.addAll(rectsSplited2);
            }
            //三个数字粘连的情况
            else
            {
                rectsSplited = ImageUtils.splitRect(boundRects.get(1), 3);
                boundRects.remove(1);
                boundRects.addAll(rectsSplited);
            }
        }
        else if (rectCount == 1)
        {
            rectsSplited = ImageUtils.splitRect(boundRects.get(0), 4);
            boundRects.remove(0);
            boundRects.addAll(rectsSplited);
        }
    }


}
