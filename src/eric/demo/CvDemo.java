package eric.demo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
    private static String picPath = "resources\\paimai.png";

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = Imgcodecs.imread(picPath);
        System.out.println("src: "+ src);

        Mat roi = getROI(src);
        System.out.println("roi: "+ roi);
        Imgcodecs.imwrite("resources\\roi.png",roi);

        Mat binary = getBinaryMat(roi);
        System.out.println("binary:" + binary);
        Imgcodecs.imwrite("resources\\binary.png",binary);

        Mat eroded = removeNoise(binary);
        System.out.println("eroded : " + eroded);
        Imgcodecs.imwrite("resources\\eroded.png",eroded);


        Mat eroded_bak = new Mat(eroded.rows(),eroded.cols(),CvType.CV_8UC3);
        eroded.convertTo(eroded_bak,CvType.CV_8UC3);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(eroded,contours,new Mat(),Imgproc.RETR_EXTERNAL  , Imgproc.CHAIN_APPROX_NONE);

        System.out.println("coutours counts : " + contours.size());
        List<Mat> sortedDigits = getSortedRectsOfDigits(contours,eroded_bak);
        System.out.println(sortedDigits.get(0));
    }

    private static List<Mat> getSortedRectsOfDigits(List<MatOfPoint> contours,Mat src)
    {
        Map<Double, Rect> sortedRects = new TreeMap<Double, Rect>();
        List<Mat> ret = new ArrayList<Mat>();
        for(int i = 0 ; i < contours.size(); ++i)
        {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            sortedRects.put(rect.tl().x, rect);
//            Imgproc.drawContours(eroded_bak, contours, i, new Scalar(0, 0, 255));
//            Imgproc.rectangle(eroded_bak,rect.tl(),rect.br(),new Scalar(0, 0, 255));
        }
        for (Map.Entry<Double, Rect> doubleRectEntry : sortedRects.entrySet())
        {
            System.out.println(doubleRectEntry.getValue());
            Mat cur = src.submat(doubleRectEntry.getValue());
            Mat resized = new Mat();
            Imgproc.resize(cur,resized,new Size(20,20));
            ret.add(resized);
            Imgcodecs.imwrite("resources\\digit"+ doubleRectEntry.getKey() +".png",resized);
        }
        return ret;
    }


    private static Mat removeNoise(Mat binary)
    {
        Mat eroded = new Mat(binary.rows(),binary.cols(),1);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(2, 2));
        Imgproc.erode(binary, eroded, element);
        Imgproc.dilate(eroded, eroded, element);

        return eroded;
    }

    private static Mat getBinaryMat(Mat roi)
    {
        int type = 1;
        List<Mat> mats = new ArrayList<Mat>();
        mats.add(new Mat(roi.rows(),roi.cols(),type));
        mats.add(new Mat(roi.rows(),roi.cols(),type));
        mats.add(new Mat(roi.rows(),roi.cols(),type));
        Core.split(roi, mats);
        for(int i = 0; i < 3; ++i){
            Imgcodecs.imwrite("resources\\mats" + i + ".png", mats.get(i));
        }

//        Mat red_blue = new Mat(roi.rows(),roi.cols(),type);
//        Core.absdiff(mats.get(2),mats.get(0),red_blue);
//        Imgcodecs.imwrite("resources\\red-blue.png",red_blue);
        Mat red_green = new Mat(roi.rows(),roi.cols(),type);
        Core.absdiff(mats.get(2),mats.get(1),red_green);
        Imgcodecs.imwrite("resources\\red-green.png",red_green);


        Mat binary = new Mat(roi.rows(),roi.cols(),CvType.CV_8U);
        Imgproc.threshold(red_green, binary, 100, 255, Imgproc.THRESH_BINARY);
        return binary;
    }

    private static Mat getRedRoi(Mat roi)
    {
        Mat ret = new Mat(roi.rows(),roi.cols(), roi.type());
        if(roi.elemSize()==3)
        {
            int totalBytes = (int) (roi.total() * roi.elemSize());
            byte buffer[] = new byte[totalBytes];
            roi.get(0, 0, buffer);
            for (int i = 0; i < totalBytes; ++i)
            {
                if (i % 3 != 2)
                {
                    buffer[i] = 127;
                }
            }
            ret.put(0,0,buffer);
        }
        return ret;
    }

    private static Mat getROI(Mat src)
    {
        return new Mat(src,new Rect(1090,425,120,34));
    }
}
