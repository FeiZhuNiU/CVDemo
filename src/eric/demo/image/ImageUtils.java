package eric.demo.image;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 麟 on 2015/10/30.
 */
public class ImageUtils
{

    public static Mat getRedPartOfPic(Mat src)
    {
        Mat ret = new Mat(src.rows(),src.cols(), src.type());
        if(src.elemSize()==3)
        {
            int totalBytes = (int) (src.total() * src.elemSize());
            int buffer[] = new int[totalBytes];
            src.get(0, 0, buffer);
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

    public static Mat getBinaryMat(Mat src)
    {
        Mat red_minus_green = getRedMat(src);

        Mat binary = new Mat(src.rows(),src.cols(), CvType.CV_8UC1);
        Imgproc.threshold(red_minus_green, binary, 100, 255, Imgproc.THRESH_BINARY);
        return binary;
    }

    private static Mat getRedMat(Mat src)
    {
        int type = 1;
        List<Mat> mats = new ArrayList<Mat>();
        mats.add(new Mat(src.rows(),src.cols(),type));
        mats.add(new Mat(src.rows(),src.cols(),type));
        mats.add(new Mat(src.rows(),src.cols(),type));
        // splite into 3 channels
        Core.split(src, mats);
//        for(int i = 0; i < 3; ++i){
//            Imgcodecs.imwrite("resources\\channel" + i + ".png", mats.get(i));
//        }

//        Mat red_blue = new Mat(src.rows(),src.cols(),type);
//        Core.absdiff(mats.get(2),mats.get(0),red_blue);
//        Imgcodecs.imwrite("resources\\red-blue.png",red_blue);
        Mat red_minus_green = new Mat(src.rows(),src.cols(),type);
        Core.absdiff(mats.get(2),mats.get(1),red_minus_green);
        Imgcodecs.imwrite("resources\\red-green.png",red_minus_green);
        return red_minus_green;
    }

    public static Mat removeNoise(Mat src)
    {
        Mat eroded = new Mat(src.rows(),src.cols(),1);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(2, 2));
        Imgproc.erode(src, eroded, element);
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

    /**
     * @param contours
     * @param src
     * @return mats of contoured-images in src Mat
     */
    public static List<Mat> getSortedRectsOfDigits(List<MatOfPoint> contours,Mat src)
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
                        if (o1.rows() + o1.cols() - o2.rows() - o2.cols() > 0)
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
        }
        return ret;
    }
}
