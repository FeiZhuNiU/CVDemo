package ericyu.chepai;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 1/20/2016  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class FileUtils
{
    /**
     * create dir recursively if not exist
     *
     * @param file
     */
    public static void mkDir(File file)
    {
        file = file.getAbsoluteFile();
        if (!file.getParentFile().exists())
        {
            mkDir(file.getParentFile());
        }
        if (!file.exists())
        {
            file.mkdir();
        }
    }

    public static void writeObjectToFile(Object obj, String dst)
    {
        File file = new File(dst);
        FileOutputStream out;
        try
        {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            Logger.log(Logger.Level.INFO, null, "write object success!");
        }
        catch (Exception e)
        {
            Logger.log(Logger.Level.WARNING, null, "write object failed!", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        mkDir(new File("d:\\1\\2\\3\\4"));
    }
}
