package eric.demo.image;

import com.sun.javafx.collections.transformation.SortedList;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by 麟 on 2015/10/30.
 */
public class ImageUtils
{
    private static int[] gammaTable;
    public static String screenCaptureImage = "screenCapture.png";
    public static String imageFormat = "png";
    private static boolean dumpImg = true;
    private static String dumpDir = "dump\\";
    private static String dumpPicName = ".png";

    static
    {
        gammaTable = new int[256];
        for (int i = 0; i < 256; ++i)
        {
            gammaTable[i] = (int) (Math.pow(i / 255.0, 1.5) * 255.0);
        }
    }


    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        File dir = new File("CodeImage");
//        String[] files = dir.list();
//        for(String file : files)
//        {
//            dumpPicName=file;
//            digitSegmentation("CodeImage\\" + file);
//        }
        digitSegmentation("CodeImage\\3436.jpg");
    }

    public static Mat gammaCorrection(Mat src)
    {
        Mat ret = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        for (int i = 0; i < src.rows(); ++i)
        {
            for (int j = 0; j < src.cols(); ++j)
            {
                ret.put(i, j, gammaTable[((int) src.get(i, j)[0])]);
            }
        }
        return ret;
    }

    public static Mat equalization(Mat src)
    {
        int type = CvType.CV_8UC1;

        List<Mat> preEqualization = new ArrayList<Mat>();
        preEqualization.add(new Mat(src.rows(), src.cols(), type));
        preEqualization.add(new Mat(src.rows(), src.cols(), type));
        preEqualization.add(new Mat(src.rows(), src.cols(), type));
        Core.split(src, preEqualization);

        List<Mat> postEqualization = new ArrayList<Mat>();
        postEqualization.add(new Mat(src.rows(), src.cols(), type));
        postEqualization.add(new Mat(src.rows(), src.cols(), type));
        postEqualization.add(new Mat(src.rows(), src.cols(), type));

        for (int i = 0; i < 3; ++i)
        {
            Imgproc.equalizeHist(preEqualization.get(i), postEqualization.get(i));
        }
        Mat src_equalized = new Mat();
        Core.merge(postEqualization, src_equalized);
        return src_equalized;
    }

    public static Mat getBinaryMat(Mat src)
    {

//        int type = CvType.CV_8UC1;
//        List<Mat> preEqualization = new ArrayList<Mat>();
//        preEqualization.add(new Mat(src.rows(), src.cols(), type));
//        preEqualization.add(new Mat(src.rows(), src.cols(), type));
//        preEqualization.add(new Mat(src.rows(), src.cols(), type));
//
//        List<Mat> postEqualization = new ArrayList<Mat>();
//        postEqualization.add(new Mat(src.rows(), src.cols(), type));
//        postEqualization.add(new Mat(src.rows(), src.cols(), type));
//        postEqualization.add(new Mat(src.rows(), src.cols(), type));
//
//        Mat src_equalized = new Mat();
//
//        Core.split(src, preEqualization);
//        for (int i = 0; i < 3; ++i)
//        {
//            Imgproc.equalizeHist(preEqualization.get(i), postEqualization.get(i));
//        }
//        Core.merge(postEqualization, src_equalized);
        Mat src_equalized = equalization(src);
        Imgcodecs.imwrite(dumpDir + "src_equalized_" + dumpPicName, src_equalized);

        Mat gray = new Mat();
        Imgproc.cvtColor(src_equalized, gray, Imgproc.COLOR_RGB2GRAY);
        Imgcodecs.imwrite(dumpDir + "gray_" + dumpPicName, gray);

        Mat gray_equalized = new Mat();
        Imgproc.equalizeHist(gray, gray_equalized);
        Imgcodecs.imwrite(dumpDir + "gray_equalized_" + dumpPicName, gray_equalized);

        Mat gamma = gammaCorrection(gray_equalized);
        Imgcodecs.imwrite(dumpDir + "gamma_" + dumpPicName, gamma);

        Mat binary = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);

        Imgproc.threshold(gray, binary, 90, 255, Imgproc.THRESH_BINARY_INV);
        return binary;
    }

