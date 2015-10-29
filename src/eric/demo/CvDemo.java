package eric.demo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.*;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
    private static String pic = "paimai";
    private static String resource_path = "resources\\paimai";
    private static int cnt = 1;

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for(int i = 21 ; i <=21 ; ++i)
        {
            cnt = i;
            digitSegmentation(i);
        }

    }

    private static void digitSegmentation(int index)
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

        List<Mat> sortedDigits = getSortedRectsOfDigits(contours,eroded_bak);
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
