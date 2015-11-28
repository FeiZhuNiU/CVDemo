package ericyu.chepai.recognize;

import java.io.File;

/**
 * Created by 麟 on 2015/11/28.
 */
public class SampleUtils
{
    /**
     * create dir recursively if not exist
     * @param file
     */
    public static void mkDir(File file)
    {
        file = file.getAbsoluteFile();
        if (file.getParentFile().exists())
        {
            file.mkdir();
        } else
        {
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }
}
