package eric.demo;

import eric.demo.image.FileUtils;
import eric.demo.image.ImageUtils;
import eric.demo.recognize.RecogUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;

import java.util.*;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
    public static boolean dumpImg = false;
//    private static CascadeClassifier zeroDetector = new CascadeClassifier("resources\\data\\cascade.xml");

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //get classifier
        KNearest kNearest = RecogUtils.getClassifier();

        while(true)
        {
            //get samples to recognize
            ImageUtils.screenCapture();
            List<Mat> digitsToRecog = digitSegmentation(ImageUtils.screenCaptureImage);
            if(digitsToRecog != null)
            {
                for (Mat mat : digitsToRecog)
                {
                    Mat toRecog = RecogUtils.getEigenVec(mat, null);
                    System.out.println((int) kNearest.findNearest(toRecog, 1, new Mat()));
                }
            }
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * if fails , return null
     * @param absolutePathOfPic
     * @return
     */

    private static List<Mat> digitSegmentation(String absolutePathOfPic)
    {
        System.out.println("processing : " + absolutePathOfPic);
        Mat src = Imgcodecs.imread(absolutePathOfPic);

//        Mat roi = src.submat(new Rect(740,368,120,35));
        Mat roi = src.submat(new Rect(1090, 450, 120, 34));
        Mat binary = ImageUtils.getBinaryMat(roi);
        Mat eroded = ImageUtils.removeNoise(binary);

        if(dumpImg)
        {
            Imgcodecs.imwrite("resources\\roi.png",roi);
            Imgcodecs.imwrite("resources\\binary.png",binary);
            Imgcodecs.imwrite("resources\\eroded.png",eroded);
        }
        Mat eroded_bak = new Mat(eroded.rows(),eroded.cols(), CvType.CV_8UC3);
        eroded.convertTo(eroded_bak,CvType.CV_8UC3);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(eroded, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        List<Mat> ret = ImageUtils.getSortedRectsOfDigits(contours, eroded_bak);
        if(ret.size()==0)
        {
            return null;
        }
        for(Mat mat:ret)
        {
            String pathToSave = absolutePathOfPic.substring(0,absolutePathOfPic.lastIndexOf("."))+ret.indexOf(mat) +".png";
            if(dumpImg)
            {
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return ret;
    }

}
