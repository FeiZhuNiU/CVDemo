package eric.demo;

import eric.demo.image.ImageUtils;
import eric.demo.recognize.RecogUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.objdetect.BaseCascadeClassifier;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
    private static String pic = "paimai";
    private static String resource_path = "resources\\paimai";
    private static int cnt = 1;
//    private static CascadeClassifier zeroDetector = new CascadeClassifier("resources\\data\\cascade.xml");

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Map.Entry<Mat,Mat> trainData = RecogUtils.loadSamplesToMat();
        KNearest kNearest = KNearest.create();
        kNearest.train(trainData.getKey(), Ml.ROW_SAMPLE,trainData.getValue());

        for(int i = 22 ; i <=22 ; ++i)
        {
            cnt = i;
            List<Mat> digitsToRecog = digitSegmentation(i);

            for(Mat mat : digitsToRecog)
            {
                int cols = mat.rows()*mat.cols();
                Mat toRecog = new Mat(1,cols,CvType.CV_32FC1);
                for(int j = 0 ; j < cols ; ++j )
                {
                    toRecog.put(0,j,mat.get(j/mat.cols(), j %mat.cols()));
                }

                System.out.println((int)kNearest.findNearest(toRecog, 1, new Mat()));
            }
        }

//        renameImageFiles("0");

    }

    /**
     * rename image name to image**.png
     * generate *.info for training
     *
     * fileName :  line  0 1 2 3 4 5 ... 9
     */

    private static void renameImageFiles(String fileName)
    {
        File file = new File("resources\\" + fileName);
        File[] images = file.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".png");
            }
        });

        for(int i = 0 ; i < images.length; ++i)
        {
            images[i].renameTo(new File(file.getAbsolutePath() + "\\image"+(i+1)+".png"));
        }
        try
        {
            FileWriter infoFile = new FileWriter(file.getAbsolutePath() + "\\" + fileName + ".info");
            for(int i = 0 ; i < images.length; ++i)
            {
                infoFile.write("image" + (i + 1) + ".png 1 0 0 20 20\n");
            }
            infoFile.close();
            FileWriter negdataFile = new FileWriter(file.getAbsolutePath() + "\\" + fileName + "negdata.txt");
            for(int i = 0 ; i < images.length; ++i)
            {
                negdataFile.write(fileName + "\\image" + (i + 1) + ".png\n");
            }
            negdataFile.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static List<Mat> digitSegmentation(int index)
    {
        String picPath = resource_path + File.separator + pic + index + ".png";
        System.out.println("processing : " + picPath);
        Mat src = Imgcodecs.imread(picPath);

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
            Imgcodecs.imwrite("resources\\"+ pic + cnt +"_"+ ret.indexOf(mat) +".png",mat);
        }
        return ret;
    }









}
