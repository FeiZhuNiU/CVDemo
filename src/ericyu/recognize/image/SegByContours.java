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
    private List<Rect> getDigitRects(Mat src)
    {
        List<MatOfPoint> contours = ImageUtils.findContours(src);

        List<Rect> boundRects = new ArrayList<Rect>();

        for (int i = 0; i < contours.size(); ++i)
        {
            //get rect bound of contour
            Rect rect = Imgproc.boundingRect(contours.get(i));
            //give up small rects which we assert they are not digits
            if(rect.width+rect.height > 18)
                boundRects.add(rect);
        }

        checkAndSplitBounds(boundRects);
        return boundRects;
    }

    /**
     * split bounds if digits are connected
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
        }
        else if (rectCount == 2)
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
        }
        else if (rectCount == 1)
        {
            rectsSplited = ImageUtils.splitRect(boundRects.get(0), 4);
            boundRects.remove(0);
            boundRects.addAll(rectsSplited);
        }
    }


}
