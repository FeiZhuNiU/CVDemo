package ericyu.chepai.recognition.flashstatus;

import com.iknow.image.AbstractSegStrategy;
import com.iknow.image.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 麟 on 2016/2/17.
 */
public class FlashStatusSeg extends AbstractSegStrategy
{
    public FlashStatusSeg(Mat mat)
    {
        super(mat);
    }

    @Override
    public Mat preProcess()
    {
        Mat temp = ImageUtils.reduceColor(src,1);
        Mat gray = ImageUtils.color2Gray(temp);
        Mat binary = ImageUtils.gray2BinaryReverse(gray);
        return binary;
    }

    @Override
    protected List<Rect> getSegRects(Mat preprocessed)
    {
        List<Rect> ret = new ArrayList<>();
        ret.add(new Rect(0,0,preprocessed.width(),preprocessed.height()));
        return ret;
    }
}
