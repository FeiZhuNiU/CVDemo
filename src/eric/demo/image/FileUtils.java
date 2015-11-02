package eric.demo.image;

import java.io.*;

/**
 * Created by 麟 on 2015/10/31.
 */
public class FileUtils
{
    /**
     * rename image name to image**.png
     * generate *.info for training
     *
     * fileName :  line  0 1 2 3 4 5 ... 9
     */
    private static void renameImageFiles(String fileName)
    {
        File file = new File("resources\\" + fileName);
        File[] images = file.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".png");
            }
        });

        for(int i = 0 ; i < images.length; ++i)
        {
            images[i].renameTo(new File(file.getAbsolutePath() + "\\image"+(i+1)+".png"));
        }
        try
        {
            FileWriter infoFile = new FileWriter(file.getAbsolutePath() + "\\" + fileName + ".info");
            for(int i = 0 ; i < images.length; ++i)
            {
                infoFile.write("image" + (i + 1) + ".png 1 0 0 20 20\n");
            }
            infoFile.close();
            FileWriter negdataFile = new FileWriter(file.getAbsolutePath() + "\\" + fileName + "negdata.txt");
            for(int i = 0 ; i < images.length; ++i)
            {
                negdataFile.write(fileName + "\\image" + (i + 1) + ".png\n");
            }
            negdataFile.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void writeObjectToFile(Object obj)
    {
        File file =new File("test.dat");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (Exception e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }


}
