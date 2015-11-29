package ericyu.chepai.recognize;

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Created by 麟 on 2015/11/29.
 */
public class FlashStatusTrain extends SampleTrain
{
    public FlashStatusTrain(String trainDataPath, String trainClassPath)
    {
        super(trainDataPath, trainClassPath);
    }

    public FlashStatusTrain(String[] srcImages)
    {
        super(srcImages);
    }

    public FlashStatusTrain(String dir)
    {
        super(dir);
    }

    @Override
    protected void setSampleEntries()
    {
        for(String str : srcImages)
        {
            Mat image = ImageUtils.readImage(str);

        }
    }
}
