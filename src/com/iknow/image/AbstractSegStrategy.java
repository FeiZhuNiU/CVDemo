package com.iknow.image;
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

import java.util.List;

public abstract class AbstractSegStrategy
{
    protected Mat src;

    public AbstractSegStrategy(Mat mat)
    {
        src = mat;
    }

    public void setSrc(Mat src)
    {
        this.src = src;
    }

    /**
     * should return black background binary image
     * @return
     */
    protected abstract Mat preProcess();

    protected abstract List<Rect> getSegRects(Mat preprocessed);

    /**
     * @return return mats should be ordered
     */
    public List<Mat> doSegmentation()
    {
        Mat preprocessed = preProcess();
        if (preprocessed == null)
        {
            return null;
        }
        List<Rect> segRects = getSegRects(preprocessed);
        if(segRects == null)
        {
            System.out.println("segmentation failed!");
            return null;
        }
        List<Mat> ret =  ImageUtils.getOrderedMatsByRects(segRects, preprocessed);
        postProcessSegedMats(ret);
        return ret;
    }

    protected void postProcessSegedMats(List<Mat> mats)
    {
//        for(Mat mat: mats)
//        {
//            ImageUtils.keepLargestNRect(mat,1);
//        }
    }
}
