package ericyu.chepai.train;

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Mat;

/**
 * Created by 麟 on 2015/11/29.
 */
public class FlashStatusTrain extends SampleTrain
{
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
        for(String str : srcImages)
        {
            Mat image = ImageUtils.readImage(str);

        }
    }
}
