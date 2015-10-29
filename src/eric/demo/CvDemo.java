package eric.demo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
        for(int i = 5 ; i <=5 ; ++i)
        {
            cnt = i;
            List<Mat> digitsToRecog = digitSegmentation(i);

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
            images[i].renameTo(new File(file.getAbsolutePath() + "\\temp"+(i+1)+".png"));
        }

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

        Mat roi = getROI(src);
        Imgcodecs.imwrite("resources\\roi.png",roi);

        Mat binary = getBinaryMat(roi);
        Imgcodecs.imwrite("resources\\binary.png",binary);

        Mat eroded = removeNoise(binary);
        Imgcodecs.imwrite("resources\\eroded.png",eroded);

        Mat eroded_bak = new Mat(eroded.rows(),eroded.cols(), CvType.CV_8UC3);
        eroded.convertTo(eroded_bak,CvType.CV_8UC3);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(eroded, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        List<Mat> ret = getSortedRectsOfDigits(contours,eroded_bak);
        return ret;
    }

    private static List<Mat> getSortedRectsOfDigits(List<MatOfPoint> contours,Mat src)
    {
        //make sure there are 4 contours
        if(contours.size()<4){
            System.out.println("failed");
            return new ArrayList<Mat>();
        }
        else
        {
            if (contours.size() > 4){
                Collections.sort(contours, new Comparator<MatOfPoint>()
                {
                    @Override
                    public int compare(MatOfPoint o1, MatOfPoint o2)
                    {
                        if(o1.rows()+o1.cols()-o2.rows()-o2.cols() > 0 )
                            return 1;
                        return -1;
                    }
                });
                while(contours.size()>4){
                    contours.remove(0);
                }
            }
        }

        List<Rect> boundRects = new ArrayList<Rect>();
        List<Mat> ret = new ArrayList<Mat>();
        for(int i = 0 ; i < contours.size(); ++i)
        {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            boundRects.add(rect);
//            Imgproc.drawContours(eroded_bak, contours, i, new Scalar(0, 0, 255));
//            Imgproc.rectangle(eroded_bak,rect.tl(),rect.br(),new Scalar(0, 0, 255));
        }
        Collections.sort(boundRects, new Comparator<Rect>()
        {
            @Override
            public int compare(Rect o1, Rect o2)
            {
                if(o1.tl().x > o2.tl().x)
                    return 1;
                return -1;
            }
        });

        for(Rect rect : boundRects)
        {
            Mat cur = src.submat(rect);
            Mat resized = new Mat();
            Imgproc.resize(cur,resized,new Size(20,20));
            ret.add(resized);
//            System.out.println(cur.dump());
            Imgcodecs.imwrite("resources\\"+ pic + cnt +"_"+ rect.tl().x +".png",resized);
        }
        return ret;
    }

    private static Mat removeNoise(Mat binary)
    {
        Mat eroded = new Mat(binary.rows(),binary.cols(),1);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(2, 2));
        Imgproc.erode(binary, eroded, element);
        Imgproc.dilate(eroded, eroded, element);

//        CascadeClassifier zeroDetector = new CascadeClassifier("resources\\data\\cascade.xml");
//        MatOfRect zeroDetections = new MatOfRect();
//        zeroDetector.detectMultiScale(eroded,zeroDetections);
//        Mat detectedMat = new Mat();
//        Imgproc.cvtColor(eroded,detectedMat,Imgproc.COLOR_GRAY2RGB);
//        for (Rect rect : zeroDetections.toArray()) {
//            Imgproc.rectangle(detectedMat, new Point(rect.x, rect.y), new Point(rect.x
//                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
//        }
//        Imgcodecs.imwrite("resources\\zero-detected.png",detectedMat);


        return eroded;
    }

    private static Mat getBinaryMat(Mat roi)
    {
        int type = 1;
        List<Mat> mats = new ArrayList<Mat>();
        mats.add(new Mat(roi.rows(),roi.cols(),type));
        mats.add(new Mat(roi.rows(),roi.cols(),type));
        mats.add(new Mat(roi.rows(),roi.cols(),type));
        // splite into 3 channels
        Core.split(roi, mats);
        for(int i = 0; i < 3; ++i){
            Imgcodecs.imwrite("resources\\channel" + i + ".png", mats.get(i));
        }

//        Mat red_blue = new Mat(roi.rows(),roi.cols(),type);
//        Core.absdiff(mats.get(2),mats.get(0),red_blue);
//        Imgcodecs.imwrite("resources\\red-blue.png",red_blue);
        Mat red_minus_green = new Mat(roi.rows(),roi.cols(),type);
        Core.absdiff(mats.get(2),mats.get(1),red_minus_green);
        Imgcodecs.imwrite("resources\\red-green.png",red_minus_green);


        Mat binary = new Mat(roi.rows(),roi.cols(),CvType.CV_8UC1);
        Imgproc.threshold(red_minus_green, binary, 100, 255, Imgproc.THRESH_BINARY);
        return binary;
    }

    private static Mat getRedRoi(Mat roi)
    {
        Mat ret = new Mat(roi.rows(),roi.cols(), roi.type());
        if(roi.elemSize()==3)
        {
            int totalBytes = (int) (roi.total() * roi.elemSize());
            int buffer[] = new int[totalBytes];
            roi.get(0, 0, buffer);
            for (int i = 0; i < totalBytes; ++i)
            {
                if (i % 3 != 2)
                {
                    buffer[i] = 255;
                }
            }
            ret.put(0,0,buffer);
        }
        return ret;
    }

    private static Mat getROI(Mat src)
    {
//        return new Mat(src,new Rect(1090,425,120,34));
        return new Mat(src,new Rect(740,368,120,35));
    }
}
