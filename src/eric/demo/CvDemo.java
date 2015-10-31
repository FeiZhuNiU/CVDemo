package eric.demo;

import eric.demo.image.ImageUtils;
import eric.demo.recognize.RecogUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
//    private static CascadeClassifier zeroDetector = new CascadeClassifier("resources\\data\\cascade.xml");

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Long startTime = System.currentTimeMillis();

        //get classifier
        KNearest kNearest = RecogUtils.getClassifier();

        Long midTime = System.currentTimeMillis();

        for(int i = 22 ; i <=22 ; ++i)
        {
            //get samples to recognize
            List<Mat> digitsToRecog = digitSegmentation("resources\\paimai\\paimai"+i+".png");

            for(Mat mat : digitsToRecog)
            {
                Mat toRecog = RecogUtils.getEigenVec(mat,null);
                System.out.println((int)kNearest.findNearest(toRecog, 1, new Mat()));
            }
        }
        Long endTime = System.currentTimeMillis();

        System.out.println("train time : " + (midTime - startTime)/1000.0);
        System.out.println("recog time : " + (endTime - midTime)/1000.0);
//        renameImageFiles("0");
    }



    private static List<Mat> digitSegmentation(String absolutePathOfPic)
    {
        //String absolutePathOfPic = resource_path + File.separator + pic + index + ".png";
        System.out.println("processing : " + absolutePathOfPic);
        Mat src = Imgcodecs.imread(absolutePathOfPic);

//        new Rect(1090,425,120,34)
        Mat roi = src.submat(new Rect(740,368,120,35));
        Imgcodecs.imwrite("resources\\roi.png",roi);

        Mat binary = ImageUtils.getBinaryMat(roi);
        Imgcodecs.imwrite("resources\\binary.png",binary);

        Mat eroded = ImageUtils.removeNoise(binary);
        Imgcodecs.imwrite("resources\\eroded.png",eroded);

        Mat eroded_bak = new Mat(eroded.rows(),eroded.cols(), CvType.CV_8UC3);
        eroded.convertTo(eroded_bak,CvType.CV_8UC3);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(eroded, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        List<Mat> ret = ImageUtils.getSortedRectsOfDigits(contours, eroded_bak);
        for(Mat mat:ret)
        {
            String pathToSave = absolutePathOfPic.substring(0,absolutePathOfPic.lastIndexOf("."))+ret.indexOf(mat) +".png";
            Imgcodecs.imwrite(pathToSave,mat);
        }
        return ret;
    }

}
