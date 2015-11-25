package ericyu.recognize;

import ericyu.recognize.image.ImageUtils;
import ericyu.recognize.image.SegSingleColor;
import ericyu.recognize.image.Segmentation;
import ericyu.recognize.recognize.RecogUtils;
import ericyu.recognize.robot.FlashPosition;
import ericyu.recognize.robot.PositionConstants;
import ericyu.recognize.robot.MyRobot;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.KNearest;

import java.util.*;
import java.util.List;

/**
 * Created by 麟 on 2015/10/28.
 */
public class CvDemo
{
    private static Rect picRect;
    private static FlashPosition flashPosition;

    public static void main(String[] args)
    {
        if (!init())
        {
            System.out.println("please verify the input params");
            return;
        }

        //generate a robot
        MyRobot myRobot = new MyRobot(flashPosition);

        //recognize
        ArrayList<Integer> numbers;
        while (true)
        {
            numbers = recogVerificationCode(flashPosition);
            if (numbers != null)
                break;
            try
            {
                myRobot.refreshVerificationCode();
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        myRobot.focusOnVerCodeInputBox();
        myRobot.enterVerificationCode(numbers);
        myRobot.confirmVerificationCode();


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

        //get screen shot
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


    @Deprecated
    private static boolean initParams(String[] args)
    {
        int argCnt = args.length;
        if (argCnt < 4)
        {
            System.out.println("input args are not correct.\n" +
                    "There should be 4 args: \n" +
                    "x,y,width,height\n" +
                    " ______________________________________\n" +
                    "|              ^                       |\n" +
                    "|              |                       |\n" +
                    "|  screen      y                       |\n" +
                    "|              |                       |\n" +
                    "|              v                       |\n" +
                    "|<----x-------> __width____            |\n" +
                    "|          ^   |           |           |\n" +
                    "|      height  | digits    |           |\n" +
                    "|          v   |___________|           |\n" +
                    "|                                      |\n" +
                    "|______________________________________|");
            return false;
        }
        int x, y, width, height;
        try
        {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            width = Integer.parseInt(args[2]);
            height = Integer.parseInt(args[3]);
        } catch (Exception e)
        {
            return false;
        }
        picRect = new Rect(x, y, width, height);
        return true;
    }


}
