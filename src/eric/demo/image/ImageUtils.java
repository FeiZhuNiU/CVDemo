package eric.demo.image;

import org.opencv.core.*;
import org.opencv.core.Point;
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
        long startTime = System.currentTimeMillis();
        digitSegmentation("CodeImage\\7723.jpg");
        long endTime = System.currentTimeMillis();
        System.out.println("seg time: " + (endTime-startTime)/1000.0);
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

    /**
     * 1. convert rgb to gray
     * 2. binaryZation
     *
     * @param src
     * @return
     */
    public static Mat binaryZation(Mat src)
    {
//        Mat src_equalized = equalization(src);
//        Imgcodecs.imwrite(dumpDir + "src_equalized_" + dumpPicName, src_equalized);

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);
        if (dumpImg)
        {
            Imgcodecs.imwrite(dumpDir + "gray_" + dumpPicName, gray);
        }

//        Mat gray_equalized = new Mat();
//        Imgproc.equalizeHist(gray, gray_equalized);
//        Imgcodecs.imwrite(dumpDir + "gray_equalized_" + dumpPicName, gray_equalized);

//        Mat gamma = gammaCorrection(gray_equalized);
//        Imgcodecs.imwrite(dumpDir + "gamma_" + dumpPicName, gamma);

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

    public static Mat removeNoise(Mat src, int size)
    {
        Mat ret = new Mat();
//        Imgproc.GaussianBlur(src, ret, new Size(3, 3), 0, 0);
        Imgproc.medianBlur(src, ret, size);
//        Imgproc.blur(src,ret,new Size(3,3));
        return ret;
    }

    public static Mat erosion(Mat src, double size)
    {
        Mat ret = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(size, size));
        Imgproc.erode(src, ret, element);
        return ret;
    }

    public static Mat dilation(Mat src, double size)
    {
        Mat ret = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(size, size));
        Imgproc.dilate(src, ret, element);
        return ret;
    }


    /**
     * @param rects make sure the size is 4
     * @param src
     * @return mats of contoured-images in order
     */
    public static List<Mat> getDigitMatsByRects(List<Rect> rects, Mat src)
    {
        if (rects == null || rects.size() != 4)
        {
            System.out.println("digit rects are not correct!");
            return null;
        }
        Collections.sort(rects, new Comparator<Rect>()
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
        List<Mat> ret = new ArrayList<Mat>();
        for (Rect rect : rects)
        {
            Mat cur = src.submat(rect);
            Mat resized = new Mat();
            Imgproc.resize(cur, resized, new Size(20, 20));
            ret.add(resized);
        }
        return ret;
    }

    /**
     * save current screen to local file system
     */
    public static void screenCapture()
    {
        try
        {
            int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
            BufferedImage screen = new Robot().createScreenCapture(new Rectangle(0, 0, width, height));
            ImageIO.write(screen, imageFormat, new File(screenCaptureImage));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * color quantization
     *
     * @param mat
     * @return
     */
    public static Mat reduceColor(Mat mat, int step)
    {
        Mat ret = new Mat(mat.size(), CvType.CV_8UC3);
        for (int i = 0; i < ret.rows(); ++i)
        {
            for (int j = 0; j < ret.cols(); ++j)
            {
                int r = reduceVal(mat.get(i, j)[0], step);
                int g = reduceVal(mat.get(i, j)[1], step);
                int b = reduceVal(mat.get(i, j)[2], step);
                ret.put(i, j, r, g, b);
            }
        }
        return ret;
    }

    private static int reduceVal(double val, int step)
    {
        return (int) val / step * step + step / 2;
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
        System.out.println("process Segmentation... ");
        Mat src = Imgcodecs.imread(absolutePathOfPic);

        Mat roi = src.submat(picRect);

        Mat preprocessed = preProcess(roi);

//        Mat skeleton = getSkeleton(preprocessed);

        List<Mat> ret = doSegmentation(preprocessed);

        if (ret == null) return null;
        return ret;
    }

    /**
     * return 4 mat that contain 4 digit
     *
     * @param src a binary image that has been preprocessed
     * @return if fails return null
     */
    private static List<Mat> doSegmentation(Mat src)
    {
        List<Rect> digitRects = getDigitRects(src);
        List<Mat> ret = ImageUtils.getDigitMatsByRects(digitRects, src);

        if (ret.size() == 0)
        {
            System.out.println("do segmentation fails");
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

    /**
     * make sure not change src
     *
     * @param src
     * @return need not be sorted and size should be 4
     */
    private static List<Rect> getDigitRects(Mat src)
    {
        Mat src_bak = new Mat(/*src.rows(), src.cols(), CvType.CV_8UC3*/);
        src.copyTo(src_bak);
        //get contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        src_bak.copyTo(src);


        if (contours.size() > 4)
        {
            //TODO: not good solution (MatOfPoint is the mat of contour points)
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

        List<Rect> boundRects = new ArrayList<Rect>();
        for (int i = 0; i < contours.size(); ++i)
        {
            //get rect bound of contour
            Rect rect = Imgproc.boundingRect(contours.get(i));
            boundRects.add(rect);
//            Imgproc.drawContours(eroded_bak, contours, i, new Scalar(0, 0, 255));
//            Imgproc.rectangle(eroded_bak,rect.tl(),rect.br(),new Scalar(0, 0, 255));
        }

        checkAndSplitBounds(boundRects);
        return boundRects;

    }

    /**
     * split bounds if digits are connected
     *
     * @param boundRects
     */
    private static void checkAndSplitBounds(List<Rect> boundRects)
    {
        int rectCount = boundRects.size();
        if (rectCount >= 4 || rectCount == 0)
            return;
        Collections.sort(boundRects, new Comparator<Rect>()
        {
            @Override
            public int compare(Rect o1, Rect o2)
            {
                return ((Integer)o1.width).compareTo(o2.width);
            }
        });

        //根据Rect的数量 对粘连的数字进行分割
        List<Rect> rectsSplited;
        if (rectCount == 3)
        {
            rectsSplited= splitRect(boundRects.get(2),2);
            boundRects.remove(2);
            boundRects.addAll(rectsSplited);
        } else if (rectCount == 2)
        {
            //分两种情况 一种是22数字粘连 另一种是三个数字粘连
            if(boundRects.get(0).width > boundRects.get(1).width/2){
                List<Rect> rectsSplited1 = splitRect(boundRects.get(0),2);
                List<Rect> rectsSplited2 = splitRect(boundRects.get(1),2);
                boundRects.clear();
                boundRects.addAll(rectsSplited1);
                boundRects.addAll(rectsSplited2);
            }
            //三个数字粘连的情况
            else{
                rectsSplited= splitRect(boundRects.get(1),3);
                boundRects.remove(1);
                boundRects.addAll(rectsSplited);
            }
        } else if (rectCount == 1)
        {
            rectsSplited= splitRect(boundRects.get(0),4);
            boundRects.remove(0);
            boundRects.addAll(rectsSplited);
        }
    }

    /**
     *
     * @param rect rect to split
     * @param splitNum the num to be split
     * @return split rects
     */
    private static List<Rect> splitRect(Rect rect, int splitNum)
    {
        List<Rect> ret = new ArrayList<Rect>();
        if(splitNum<=1)
        {
            ret.add(rect);
        }else{
            Point tl = rect.tl();
            for(int i = 0 ; i < splitNum; ++i)
            {
                int x = (int) (tl.x+rect.width/splitNum*i);
                int y = (int) tl.y;
                int width = rect.width/splitNum;
                int height = rect.height;
                Rect cur = new Rect(x,y,width,height);
                ret.add(cur);
            }
        }
        return ret;
    }

    /**
     * TODO
     *
     * @param src
     * @return
     */
    @Deprecated
    private static Mat getSkeleton(Mat src)
    {
        Mat skeleton = Mat.zeros(src.rows(), src.cols(), CvType.CV_8UC1);

        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(2, 2));

        Mat img = src.clone();
        int loops = 10;
        while (--loops > 0)
        {
//            Imgproc.morphologyEx(src,skeleton,Imgproc.MORPH_ERODE,element);
//            Imgproc.morphologyEx(src,skeleton,Imgproc.MORPH_CLOSE,element);
            Mat eroded = erosion(img, 2);
            Imgcodecs.imwrite(dumpDir + "eroded.png", eroded);
            Mat temp = dilation(eroded, 2);
            Imgcodecs.imwrite(dumpDir + "temp.png", temp);
            Core.absdiff(src, temp, temp);
            Imgcodecs.imwrite(dumpDir + "temp_diff.png", temp);
            Core.bitwise_or(skeleton, temp, skeleton);
            Imgcodecs.imwrite(dumpDir + "skeleton.png", skeleton);
            img = eroded.clone();
        }

        return skeleton;

    }

    private static Mat preProcess(Mat roi)
    {
        Mat mat_noiseMoved = removeNoise(roi, 3);

        Mat mat_colorReduced = reduceColor(mat_noiseMoved, 128);

        Mat mat_colorReduced_noiseremoved = removeNoise(mat_colorReduced, 3);

        Mat mat_getTargetColor = getTargetColor(mat_colorReduced_noiseremoved, 2);

        Mat mat_binary = binaryZation(mat_getTargetColor);

        Mat mat_binary_noiseRemoved = removeNoise(mat_binary, 3);

        Mat mat_binary_noiseRemoved_removeNonDigit = removeNonDigitPart(mat_binary_noiseRemoved);

        Mat ret = mat_binary_noiseRemoved_removeNonDigit;
//        Mat ret = erosion(mat_binary_noiseRemoved_removeNonDigit, 3);
//        ret = dilation(ret, 3);

        if (dumpImg)
        {
            Imgcodecs.imwrite(dumpDir + "roi_" + dumpPicName, roi);
            Imgcodecs.imwrite(dumpDir + "noiseMoved_" + dumpPicName, mat_noiseMoved);
            Imgcodecs.imwrite(dumpDir + "getTargetColor_" + dumpPicName, mat_getTargetColor);
            Imgcodecs.imwrite(dumpDir + "binary_noiseRemoved_removeNonDigit_" + dumpPicName, mat_binary_noiseRemoved_removeNonDigit);
            Imgcodecs.imwrite(dumpDir + "binary_noiseRemoved_" + dumpPicName, mat_binary_noiseRemoved);
            Imgcodecs.imwrite(dumpDir + "colorReduced_" + dumpPicName, mat_colorReduced);
            Imgcodecs.imwrite(dumpDir + "binary_" + dumpPicName, mat_binary);
            Imgcodecs.imwrite(dumpDir + "fixed_" + dumpPicName, ret);
        }
        return ret;
    }

    private static Mat removeNonDigitPart(Mat src)
    {
        Mat src_bak = new Mat();
        src.copyTo(src_bak);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        for (int i = 0; i < contours.size(); ++i)
        {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width + rect.height < 16)
            {
                contours.remove(i--);
            }
        }
        Mat ret = new Mat(src.rows(), src.cols(), CvType.CV_8UC1, new Scalar(0));
        for (int i = 0; i < src.rows(); ++i)
        {
            for (int j = 0; j < src.cols(); ++j)
            {
                for (MatOfPoint contour : contours)
                {
                    Rect rect = Imgproc.boundingRect(contour);
                    if (new Point(j, i).inside(rect))
                    {
                        ret.put(i, j, src_bak.get(i, j));
                        break;
                    }
                }
            }
        }
        src_bak.copyTo(src);
        return ret;
    }

    /**
     * return a Mat that contains the colors that have most weight
     *
     * @param src
     * @param level the color count (except the background)
     * @return
     */
    public static Mat getTargetColor(Mat src, int level)
    {
        Mat ret = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));

        Map<Scalar, Integer> colorMap = new HashMap<Scalar, Integer>();
        for (int i = 0; i < src.rows(); ++i)
        {
            for (int j = 0; j < src.cols(); ++j)
            {
                Scalar cur = new Scalar(src.get(i, j));
                if (colorMap.get(cur) == null)
                {
                    colorMap.put(cur, 1);
                } else
                {
                    int val = colorMap.get(cur);
                    ++val;
                    colorMap.put(cur, val);
                }
            }
        }

        List<Map.Entry<Scalar, Integer>> entryList = new ArrayList<Map.Entry<Scalar, Integer>>(colorMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<Scalar, Integer>>()
        {
            @Override
            public int compare(Map.Entry<Scalar, Integer> o1, Map.Entry<Scalar, Integer> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        for (int i = 0; i < ret.rows(); ++i)
        {
            for (int j = 0; j < ret.cols(); ++j)
            {
                double[] curColor = src.get(i, j);
                Scalar temp = new Scalar(curColor);
                for (int k = 0; k < level; ++k)
                {
                    // -2 because -1 is the background
                    if (temp.equals(entryList.get(entryList.size() - 2 - k).getKey()))
                    {
                        double[] color = new double[]{temp.val[0], temp.val[1], temp.val[2]};
                        ret.put(i, j, color);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * if fails , return null
     *
     * @param absolutePathOfPic
     * @return
     */
    public static List<Mat> digitSegmentation(String absolutePathOfPic)
    {
        return digitSegmentationWithROI(absolutePathOfPic, new Rect(3, 3, 105, 26));
    }

}
