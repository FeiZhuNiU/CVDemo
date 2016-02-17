package ericyu.chepai.recognition.vcode;

import com.iknow.image.AbstractSegStrategy;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

/**
 * Created by 麟 on 2016/2/17.
 */
public class VCodeSeg extends AbstractSegStrategy
{
    public VCodeSeg(Mat mat)
    {
        super(mat);
    }

    @Override
    public Mat preProcess()
    {
        return null;
    }

    @Override
    protected List<Rect> getSegRects(Mat preprocessed)
    {
        return null;
    }
}
