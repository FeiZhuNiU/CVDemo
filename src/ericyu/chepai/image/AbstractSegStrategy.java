package ericyu.chepai.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/17/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

import ericyu.chepai.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

public abstract class AbstractSegStrategy
{


    abstract protected Mat preProcess(Mat src);


    abstract protected List<Rect> getSegRects(Mat src);

    /**
     * @param src color image
     * @return return mats should be ordered
     */
    public List<Mat> doSegmentation(Mat src)
    {
        Mat preprocessed = preProcess(src);
        if (preprocessed == null)
        {
            Logger.log(Logger.Level.WARNING, null,"preprocess failed");
            return null;
        }
        List<Rect> segRects = getSegRects(preprocessed);
        return ImageUtils.getOrderedMatsByRects(segRects, preprocessed);
    }
}
