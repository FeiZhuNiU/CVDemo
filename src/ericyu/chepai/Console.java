package ericyu.chepai;

import ericyu.chepai.image.ImageUtils;
import ericyu.chepai.image.SegSingleColor;
import ericyu.chepai.image.Segmentation;
import ericyu.chepai.recognize.RecogUtils;
import ericyu.chepai.robot.FlashPosition;
import ericyu.chepai.robot.OCRUtils;
import ericyu.chepai.robot.PositionConstants;
import ericyu.chepai.robot.MyRobot;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.KNearest;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by 麟 on 2015/10/28.
 */
public class Console
{
    private static Rect picRect;
    private static FlashPosition flashPosition;

    public static void main(String[] args) throws AWTException
    {
        if (!init())
        {
            System.out.println("please verify the input params");
            return;
        }

        //generate a robot
        MyRobot myRobot = new MyRobot(new Robot(),flashPosition);

        myRobot.focusOnCustomAddMoneyInputBox();
        myRobot.inputAddMoneyRange(900);
        myRobot.clickAddMoneyButton();
        myRobot.wait(2000);
        myRobot.clickBidButton();
        myRobot.wait(2000);

        //recognize verification code
        ArrayList<Integer> numbers;
        while (true)
        {
            numbers = recogVerificationCode(flashPosition);
            if (numbers != null)
                break;
            try
            {
                myRobot.clickRefreshVerificationCodeButton();
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        //enter verification code and submit
        myRobot.focusOnVerCodeInputBox();
        myRobot.enterVerificationCode(numbers);
        myRobot.clickConfirmVerificationCodeButton();
        myRobot.wait(500);

        verifyResult(myRobot);

    }

    /**
     * verify bid result
     * @param myRobot
     * @return  0   -> bid success
     *          1   -> not in bid range
     *          -1  -> wrong verification code
     */
    private static int verifyResult(MyRobot myRobot)
    {
        while(true)
        {
            ImageUtils.screenCapture("systemNotification.bmp",
                    flashPosition.origin.x + PositionConstants.SYSTEM_NOTIFICATION_WINDOW_X,
                    flashPosition.origin.y + PositionConstants.SYSTEM_NOTIFICATION_WINDOW_Y,
                    PositionConstants.SYSTEM_NOTIFICATION_WINDOW_WIDTH,
                    PositionConstants.SYSTEM_NOTIFICATION_WINDOW_HEIGHT);
            String result = OCRUtils.doOCR("systemNotification.bmp");
            if (result.contains("范围"))
            {
                myRobot.clickReBidConfirmButton();
                return 1;
            } else if (result.contains("出价威功"))
            {
                return 0;
            } else if (result.contains("验"))
            {
                myRobot.clickReEnterVerificationCodeConfirmButton();
                return -1;
            }
        }


    }

    public static FlashPosition findFlashPosition()
    {
        FlashPosition flashPosition;
        while (true)
        {
            flashPosition = new FlashPosition();
            if (flashPosition.origin != null)
            {
                System.out.println("flash origin found: " + flashPosition.origin.x + "," + flashPosition.origin.y);
                break;
            }
            System.out.println("flash not found yet");
        }
        return flashPosition;
    }

    /**
     *
     * @param flashPosition
     */
    private static ArrayList<Integer> recogVerificationCode(FlashPosition flashPosition)
    {
        //get classifier
        KNearest kNearest = RecogUtils.getKnnClassifier();
//        ANN_MLP ann_mlp = RecogUtils.getAnnClassifier();

        ArrayList<Integer> ret = new ArrayList<Integer>();

        //get screen shot of flash
        ImageUtils.screenCapture(ImageUtils.screenCaptureImage,
                flashPosition.origin.x,
                flashPosition.origin.y,
                PositionConstants.FLASH_WIDTH,
                PositionConstants.FLASH_HEIGHT);
        Mat src = Imgcodecs.imread(ImageUtils.screenCaptureImage);
        //get images to recognize
        List<Mat> digitsToRecog = Segmentation.segmentROI(src, picRect, new SegSingleColor());
        //recognize
        if (digitsToRecog != null && digitsToRecog.size() == 4)
        {

            for (Mat mat : digitsToRecog)
            {
                Mat toRecog = RecogUtils.getEigenVec(mat, null);
                int num = (int) kNearest.findNearest(toRecog, 10, new Mat());
//                    int num = (int)ann_mlp.predict(toRecog);
                ret.add(num);
                System.out.println(num);
            }

            return ret;
        }
        return null;
    }

    private static boolean init()
    {
        picRect = new Rect(PositionConstants.VERIFICATION_CODE_LT_X,
                PositionConstants.VERIFICATION_CODE_LT_Y,
                PositionConstants.VERIFICATION_CODE_WIDTH,
                PositionConstants.VERIFICATION_CODE_HEIGHT);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //find flash position
        flashPosition = findFlashPosition();
        return true;
    }

}
