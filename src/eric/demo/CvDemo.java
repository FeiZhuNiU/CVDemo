package eric.demo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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

    }
}
