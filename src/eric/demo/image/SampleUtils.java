package eric.demo.image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.*;

/**
 * Created by 麟 on 2015/10/31.
 */
public class SampleUtils
{
    /**
     * rename image name to image**.png
     * generate *.info for training
     * <p/>
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

        for (int i = 0; i < images.length; ++i)
        {
            images[i].renameTo(new File(file.getAbsolutePath() + "\\image" + (i + 1) + ".png"));
        }
        try
        {
            FileWriter infoFile = new FileWriter(file.getAbsolutePath() + "\\" + fileName + ".info");
            for (int i = 0; i < images.length; ++i)
            {
                infoFile.write("image" + (i + 1) + ".png 1 0 0 20 20\n");
            }
            infoFile.close();
            FileWriter negdataFile = new FileWriter(file.getAbsolutePath() + "\\" + fileName + "negdata.txt");
            for (int i = 0; i < images.length; ++i)
            {
                negdataFile.write(fileName + "\\image" + (i + 1) + ".png\n");
            }
            negdataFile.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void writeObjectToFile(Object obj)
    {
        File file = new File("test.dat");
        FileOutputStream out;
        try
        {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        }
        catch (Exception e)
        {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }

    /**
     * generate rotated samples (normalized)
     * @param fileName input image (should not be normalized!)
     * @param dstDir dir to save rotated samples
     */
    public static void generateRotatedSamples(String fileName, String dstDir)
    {
        Mat digit = Imgcodecs.imread(fileName);
        for (int i = -8; i <= 8; ++i)
        {
            Mat rotated = ImageUtils.rotateMat(digit, i * 10);
            Mat normalized = ImageUtils.normalize(rotated);
            String curFileName = (new File(fileName).getName()).substring(0, (new File(fileName).getName()).lastIndexOf(
                    "."));
            Imgcodecs.imwrite(dstDir + File.separator + curFileName + "_rotated_" + i * 10 + ".png", normalized);
        }
    }

    public static void generateUnNormalizedSample()
    {
        ImageUtils.dumpUnNormalizedSamples = true;

        ImageUtils.main(new String[]{"CodeImage\\1347.jpg"});
        ImageUtils.main(new String[]{"CodeImage\\1697.jpg"});
        ImageUtils.main(new String[]{"CodeImage\\2118.jpg"});
        ImageUtils.main(new String[]{"CodeImage\\4150.jpg"});

        ImageUtils.dumpUnNormalizedSamples = false;

    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        File unNormFile = new File("dump\\unNormalized");

        File[] files = unNormFile.listFiles();

        for(File pic : files)
        {
            generateRotatedSamples(pic.getAbsolutePath(),"dump\\rotated");
        }
//        generateUnNormalizedSample();
//        generateRotatedSamples(ImageUtils.dumpDir + "2_processed (3).png");
    }

}
