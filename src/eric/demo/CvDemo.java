package eric.demo;

import eric.demo.image.FileUtils;
import eric.demo.image.ImageUtils;
import eric.demo.recognize.RecogUtils;
import eric.demo.robot.RobotUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
    private static Rect picRect;
    public static boolean dumpImg = true;
//    private static CascadeClassifier zeroDetector = new CascadeClassifier("resources\\data\\cascade.xml");

    public static int main(String[] args)
    {
        if(initParams(args)==false)
        {
            System.out.println("please verify the input params");
            return -1;
        }
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
                ArrayList<Integer> numbers = new ArrayList<Integer>();
                for (Mat mat : digitsToRecog)
                {
                    Mat toRecog = RecogUtils.getEigenVec(mat, null);
                    int num = (int) kNearest.findNearest(toRecog, 1, new Mat());
                    numbers.add(num);
                    System.out.println(num);
                }
                return 0;
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static boolean initParams(String[] args)
    {
        int argCnt=args.length;
        if(argCnt!=4)
        {
            System.out.println("input args are not correct.\n" +
                    "There should be 4 args: \n" +
                    "x,y,width,height\n\n" +
                    " ___________________________________\n" +
                    "|\t\t\t\t^\t\t\t\t\t|\n" +
                    "|\t\t\t\t|\t\t\t\t\t|\n" +
                    "|  screen\t\ty\t\t\t\t\t|\n" +
                    "|\t\t\t\t|\t\t\t\t\t|\n" +
                    "|\t\t\t\tv\t\t\t\t\t|\n" +
                    "|<----x------->  __width____\t\t|\n" +
                    "|\t\t  ^\t\t|\t\t    |\t\t|\n" +
                    "|\t\theight\t| digits    |\t\t|\n" +
                    "|         v     |___________|\t\t|\n" +
                    "|\t\t\t\t\t\t\t\t\t|\n" +
                    "|___________________________________|");
            return false;
        }
        int x,y,width,height;
        try
        {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            width = Integer.parseInt(args[2]);
            height = Integer.parseInt(args[3]);
        }
        catch (Exception e)
        {
            return false;
        }
        picRect = new Rect(x,y,width,height);
        return true;
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
        //TODO: input position
//        Mat roi = src.submat(new Rect(1090, 450, 120, 34));
        Mat roi = src.submat(picRect);
        Mat binary = ImageUtils.getBinaryMat(roi);
        Mat eroded = ImageUtils.removeNoise(binary);

        if(dumpImg)
        {
            Imgcodecs.imwrite("roi.png",roi);
            Imgcodecs.imwrite("binary.png",binary);
            Imgcodecs.imwrite("eroded.png",eroded);
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
            String pathToSave = ret.indexOf(mat) +".png";
            if(dumpImg)
            {
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return ret;
    }

}
