package com.iknow.image;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 10/30/2015  (yulin.jay@gmail.com)           |
 +===========================================================================*/

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

public class ImageUtils
{
    private static int[] gammaTable;

    public enum Color
    {
        RED, GREEN, BLUE
    }

    static
    {
        gammaTable = new int[256];
        for (int i = 0; i < 256; ++i)
        {
            gammaTable[i] = (int) (Math.pow(i / 255.0, 1.5) * 255.0);
        }
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

    /**
     * histogram equalization for color image
     *
     * @param src color image
     * @return histogram equalized color image
     */
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
     * 2. color2BinaryInverse
     *
     * @param src
     * @return
     */
    public static Mat color2BinaryInverse(Mat src, int threshold)
    {
        Mat gray = color2Gray(src);
        return gray2BinaryReverse(gray, threshold);
    }

    public static Mat color2Binary(Mat src)
    {
        Mat gray = color2Gray(src);
        return gray2Binary(gray);
    }
    public static Mat color2BinaryInverse(Mat src)
    {
        Mat gray = color2Gray(src);
        return gray2BinaryReverse(gray);
    }

    public static Mat color2Gray(Mat src)
    {
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);
        return gray;
    }

    public static Mat gray2BinaryReverse(Mat gray, int threshold)
    {
        Mat binary = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);