//    private static Mat getRedMat(Mat src)
//    {
//        int type = 1;
//        List<Mat> mats = new ArrayList<Mat>();
//        mats.add(new Mat(src.rows(),src.cols(),type));
//        mats.add(new Mat(src.rows(),src.cols(),type));
//        mats.add(new Mat(src.rows(),src.cols(),type));
//        // splite into 3 channels
//        Core.split(src, mats);
////        for(int i = 0; i < 3; ++i){
////            Imgcodecs.imwrite("resources\\channel" + i + ".png", mats.get(i));
////        }
//
////        Mat red_blue = new Mat(src.rows(),src.cols(),type);
////        Core.absdiff(mats.get(2),mats.get(0),red_blue);
////        Imgcodecs.imwrite("resources\\red-blue.png",red_blue);
//        Mat red_minus_green = new Mat(src.rows(),src.cols(),type);
//        Core.absdiff(mats.get(2), mats.get(1), red_minus_green);
//        if(dumpImg)
//        {
//            Imgcodecs.imwrite("red-green.png", red_minus_green);
//        }
//        return red_minus_green;
//    }

    public static Mat removeNoise(Mat src)
    {
        Mat ret = new Mat();
//        Imgproc.GaussianBlur(src, ret, new Size(3, 3), 0, 0);
        Imgproc.medianBlur(src, ret, 3);
//        Imgproc.blur(src,ret,new Size(3,3));
        return ret;
    }

    public static Mat erosion(Mat src, double size)
    {
        Mat ret = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(size, size));
        Imgproc.erode(src, ret, element);
        return ret;
    }

    public static Mat dilation(Mat src, double size)
    {
        Mat ret = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(size, size));
        Imgproc.dilate(src, ret, element);
        return ret;
    }


    /**
     * @param contours
     * @param src
     * @return mats of contoured-images in src Mat
     */
    public static List<Mat> getSortedRectsOfDigits(List<MatOfPoint> contours, Mat src)
    {
        //make sure there are 4 contours
        if (contours.size() < 4)
        {
            System.out.println("recognize failed");
            return new ArrayList<Mat>();
        }
        else
        {
            if (contours.size() > 4)
            {
                Collections.sort(contours, new Comparator<MatOfPoint>()
                {
                    @Override
                    public int compare(MatOfPoint o1, MatOfPoint o2)
                    {
                        if (o1.rows() + o1.cols() - o2.rows() - o2.cols() > 0)
                        {
                            return 1;
                        }
                        return -1;
                    }
                });
                while (contours.size() > 4)
                {
                    contours.remove(0);
                }
            }
        }

        List<Rect> boundRects = new ArrayList<Rect>();
        List<Mat> ret = new ArrayList<Mat>();
        for (int i = 0; i < contours.size(); ++i)
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
                if (o1.tl().x > o2.tl().x)
                {
                    return 1;
                }
                return -1;
            }
        });

        for (Rect rect : boundRects)
        {
            Mat cur = src.submat(rect);
            Mat resized = new Mat();
            Imgproc.resize(cur, resized, new Size(20, 20));
            ret.add(resized);
        }
        return ret;
    }

    public static void screenCapture()
    {
        try
        {
            int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
            BufferedImage screen = new Robot().createScreenCapture(new Rectangle(0, 0, width, height));
            ImageIO.write(screen, imageFormat, new File(screenCaptureImage));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Mat reduceColor(Mat mat)
    {
        Mat ret = new Mat(mat.size(), CvType.CV_8UC3);
        for (int i = 0; i < ret.rows(); ++i)
        {
            for (int j = 0; j < ret.cols(); ++j)
            {
                int r = reduceVal(mat.get(i, j)[0]);
                int g = reduceVal(mat.get(i, j)[1]);
                int b = reduceVal(mat.get(i, j)[2]);
                ret.put(i, j, r, g, b);
            }
        }
        return ret;
    }

    private static int reduceVal(double val)
    {
        int step = 128;
        return (int)val/step*step + step/2;
    }


    /**
     * if fails , return null
     *
     * @param absolutePathOfPic
     * @param picRect
     * @return
     */
    public static List<Mat> digitSegmentationWithROI(String absolutePathOfPic, Rect picRect)
    {
        System.out.println("processing ... ");
        Mat src = Imgcodecs.imread(absolutePathOfPic);

        // get target mat
        Mat roi = src.submat(picRect);

        Mat preprocessed = preProcess(roi);

        Mat mat_bak = new Mat(preprocessed.rows(), preprocessed.cols(), CvType.CV_8UC3);
        preprocessed.copyTo(mat_bak);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(preprocessed, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        List<Mat> ret = ImageUtils.getSortedRectsOfDigits(contours, mat_bak);
        if (ret.size() == 0)
        {
            return null;
        }
        if (dumpImg)
        {
            for (Mat mat : ret)
            {
                String pathToSave = ret.indexOf(mat) + ".png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return ret;
    }

    private static Mat preProcess(Mat roi)
    {
        Mat mat_noiseMoved = removeNoise(roi);

        Mat mat_colorReduced = reduceColor(mat_noiseMoved);

        Mat mat_getTargetColor = getTargetColor(mat_colorReduced,1);

        Mat mat_binary = getBinaryMat(mat_getTargetColor);

        Mat preprocessed = dilation(mat_binary, 3);

        preprocessed = erosion(preprocessed, 3);

        if (dumpImg)
        {
            Imgcodecs.imwrite(dumpDir + "roi_" + dumpPicName, roi);
            Imgcodecs.imwrite(dumpDir + "noiseMoved_" + dumpPicName, mat_noiseMoved);
            Imgcodecs.imwrite(dumpDir + "getTargetColor_" + dumpPicName, mat_getTargetColor);
            Imgcodecs.imwrite(dumpDir + "colorReduced_" + dumpPicName, mat_colorReduced);
            Imgcodecs.imwrite(dumpDir + "binary_" + dumpPicName, mat_binary);
            Imgcodecs.imwrite(dumpDir + "fixed_" + dumpPicName, preprocessed);
        }
        return preprocessed;
    }

    public static Mat getTargetColor(Mat src, int level)
    {
        Mat ret = new Mat(src.size(),CvType.CV_8UC3,new Scalar(255,255,255));

        Map<Scalar,Integer> colorMap = new HashMap<Scalar, Integer>();
        for(int i = 0; i < src.rows(); ++i)
        {
            for(int j = 0; j < src.cols(); ++j)
            {
                Scalar cur = new Scalar(src.get(i,j));
                if(colorMap.get(cur)==null)
                {
                    colorMap.put(cur,1);
                }
                else
                {
                    int val = colorMap.get(cur);
                    ++val;
                    colorMap.put(cur,val);
                }
            }
        }

        List<Map.Entry<Scalar,Integer>> entryList = new ArrayList<Map.Entry<Scalar, Integer>>(colorMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<Scalar, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Scalar, Integer> o1, Map.Entry<Scalar, Integer> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        for(int i = 0; i < ret.rows(); ++i)
        {
            for (int j = 0; j < ret.cols(); ++j)
            {
                double[] curColor =src.get(i,j);
                Scalar temp = new Scalar(curColor);
                for(int k=0; k <level; ++k)
                {
                    if(temp.equals(entryList.get(entryList.size()-2-k).getKey()))
                    {
                        double[] color = new double[]{temp.val[0],temp.val[1],temp.val[2]};
                        ret.put(i,j,color);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * if fails , return null
     * @param absolutePathOfPic
     * @return
     */
    public static List<Mat> digitSegmentation(String absolutePathOfPic)
    {
        return digitSegmentationWithROI(absolutePathOfPic, new Rect(3, 3, 105, 26));
    }

}
