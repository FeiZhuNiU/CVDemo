package eric.demo;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

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
        Imgcodecs.imwrite("resources\\roi.png",roi);

        Mat binary = getBinaryMat(roi);
        Imgcodecs.imwrite("resources\\binary.png",binary);

        Mat eroded = removeNoise(binary);
        Imgcodecs.imwrite("resources\\eroded.png",eroded);



    }

    private static Mat removeNoise(Mat binary)
    {
        Mat eroded = new Mat(binary.rows(),binary.cols(),1);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(2, 2));
        Imgproc.dilate(binary, eroded, element);
        Imgproc.erode(eroded, eroded, element);
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


        Mat binary = new Mat(roi.rows(),roi.cols(),type);
        Imgproc.threshold(red_green, binary, 100, 255, 1);
        System.out.println(binary);

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
