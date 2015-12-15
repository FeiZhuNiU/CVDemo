package ericyu.chepai.train;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 11/30/2015  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import ericyu.chepai.image.ImageUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class RefreshButtonTrain extends AbstractSampleTrain
{

    @Deprecated
    public RefreshButtonTrain()
    {
        super();
    }

    public RefreshButtonTrain(String trainDataPath, String trainClassPath,
                              IEigenvectorStrategy eigenvetorStrategy)
    {
        super(trainDataPath, trainClassPath, eigenvetorStrategy);
    }

    public RefreshButtonTrain(String[] images, IEigenvectorStrategy eigenvetorStrategy, String trainDataPath,
                              String trainClassPath)
    {
        super(images, eigenvetorStrategy, trainDataPath, trainClassPath);
    }

    public RefreshButtonTrain(String dir, IEigenvectorStrategy eigenvetorStrategy, String trainDataPath,
                              String trainClassPath)
    {
        super(dir, eigenvetorStrategy, trainDataPath, trainClassPath);
    }

    @Override
    protected void setSampleEntries()
    {
        sampleEntries = new ArrayList<>();
        for(File file : srcImages)
        {
            Mat curData = ImageUtils.readImage(file.toString());
            int curClass = Integer.valueOf(file.getName().substring(0,1));
            sampleEntries.add(new AbstractMap.SimpleEntry<>(curData,curClass));
        }
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String sampleDir = "RefreshImage";

        RefreshButtonTrain train = new RefreshButtonTrain(sampleDir,
                                                      new RegionPixelEigenVecStrategy(2,10),
                                                      SampleConstants.REFRESH_BUTTON_SAMPLE_TRAIN_DATA_PATH,
                                                      SampleConstants.REFRESH_BUTTON_SAMPLE_TRAIN_CLASSES_PATH);
        train.train();
        train.dumpTrainData();

    }
}