        Imgproc.threshold(gray, binary, threshold, 255, Imgproc.THRESH_BINARY_INV);
        return binary;
    }

    public static Mat gray2Binary(Mat gray)
    {
        Mat binary = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);
        Imgproc.threshold(gray, binary, 255, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        return binary;
    }
    public static Mat gray2BinaryReverse(Mat gray)
    {
        Mat binary = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);
        Imgproc.threshold(gray, binary, 255, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
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
     * @param rects
     * @param src
     * @return mats of contoured-images IN ORDER (unNormalized)
     */
    public static List<Mat> getOrderedMatsByRects(List<Rect> rects, Mat src)
    {
        if (rects == null || rects.size() == 0)
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
        List<Mat> ret = new ArrayList<>();
        for (Rect rect : rects)
        {
            Mat cur = src.submat(rect);
            ret.add(cur);
        }
        return ret;
    }

    /**
     * enlarge mat by adding frame
     *
     * @param src
     * @param size_width  (%2==0)
     * @param size_height (%2==0)
     * @return
     */
    public static Mat enlargeMat(Mat src, int size_width, int size_height)
    {
        Mat enlarged = new Mat(src.rows() + size_height, src.cols() + size_width, src.type(),
                new Scalar(0));
        for (int i = 0; i < src.rows(); ++i)
        {
            for (int j = 0; j < src.cols(); ++j)
            {
                enlarged.put(i + size_height / 2, j + size_width / 2, src.get(i, j));
            }
        }
        return enlarged;
    }

    public static Mat smallenMat(Mat src, int size_width, int size_height)
    {
        Mat smallen = new Mat(src.rows() - size_height, src.cols() - size_width, src.type(),
                new Scalar(0));
        for (int i = 0; i < smallen.rows(); ++i)
        {
            for (int j = 0; j < smallen.cols(); ++j)
            {
                smallen.put(i, j, src.get(i+size_height/2, j+size_width/2));
            }
        }
        return smallen;
    }

    public static Mat resize(Mat src, int width, int height)
    {
        Mat ret = new Mat();
        Imgproc.resize(src, ret, new Size(width, height));
        return ret;
    }

    /**
     * save current screen to local file system
     */
    public static Mat screenCapture(int lt_x, int lt_y, int width, int height)
    {
        String dst = "screenCapture_tmp.bmp";
        try
        {
            BufferedImage screen = new Robot().createScreenCapture(new Rectangle(lt_x, lt_y, width, height));
            String format = dst.lastIndexOf(".") > 0 ? dst.substring(dst.lastIndexOf(".") + 1, dst.length()) : "bmp";
            ImageIO.write(screen, format, new File(dst));
            Mat ret = readImage(dst);
            deleteImage(dst);
            return ret;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Mat readImage(String dst)
    {
        return Imgcodecs.imread(dst);
    }

    public static void writeImage(Mat mat, String dst)
    {
        Imgcodecs.imwrite(dst, mat);
    }

    public static void screenCapture(String dst, int lt_x, int lt_y, int width, int height)
    {
        try
        {
            BufferedImage screen = new Robot().createScreenCapture(new Rectangle(lt_x, lt_y, width, height));
            String format = dst.lastIndexOf(".") > 0 ? dst.substring(dst.lastIndexOf(".") + 1, dst.length()) : "bmp";
            ImageIO.write(screen, format, new File(dst));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void screenCapture(String dst)
    {
        int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        screenCapture(dst, 0, 0, width, height);
    }


    public static Mat screenCapture()
    {
        int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        return screenCapture(0, 0, width, height);
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
     * @param rect     rect to split
     * @param splitNum the num to be split
     * @return split rects
     */
    public static List<Rect> splitRect(Rect rect, int splitNum)
    {
        List<Rect> ret = new ArrayList<Rect>();
        if (splitNum <= 1)
        {
            ret.add(rect);
        } else
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
    public static Mat thin(Mat src, int loops)
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

    /**
     * remove info in rect from src (set to 0 (black))
     * @param src
     * @param rect
     */
    public static void removeRect(Mat src, Rect rect)
    {
        Point tl = rect.tl();
        Point br = rect.br();
        {
            for (int i = (int) tl.x; i <= (int) br.x; ++i)
            {
                for (int j = (int) tl.y; j <= (int) br.y; ++j)
                {
                    src.put(j,i,0);
                }
            }
        }
    }

    /**
     * strategy: remove the pixels' info (where width+height < threshold) from mat
     * if there is only 1 part in image. Return src.clone()
     *
     * @param src
     * @return
     */
    public static Mat removeSmallPart(Mat src, int threshold)
    {
        Mat ret = src.clone();
        List<MatOfPoint> contours = findContours(src);
        //if there is only 1 part in image. Just return src.clone()
        if (contours.size() <= 1)
        {
            return ret;
        }

        List<Rect> contourRects_remove = new ArrayList<>();
        for (MatOfPoint contour : contours)
        {
            contourRects_remove.add(Imgproc.boundingRect(contour));
        }

        for (int i = 0; i < contourRects_remove.size(); ++i)
        {
            Rect rect = contourRects_remove.get(i);
            if (rect.width + rect.height > threshold)
            {
                contourRects_remove.remove(i--);
            }
        }

//        Mat ret = new Mat(src.rows(), src.cols(), CvType.CV_8UC1, new Scalar(0));
        for (Rect rect : contourRects_remove)
        {
            removeRect(ret,rect);
        }

        return ret;
    }

    public static void keepLargestNRect(Mat src, int n)
    {
        List<MatOfPoint> contours = findContours(src);
        List<Rect> rects = new ArrayList<>();
        for(MatOfPoint contour : contours)
        {
            rects.add(getContourRect(contour));
        }
        Collections.sort(rects, new Comparator<Rect>() {
            @Override
            public int compare(Rect o1, Rect o2) {
                return o1.height + o1.width - o2.height - o2.width;
            }
        });
        for(int i = 0 ; i < rects.size()-n; ++i)
        {
            removeRect(src,rects.get(i));
        }
    }

    public static void filterContours(List<MatOfPoint> contours, ContourFilter filter)
    {
        for(int i = 0 ; i < contours.size(); ++i)
        {
            if(!filter.accept(contours.get(i)))
            {
                contours.remove(i--);
            }
        }
    }

    public interface ContourFilter
    {
        boolean accept(MatOfPoint contour);
    }

    public static Rect getContourRect(MatOfPoint contour)
    {
        return Imgproc.boundingRect(contour);
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
                } else
                {
                    int val = colorMap.get(cur);
                    ++val;
                    colorMap.put(cur, val);
                }
            }
        }

        if (colorMap.size() < level + 1)
        {
            ret = src.clone();
            return ret;
//            return null;
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
        Imgproc.warpAffine(src, ret, r, new Size(src.height()+3, src.height()+3));
        ret = color2Binary(ret);
        return ret;
    }

    public static Mat transition(Mat src, int x, int y)
    {
        Mat ret = new Mat(src.size(), src.type(), new Scalar(0));
        for (int i = 0; i < ret.rows(); ++i)
        {
            for (int j = 0; j < ret.cols(); ++j)
            {
                if (i + x >= 0 && i + x < src.rows() && j + y >= 0 && j + y < src.cols())
                {
                    ret.put(i, j, src.get(i + x, j + y));
                }
            }
        }
        return ret;
    }

    /**
     * cut a image to its border
     *
     * @param src
     * @return
     */
    public static Mat cutImage(Mat src)
    {
        Mat ret;
        int rows = src.rows();
        int cols = src.cols();
        int top = 0, bottom = rows - 1, left = 0, right = cols - 1;
        boolean hasFound = false;
        for (int i = 0; i < rows; ++i)
        {
            for (int j = 0; j < cols; ++j)
            {
                if ((int) src.get(i, j)[0] != 0)
                {
                    top = i;
                    hasFound = true;
                    break;
                }
            }
            if (hasFound)
            {
                hasFound = false;
                break;
            }
        }

        for (int j = 0; j < cols; ++j)
        {
            for (int i = 0; i < rows; ++i)
            {
                if ((int) src.get(i, j)[0] != 0)
                {
                    left = j;
                    hasFound = true;
                    break;
                }
            }
            if (hasFound)
            {
                hasFound = false;
                break;
            }
        }

        for (int i = rows - 1; i >= 0; --i)
        {
            for (int j = 0; j < cols; ++j)
            {
                if ((int) src.get(i, j)[0] != 0)
                {
                    bottom = i;
                    hasFound = true;
                    break;
                }
            }
            if (hasFound)
            {
                hasFound = false;
                break;
            }
        }

        for (int j = cols - 1; j >= 0; --j)
        {
            for (int i = 0; i < rows; ++i)
            {
                if ((int) src.get(i, j)[0] != 0)
                {
                    right = j;
                    hasFound = true;
                    break;
                }
            }
            if (hasFound)
            {
                break;
            }
        }
        ret = src.submat(top, bottom + 1, left, right + 1);
        return ret;
    }

    /**
     * this method would not change src image
     *
     * @param src the type should be CvType.CV_8UC1
     * @return
     */
    public static List<MatOfPoint> findContours(Mat src)
    {
        if (src == null)
            return null;
        Mat src_bak = new Mat();
        src.copyTo(src_bak);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        src_bak.copyTo(src);
        return contours;
    }

    public static void deleteImage(String image)
    {
        File file = new File(image);
        if (file.exists())
        {
            file.delete();
        }
    }


    public static Mat getRedChannel(Mat src)
    {
        return getChannel(src, 2);
    }

    public static Mat getGreenChannel(Mat src)
    {
        return getChannel(src, 1);
    }

    public static Mat getBlueChannel(Mat src)
    {
        return getChannel(src, 0);
    }

    private static Mat getChannel(Mat src, int i)
    {
        int type = CvType.CV_8UC1;
        List<Mat> channels = new ArrayList<>();
        channels.add(new Mat(src.rows(), src.cols(), type));
        channels.add(new Mat(src.rows(), src.cols(), type));
        channels.add(new Mat(src.rows(), src.cols(), type));
        Core.split(src, channels);
        return channels.get(i);
    }

    public static Mat getRegionOfTargetColor(Mat src, Color color, double ratio)
    {
        Mat ret = src.clone();
        int indexTargetColor;
        int indexOtherColor1;
        int indexOtherColor2;
        if (color == Color.RED)
        {
            indexTargetColor = 2;
            indexOtherColor1 = 0;
            indexOtherColor2 = 1;
        } else if (color == Color.GREEN)
        {
            indexTargetColor = 1;
            indexOtherColor1 = 0;
            indexOtherColor2 = 2;
        } else
        {
            indexTargetColor = 0;
            indexOtherColor1 = 1;
            indexOtherColor2 = 2;
        }
        for (int i = 0; i < ret.height(); ++i)
        {
            for (int j = 0; j < ret.width(); ++j)
            {
                int targetColor = (int) ret.get(i, j)[indexTargetColor];
                int otherColor1 = (int) ret.get(i, j)[indexOtherColor1];
                int otherColor2 = (int) ret.get(i, j)[indexOtherColor2];
                if (targetColor > ratio * otherColor2 && targetColor > ratio * otherColor1)
                {
                    //keep this color
                } else
                {
                    // set white
                    ret.put(i, j, 255, 255, 255);
                }
            }
        }
        return ret;
    }
}
