package eric.demo.recognize;

import eric.demo.image.ImageUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;


import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by 麟 on 2015/10/30.
 */
public class RecogUtils
{

    public static Map.Entry<Mat,Mat> loadSamplesToMat()
    {

        List<Map.Entry<Integer,Mat>> samples = new ArrayList<Map.Entry<Integer, Mat>>();

        for(int i = 0 ; i < 10 ; ++i)
        {
//            String path = Thread.currentThread().getContextClassLoader().getResource("/resources/0/image1.png").getFile();
//            File file = new File(path);
            File file = new File("resources\\"+ i);
            String[] pics = file.list(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("."+ ImageUtils.imageFormat);
                }
            });

            for(String pic : pics)
            {
                Mat cur = Imgcodecs.imread("resources\\"+i+"\\" + pic);
                samples.add(new AbstractMap.SimpleEntry<Integer, Mat>(i,cur));
            }
        }

        Mat trainData = new Mat(samples.size(),samples.get(0).getValue().rows()*samples.get(0).getValue().cols(), CvType.CV_32FC1);
        Mat trainClasses = new Mat(samples.size(),1,CvType.CV_32FC1);
        Map.Entry<Mat,Mat> ret = new AbstractMap.SimpleEntry<Mat, Mat>(trainData,trainClasses);
        for(int i = 0 ; i < samples.size(); ++i)
        {
            int curVal = samples.get(i).getKey();
            Mat curMat = samples.get(i).getValue();
            trainClasses.put(i,0,curVal);
            for(int j = 0 ; j < trainData.cols(); ++j)
            {
                trainData.put(i,j,curMat.get(j/curMat.rows(),j%curMat.rows()));
            }
        }
        return ret;
    }
    public static KNearest getClassifier()
    {
        KNearest kNearest = KNearest.create();
        Map.Entry<Mat,Mat> trainData = RecogUtils.loadSamplesToMat();
        kNearest.train(trainData.getKey(), Ml.ROW_SAMPLE,trainData.getValue());
        return kNearest;
    }

    public static Mat getEigenVec(Mat mat,EigenStrategy strategy)
    {
        if(strategy == null)
        {
            int cols = mat.rows()*mat.cols();
            Mat ret = new Mat(1,cols,CvType.CV_32FC1);
            for(int j = 0 ; j < cols ; ++j )
            {
                ret.put(0,j,mat.get(j/mat.cols(), j %mat.cols()));
            }
            return ret;
        }
        else
        {
            System.out.println("EigenStrategy not yet support");
            return null;
        }
    }
}