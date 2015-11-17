package eric.demo.recognize;

import eric.demo.image.ImageUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.ANN_MLP;
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
    public enum Classifier
    {
        KNN, ANN
    }

    public static String sampleDir = "resources\\samples";
    public static Map.Entry<Mat, Mat> trainData;

    static
    {
        trainData = loadSamplesToMat();
    }

    /**
     * @return <trainData , trainClasses>
     */
    public static Map.Entry<Mat, Mat> loadSamplesToMat()
    {

        List<Map.Entry<Integer, Mat>> samples = new ArrayList<Map.Entry<Integer, Mat>>();


        File file = new File(sampleDir);
        File[] pics = file.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith("." + ImageUtils.sampleImageFormat);
            }
        });
        //make sure the name of picFiles start with the number it means
        for (File pic : pics)
        {
            Mat cur = Imgcodecs.imread(pic.getAbsolutePath());
            samples.add(
                    new AbstractMap.SimpleEntry<Integer, Mat>(Integer.parseInt(pic.getName().substring(0, 1)), cur));
        }


        Mat trainData = new Mat(samples.size(), samples.get(0).getValue().rows() * samples.get(0).getValue().cols(),
                                CvType.CV_32FC1);
        Mat trainClasses = new Mat(samples.size(), 1, CvType.CV_32FC1);
        Map.Entry<Mat, Mat> ret = new AbstractMap.SimpleEntry<Mat, Mat>(trainData, trainClasses);
        for (int i = 0; i < samples.size(); ++i)
        {
            int curVal = samples.get(i).getKey();
            Mat curMat = samples.get(i).getValue();
            trainClasses.put(i, 0, curVal);
            for (int j = 0; j < trainData.cols(); ++j)
            {
                trainData.put(i, j, curMat.get(j / curMat.cols(), j % curMat.cols())[0]);
            }
        }
        return ret;
    }

    public static KNearest getKnnClassifier()
    {
        KNearest kNearest = KNearest.create();
//        Map.Entry<Mat, Mat> trainData = RecogUtils.loadSamplesToMat();
        kNearest.train(trainData.getKey(), Ml.ROW_SAMPLE, trainData.getValue());
        return kNearest;
    }

    @Deprecated
    public static ANN_MLP getAnnClassifier()
    {
        ANN_MLP ann_mlp = ANN_MLP.create();
        Mat layOut = new Mat(1, 3, CvType.CV_32S, new Scalar(new double[]{9, 5, 9}));
//        layOut.put(0,0,new int[]{9,5,9});

        ann_mlp.setLayerSizes(layOut);
        ann_mlp.setTrainMethod(ANN_MLP.BACKPROP);
        ann_mlp.setBackpropMomentumScale(0.1);
        ann_mlp.setBackpropWeightScale(0.1);

//        Map.Entry<Mat, Mat> trainData = RecogUtils.loadSamplesToMat();
        ann_mlp.train(trainData.getKey(), Ml.ROW_SAMPLE, trainData.getValue());

        return ann_mlp;
    }

    public static Mat getEigenVec(Mat mat, EigenStrategy strategy)
    {
        if (strategy == null)
        {
            int cols = mat.rows() * mat.cols();
            Mat ret = new Mat(1, cols, CvType.CV_32FC1);
            for (int j = 0; j < cols; ++j)
            {
                ret.put(0, j, mat.get(j / mat.cols(), j % mat.cols()));
            }
            return ret;
        }
        else
        {
            return strategy.getEigenVec(mat);
        }
    }
}
