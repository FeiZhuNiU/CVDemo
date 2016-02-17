package ericyu.chepai;

import com.iknow.image.ImageUtils;
import com.iknow.train.DefaultNamingRule;
import com.iknow.train.eigen.RegionPixelEigenVecStrategy;
import ericyu.chepai.flash.FlashPosition;
import ericyu.chepai.recognition.SampleConstants;
import ericyu.chepai.recognition.flashstatus.FlashStatusSeg;
import ericyu.chepai.recognition.flashstatus.FlashStatusTrain;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * Created by 麟 on 2016/2/17.
 */
public class Test
{
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        ImageUtils.screenCapture("temp.png",
//                FlashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
//                FlashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
//                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
//                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
//        Mat src = ImageUtils.screenCapture(
//                FlashPosition.origin.x + FlashPosition.REGION_FLASH_RIGHT_PART_X,
//                FlashPosition.origin.y + FlashPosition.REGION_FLASH_RIGHT_PART_Y,
//                FlashPosition.REGION_FLASH_RIGHT_PART_WIDTH,
//                FlashPosition.REGION_FLASH_RIGHT_PART_HEIGHT);
//        FlashStatusSeg statusSeg = new FlashStatusSeg(src);
//        Mat result = statusSeg.doSegmentation().get(0);
//        ImageUtils.writeImage(result,"haha.png");

        FlashStatusTrain statusTrain = new FlashStatusTrain("FlashStatusImage",
                SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_DATA_PATH,
                SampleConstants.FLASH_STATUS_SAMPLE_TRAIN_CLASSES_PATH,
                new RegionPixelEigenVecStrategy(5,16), new DefaultNamingRule());
        statusTrain.generateTrainDataAndTrainClassFile();
    }
}
