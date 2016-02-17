package com.iknow;
/*===========================================================================+
 |      Copyright (c) 2015 Eric Yu                                           |
 |                         All rights reserved.                              |
 +===========================================================================+
 |  HISTORY                                                                  |
 |           Created by lliyu on 1/20/2016  (yulin.jay@gmail.com)            |
 +===========================================================================*/

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils
{
    /**
     * create dir recursively if not exist
     *
     * @param file
     */
    public static void mkDirIfNotExists(File file)
    {
        file = file.getAbsoluteFile();
        if (!file.getParentFile().exists())
        {
            mkDirIfNotExists(file.getParentFile());
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void copyFile(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try
            {
                if (inStream != null)
                {
                    inStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                assert outStream != null;
                outStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
    }
}
