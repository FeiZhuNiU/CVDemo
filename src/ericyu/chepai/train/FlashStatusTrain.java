package ericyu.chepai.train;

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 麟 on 2015/11/29.
 */
public class FlashStatusTrain extends AbstractSampleTrain
{
    public FlashStatusTrain()
    {
        super(SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_DATA_PATH,SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_CLASSES_PATH,new AllPixelEigenvetorStrategy());
    }

    public FlashStatusTrain(String trainDataPath, String trainClassPath, EigenvetorStrategy eigenvetorStrategy)
    {
        super(trainDataPath, trainClassPath, eigenvetorStrategy);
    }

    public FlashStatusTrain(String[] srcImages, EigenvetorStrategy eigenvetorStrategy)
    {
        super(srcImages, eigenvetorStrategy);
    }

    public FlashStatusTrain(String dir, EigenvetorStrategy eigenvetorStrategy)
    {
        super(dir, eigenvetorStrategy);
    }

    @Override
    protected void setSampleEntries()
    {
        sampleEntries = new ArrayList<>();
        for(File file : srcImages)
        {
            Mat curData = ImageUtils.readImage(file.toString());
            int curClass = Integer.valueOf(file.getName().substring(0,1));
            sampleEntries.add(new AbstractMap.SimpleEntry(curData,curClass));
        }
    }

    @Override
    public List<Mat> process(Mat src)
    {
        List<Mat> ret = new ArrayList<>();
        ret.add(ImageUtils.color2Binary(src,90));
        return ret;
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String sampleDir = "FlashStatusImage";

        FlashStatusTrain train = new FlashStatusTrain(sampleDir, new AllPixelEigenvetorStrategy());
        train.dumpTrainData(SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_DATA_PATH, SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_CLASSES_PATH);

    }
}
