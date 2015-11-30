package ericyu.chepai.flash;

/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/

import ericyu.chepai.Logger;
import ericyu.chepai.image.ImageUtils;
import ericyu.chepai.recognize.Recognition;
import ericyu.chepai.train.FlashStatusTrain;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

/**
 * A detector to monitor flash status
 *
 * recognize current status by the right part of the flash
 * TrainClass: {@link ericyu.chepai.train.FlashStatusTrain}
 * detectRate: {@value #detectRate}
 *
 * TODO: singleton??
 */
public class FlashStatusDetector implements Runnable
{
    private static int detectRate = 100;
    private List<IStatusObserver> observers;
    private FlashPosition flashPosition;
    private Status status;
    private Recognition recognition;
    public enum Status
    {
        NONE,
        LOGIN,
        BID,
        V_CODE,
        NOTIFICATION
    }

    public FlashStatusDetector(Recognition recognition)
    {
        this.recognition = recognition;
        this.flashPosition = FlashPosition.getInstance();
        status = Status.NONE;
    }

    public void notifyStatusObservers(Status status)
    {
        if (observers!=null)
        {
            for(IStatusObserver observer : observers)
            {
                Logger.log(Logger.Level.INFO, status, "send notification to " + observer.getClass().getSimpleName());
                observer.flashStatusChanged(status);
            }
        }
    }

    public void addStatusObserver(IStatusObserver observer)
    {
        if(observers == null)
        {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    public void removeStatusObserver(IStatusObserver observer)
    {
        if(observers != null)
        {
            observers.remove(observer);
        }
    }

    /**
     * detect current Flash status every {@value #detectRate} ms
     */
    @Override
    public void run()
    {
        while(true)
        {
            FlashStatusDetector.Status originStatus = status;
            FlashStatusDetector.Status curStatus = Status.NONE;

            Mat target = getRightPartOfFlash();
            List<Mat> toRecogs = recognition.getTrainedData().process(target);
            int result = recognition.recognize(toRecogs.get(0),1);
            switch (result)
            {
                case 1:
                    curStatus = Status.LOGIN;
                    break;
                case 2:
                    curStatus = Status.BID;
                    break;
                case 3:
                    curStatus = Status.V_CODE;
                    break;
                case 4:
                    curStatus = Status.NOTIFICATION;
                    break;
            }
            // if status changed, send notification
            if (curStatus != originStatus)
            {
                Logger.log(Logger.Level.INFO, status,"FlashStatus ready to change to " + curStatus);
                setStatus(curStatus);
                Logger.log(Logger.Level.INFO, status, "FlashStatus changed to " + curStatus);
                notifyStatusObservers(curStatus);
            }
            try
            {
                Thread.sleep(detectRate);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    private Mat getRightPartOfFlash()
    {
        Mat ret = ImageUtils.screenCapture(
                flashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
                flashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
        return ret;
    }

    private void setStatus(Status status)
    {
        this.status = status;
    }

    public Status getStatus()
    {
        return status;

    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FlashStatusDetector detector = new FlashStatusDetector(new Recognition(new FlashStatusTrain()));
        detector.generateSample();
//        Thread thread = new Thread(detector);
//        thread.start();
    }

    /**
     * capture
     * @return
     */
    private Mat generateSample()
    {
//        Mat src = getRightPartOfFlash();
        Mat src = ImageUtils.screenCapture(
                FlashPosition.getInstance().origin.x + FlashPosition.REGION_VERIFICATION_CODE_LT_X,
                FlashPosition.getInstance().origin.y + FlashPosition.REGION_VERIFICATION_CODE_LT_Y,
                FlashPosition.REGION_VERIFICATION_CODE_WIDTH,
                FlashPosition.REGION_VERIFICATION_CODE_HEIGHT
                );
        Mat binary = ImageUtils.color2Binary(src,220);
        Imgcodecs.imwrite("test.bmp", binary);
        return binary;
    }
}
