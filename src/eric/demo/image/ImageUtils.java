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
    public static String dumpDir = "dump\\";
    public static String dumpPicName = ".png";
    public static final int NORMALIZATION_WIDTH = 20;
    public static final int NORMALIZATION_HEIGHT = 25;
    private static final int IMAGE_ENLARGE_SIZE = 10;
    public static boolean dumpUnNormalizedSamples = false;
    public static String sampleImageFormat="png";

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

        String imageFile = (args.length == 0 ? "CodeImage\\0687.jpg" : args[0]);
        dumpPicName = new File(imageFile).getName();

//        File dir = new File("CodeImage");
//        String[] files = dir.list();
//        for(String file : files)
//        {
//            dumpPicName=file;
//            digitSegmentation("CodeImage\\" + file);
//        }

        long startTime = System.currentTimeMillis();
        digitSegmentation(imageFile);
        long endTime = System.currentTimeMillis();
        System.out.println("seg time: " + (endTime - startTime) / 1000.0);

//        Mat tmp = Imgcodecs.imread("1.png");
//        Mat rotated = rotateMat(tmp,-30);
//        Imgcodecs.imwrite("rotated.png", rotated);
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
     * return digit images according to the given rects
     *
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
            ret.add(cur);
        }
        return ret;
    }

    /**
     * process digit mats
     * 1.  enlarge the frame
     * 2.  normalization
     *
     * @param srcs
     * @return
     */
    private static List<Mat> processDigitMats(List<Mat> srcs)
    {
        if (srcs == null || srcs.size() == 0)
        {
            return null;
        }
        List<Mat> ret = new ArrayList<Mat>();
        for (Mat src : srcs)
        {
            Mat enlarged = enlargeMat(src, IMAGE_ENLARGE_SIZE);
            Mat normalized = normalize(enlarged);
            ret.add(normalized);
        }
        return ret;
    }

    /**
     * enlarge mat by adding frame
     *
     * @param src
     * @param size
     * @return
     */
    private static Mat enlargeMat(Mat src, int size)
    {
        Mat enlarged = new Mat(src.rows() + size, src.cols() + size, src.type(),
                               new Scalar(0));
        for (int i = 0; i < src.rows(); ++i)
        {
            for (int j = 0; j < src.cols(); ++j)
            {
                enlarged.put(i + size / 2, j + size / 2, src.get(i, j));
            }
        }
        return enlarged;
    }

    /**
     * normalize
     *
     * @param src
     * @return binary image mat
     */
    public static Mat normalize(Mat src)
    {
        Mat resized = new Mat();
        Imgproc.resize(src, resized, new Size(NORMALIZATION_WIDTH, NORMALIZATION_HEIGHT));
        Mat ret = new Mat();
        Imgproc.threshold(resized, ret, 90, 255, Imgproc.THRESH_BINARY);
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
        }
        catch (Exception e)
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
        if (preprocessed == null)
        {
            System.out.println("preprocess failed");
            return null;
        }

        List<Mat> segments = doSegmentation(preprocessed);
        if (segments == null)
        {
            System.out.println("doSegmentation failed");
            return null;
        }

        List<Mat> skeletons = new ArrayList<Mat>();
        for (Mat mat : segments)
        {
            Mat skeleton = thin(mat, 10);
            skeletons.add(skeleton);
        }
        if (dumpImg)
        {
            for (Mat mat : skeletons)
            {
                String pathToSave = dumpPicName.substring(0, dumpPicName.indexOf(".")) + skeletons.indexOf(
                        mat) + "_skeleton.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return skeletons;
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
        if (digitRects == null || digitRects.size() == 0)
        {
            return null;
        }

        List<Mat> unNormalizedDigits = ImageUtils.getDigitMatsByRects(digitRects, src);

        if (unNormalizedDigits == null || unNormalizedDigits.size() == 0)
        {
            System.out.println("do segmentation fails");
            return null;
        }

        if (dumpUnNormalizedSamples)
        {
            for (Mat mat : unNormalizedDigits)
            {
                Mat enlarged = enlargeMat(mat, IMAGE_ENLARGE_SIZE);
                String pathToSave = dumpPicName.substring(0, dumpPicName.indexOf(".")) +
                        unNormalizedDigits.indexOf(mat) + "_unNormalized.png";
                Imgcodecs.imwrite(pathToSave, enlarged);
            }
        }

        List<Mat> normalized = processDigitMats(unNormalizedDigits);
        if (dumpImg)
        {
            for (Mat mat : normalized)
            {
                String pathToSave = dumpPicName.substring(0, dumpPicName.indexOf(".")) + normalized.indexOf(
                        mat) + "_processed.png";
                Imgcodecs.imwrite(pathToSave, mat);
            }
        }
        return normalized;
    }

    /**
     * return bound rects each contains one digits
     *
     * @param src make sure not change src
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
        {
            return;
        }
        Collections.sort(boundRects, new Comparator<Rect>()
        {
            @Override
            public int compare(Rect o1, Rect o2)
            {
                return ((Integer) o1.width).compareTo(o2.width);
            }
        });

        //根据Rect的数量 对粘连的数字进行分割
        List<Rect> rectsSplited;
        if (rectCount == 3)
        {
            rectsSplited = splitRect(boundRects.get(2), 2);
            boundRects.remove(2);
            boundRects.addAll(rectsSplited);
        }
        else if (rectCount == 2)
        {
            //分两种情况 一种是22数字粘连 另一种是三个数字粘连
            if (boundRects.get(0).width > boundRects.get(1).width / 2)
            {
                List<Rect> rectsSplited1 = splitRect(boundRects.get(0), 2);
                List<Rect> rectsSplited2 = splitRect(boundRects.get(1), 2);
                boundRects.clear();
                boundRects.addAll(rectsSplited1);
                boundRects.addAll(rectsSplited2);
            }
            //三个数字粘连的情况
            else
            {
                rectsSplited = splitRect(boundRects.get(1), 3);
                boundRects.remove(1);
                boundRects.addAll(rectsSplited);
            }
        }
        else if (rectCount == 1)
        {
            rectsSplited = splitRect(boundRects.get(0), 4);
            boundRects.remove(0);
            boundRects.addAll(rectsSplited);
        }
    }

    /**
     * @param rect     rect to split
     * @param splitNum the num to be split
     * @return split rects
     */
    private static List<Rect> splitRect(Rect rect, int splitNum)
    {
        List<Rect> ret = new ArrayList<Rect>();
        if (splitNum <= 1)
        {
            ret.add(rect);
        }
        else
        {
            Point tl = rect.tl();
            for (int i = 0; i < splitNum; ++i)
            {
                int x = (int) (tl.x + rect.width / splitNum * i);
                int y = (int) tl.y;
                int width = rect.width / splitNum;
                int height = rect.height;
                Rect cur = new Rect(x, y, width, height);
                ret.add(cur);
            }
        }
        return ret;
    }

    /**
     * skeleton algorithm
     *
     * @param src
     * @param loops
     * @return
     */
    private static Mat thin(Mat src, int loops)
    {
        Mat dst = new Mat();
        int height = src.rows() - 1;
        int width = src.cols() - 1;

        src.copyTo(dst);

        int n = 0, i = 0, j = 0;
        Mat tmpImg = new Mat();
        boolean isFinished = false;

        for (n = 0; n < loops; n++)
        {
            dst.copyTo(tmpImg);
            isFinished = false;   //一次 先行后列扫描 开始
            //扫描过程一 开始
            for (i = 1; i < height; i++)
            {
                for (j = 1; j < width; j++)
                {
                    if (tmpImg.get(i, j)[0] > 0)
                    {
                        int ap = 0;
                        int p2 = tmpImg.get(i - 1, j)[0] > 0 ? 1 : 0;
                        int p3 = tmpImg.get(i - 1, j + 1)[0] > 0 ? 1 : 0;
                        if (p2 == 0 && p3 == 1)
                        {
                            ap++;
                        }
                        int p4 = tmpImg.get(i, j + 1)[0] > 0 ? 1 : 0;
                        if (p3 == 0 && p4 == 1)
                        {
                            ap++;
                        }
                        int p5 = tmpImg.get(i + 1, j + 1)[0] > 0 ? 1 : 0;
                        if (p4 == 0 && p5 == 1)
                        {
                            ap++;
                        }
                        int p6 = tmpImg.get(i + 1, j)[0] > 0 ? 1 : 0;
                        if (p5 == 0 && p6 == 1)
                        {
                            ap++;
                        }
                        int p7 = tmpImg.get(i + 1, j - 1)[0] > 0 ? 1 : 0;
                        if (p6 == 0 && p7 == 1)
                        {
                            ap++;
                        }
                        int p8 = tmpImg.get(i, j - 1)[0] > 0 ? 1 : 0;
                        if (p7 == 0 && p8 == 1)
                        {
                            ap++;
                        }
                        int p9 = tmpImg.get(i - 1, j - 1)[0] > 0 ? 1 : 0;
                        if (p8 == 0 && p9 == 1)
                        {
                            ap++;
                        }
                        if (p9 == 0 && p2 == 1)
                        {
                            ap++;
                        }
                        if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) > 1 && (p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) < 7)
                        {
                            if (ap == 1)
                            {
                                if ((p2 * p4 * p6 == 0) && (p4 * p6 * p8 == 0))
                                {
                                    dst.put(i, j, 0);
                                    isFinished = true;
                                }
                            }
                        }
                    }
                } //扫描过程一 结束

                dst.copyTo(tmpImg);
                //扫描过程二 开始
                for (i = 1; i < height; i++)  //一次 先行后列扫描 开始
                {
                    for (j = 1; j < width; j++)
                    {
                        if (tmpImg.get(i, j)[0] > 0)
                        {
                            int ap = 0;
                            int p2 = tmpImg.get(i - 1, j)[0] > 0 ? 1 : 0;
                            int p3 = tmpImg.get(i - 1, j + 1)[0] > 0 ? 1 : 0;
                            if (p2 == 0 && p3 == 1)
                            {
                                ap++;
                            }
                            int p4 = tmpImg.get(i, j + 1)[0] > 0 ? 1 : 0;
                            if (p3 == 0 && p4 == 1)
                            {
                                ap++;
                            }
                            int p5 = tmpImg.get(i + 1, j + 1)[0] > 0 ? 1 : 0;
                            if (p4 == 0 && p5 == 1)
                            {
                                ap++;
                            }
                            int p6 = tmpImg.get(i + 1, j)[0] > 0 ? 1 : 0;
                            if (p5 == 0 && p6 == 1)
                            {
                                ap++;
                            }
                            int p7 = tmpImg.get(i + 1, j - 1)[0] > 0 ? 1 : 0;
                            if (p6 == 0 && p7 == 1)
                            {
                                ap++;
                            }
                            int p8 = tmpImg.get(i, j - 1)[0] > 0 ? 1 : 0;
                            if (p7 == 0 && p8 == 1)
                            {
                                ap++;
                            }
                            int p9 = tmpImg.get(i - 1, j - 1)[0] > 0 ? 1 : 0;
                            if (p8 == 0 && p9 == 1)
                            {
                                ap++;
                            }
                            if (p9 == 0 && p2 == 1)
                            {
                                ap++;
                            }
                            if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) > 1 && (p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) < 7)
                            {
                                if (ap == 1)
                                {
                                    if ((p2 * p4 * p8 == 0) && (p2 * p6 * p8 == 0))
                                    {
                                        dst.put(i, j, 0);
                                        isFinished = true;
                                    }
                                }
                            }
                        }
                    }

                } //一次 先行后列扫描完成
                //如果在扫描过程中没有删除点，则提前退出
                if (!isFinished)
                {
                    break;
                }
            }
        }
        return dst;
    }

    private static Mat preProcess(Mat roi)
    {
        Mat mat_noiseMoved = removeNoise(roi, 3);

        Mat mat_colorReduced = reduceColor(mat_noiseMoved, 128);

        Mat mat_colorReduced_noiseremoved = removeNoise(mat_colorReduced, 3);

        Mat mat_getTargetColor = getTargetColor(mat_colorReduced_noiseremoved, 2);
        if (mat_getTargetColor == null)
        {
            System.out.println("no digit");
            return null;
        }

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
            Imgcodecs.imwrite(dumpDir + "binary_noiseRemoved_removeNonDigit_" + dumpPicName,
                              mat_binary_noiseRemoved_removeNonDigit);
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

        //calculate how much each color has
        Map<Scalar, Integer> colorMap = new HashMap<Scalar, Integer>();
        for (int i = 0; i < src.rows(); ++i)
        {
            for (int j = 0; j < src.cols(); ++j)
            {
                Scalar cur = new Scalar(src.get(i, j));
                if (colorMap.get(cur) == null)
                {
                    colorMap.put(cur, 1);
                }
                else
                {
                    int val = colorMap.get(cur);
                    ++val;
                    colorMap.put(cur, val);
                }
            }
        }

        if (colorMap.size() < level + 1)
        {
            System.out.println("color quantity is not enough, maybe the picture is blank.");
            return null;
        }

        //sort color by quantity
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

    /**
     * rotate image with given angles
     *
     * @param src
     * @param angle
     * @return
     */
    public static Mat rotateMat(Mat src, double angle)
    {
        Mat ret = new Mat();

        Point point = new Point(src.cols() / 2.0, src.rows() / 2.0);
        Mat r = Imgproc.getRotationMatrix2D(point, angle, 1.0);
        Imgproc.warpAffine(src, ret, r, new Size(src.width(), src.height()));
        return ret;

    }

}
