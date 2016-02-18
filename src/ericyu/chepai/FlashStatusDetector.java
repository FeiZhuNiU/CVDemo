package ericyu.chepai;

/*===========================================================================+
 |      Copyright (c) 2014 Oracle Corporation, Redwood Shores, CA, USA       |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/27/2015  (lin.yu@oracle.com)             |
 +===========================================================================*/

import com.iknow.image.ImageUtils;
import com.iknow.recognize.Recognition;
import com.iknow.train.DefaultNamingRule;
import com.iknow.train.eigen.AllPixelEigenvectorStrategy;
import com.iknow.train.eigen.RegionPixelEigenVecStrategy;
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.recognition.SampleConstants;
import ericyu.chepai.recognition.flashstatus.FlashStatusSeg;
import ericyu.chepai.recognition.flashstatus.FlashStatusTrain;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

/**
 * A detector to monitor flash status
 *
 * recognize current status by the right part of the flash
 * detectRate: {@value #detectRate}
 *
 * TODO: singleton??
 */
public class FlashStatusDetector implements Runnable
{
    private static int detectRate = 100;
    private List<IStatusObserver> observers;
    private Status status;
    private Recognition recognition;
    private FlashStatusSeg statusSeg;
    public enum Status
    {
        NONE,
        LOGIN,
        BID,
        VCODE,
        NOTIFICATION
    }

    public FlashStatusDetector()
    {
        this.recognition = new Recognition(new FlashStatusTrain(SampleConstants.FLASH_STATUS_SAMPLE_DIR,
                SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_DATA_PATH,
                SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_CLASSES_PATH,
                new AllPixelEigenvectorStrategy(), new DefaultNamingRule()));
        this.statusSeg = new FlashStatusSeg(null);
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
            try {
                FlashStatusDetector.Status curStatus;

                Mat target = getRightPartOfFlash();
                statusSeg.setSrc(target);

                List<Mat> toRecogs = statusSeg.doSegmentation();
                char result = recognition.recognize(toRecogs.get(0), 3);
                switch (result) {
                    case '1':
                        curStatus = Status.LOGIN;
                        break;
                    case '2':
                        curStatus = Status.BID;
                        break;
                    case '3':
                        curStatus = Status.VCODE;
                        break;
                    case '4':
                        curStatus = Status.NOTIFICATION;
                        break;
                    default:
                        curStatus = status;
                        break;
                }

                // if status changed, send notification
                if (curStatus != status) {
                    Logger.log(Logger.Level.INFO, status, "FlashStatus ready to change to " + curStatus);
                    status = curStatus;
                    Logger.log(Logger.Level.INFO, status, "FlashStatus changed to " + curStatus);
                    notifyStatusObservers(status);
                }


                try {
                    Thread.sleep(detectRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {
                //do nothing 
            }
        }
    }
    private Mat getRightPartOfFlash()
    {
        Mat ret = ImageUtils.screenCapture(
                FlashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
                FlashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
        return ret;
    }

    public Status getStatus()
    {
        return status;
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FlashStatusDetector detector = new FlashStatusDetector();
//        detector.generateSample();
        Thread thread = new Thread(detector);
        thread.start();
    }

    /**
     * capture
     * @return
     */
    private Mat generateSample()
    {
//        Mat src = getRightPartOfFlash();
        Mat src = ImageUtils.screenCapture(
                FlashPosition.origin.x + FlashPosition.REGION_VCODE_X,
                FlashPosition.origin.y + FlashPosition.REGION_VCODE_Y,
                FlashPosition.REGION_VCODE_WIDTH,
                FlashPosition.REGION_VCODE_HEIGHT
                );
        Mat binary = ImageUtils.color2BinaryInverse(src,220);
        Imgcodecs.imwrite("test.bmp", binary);
        return binary;
    }
}
