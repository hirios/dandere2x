package wrappers;/**
A rather peculiar class. So Waifu2x-Caffee lacks two stuff that my version of Dandere.lua (my modified waifu2x.lua)
file has: 1) ability to scale files in a specific order, 2)  and ability to scale files that don't exist yet.(it should
 wait until the file exists).

1) To combat the first, waifu2x-caffee can read files in lexiographic order, hence why the file names
to be fed into Waifu2xCaffee are in lexigraphic orderings.

2) Waiuf2x-caffee can only scale what's currently in a folder, not files that don't exist yet. Furtheremore,
 it doesn't know what images have been upscaled already, and will reupscale an image if it's already in the ouput folder.
 The way we get around this is by having the 'output' folder become a buffer in a sense, and then signaling waifu2x-caffee
to upscale all the images in the 'output' folder, as it can only upscale a folder, not a list of files.
Once it's all upscaled, do a quick check of what got upscaled, delete them from the outputs Folder,
 then signal the 'upscaleFolder' command in waifu2x-caffee to start the process again, upscaling all the images
 in the 'outputs' folder at a given time.


 **/

import dandere2x.Utilities.DandereUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Waifu2xCaffe {

    //debugging main
//    public static void main(String[] args){
//        String waifu2xCaffeCUIDir = "C:\\Users\\windwoz\\Desktop\\waifu2x-caffe\\waifu2x-caffe-cui.exe";
//        String outputsDir = "C:\\Users\\windwoz\\Desktop\\autodeletetest\\outputs";
//        String upscaledDir = "C:\\Users\\windwoz\\Desktop\\autodeletetest\\upscaled";
//        int count = 137;
//        String setting = "cudnn";
//
//        wrappers.Waifu2xCaffe sesh = new wrappers.Waifu2xCaffe(waifu2xCaffeCUIDir,outputsDir,upscaledDir,count,setting);
//        sesh.upscale();
//    }

    public int lexiConstant = 6;
    String waifu2xCaffeDir;
    String outputDir;
    String upscaledDir;
    String setting;
    String noiseLevel;
    String scaleFactor;
    ArrayList<Integer> upscaledFrames;
    int frameCount;
    Process waifu2xProc = null;

    public Waifu2xCaffe(String waifu2xCaffeDir, String outputDir, String upscaledDir, int frameCount, String setting, String noiseLevel, String scaleFactor) {
        this.waifu2xCaffeDir = waifu2xCaffeDir;
        this.outputDir = outputDir;
        this.upscaledDir = upscaledDir;
        this.frameCount = frameCount;
        this.setting = setting;
        this.scaleFactor = scaleFactor;
        this.noiseLevel = noiseLevel;
        upscaledFrames = new ArrayList<Integer>();

        for (int x = 1; x < frameCount - 1; x++) {
            upscaledFrames.add(x);
        }

    }

    //a function that exists soley for upscaling a single file.
    public static void upscaleFile(String waifu2xCaffeDir, String input, String output, String setting, String noiseLevel, String scaleFactor) {
        Process proc = null;
        Runtime run = Runtime.getRuntime();

        String upscaleCommand = waifu2xCaffeDir + " -i " + input + " -p " + setting + " -n " + noiseLevel + " -s " + scaleFactor + " -o " + output;
        try {
            proc = run.exec(upscaleCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (proc) {
            while (proc.isAlive()) {
            }
        }
    }


    public void upscale() {
        int scaledCount = 0;
        shutdownHook();

        String upscaleCommand = waifu2xCaffeDir + " -i " + outputDir + " -p" + setting + " -n " + noiseLevel + " -s " + scaleFactor + " -o " + upscaledDir;


        /**
         * We keep track of how many frames are removed with 'scaledCount', and exit when we're done.
         */
        while (scaledCount < frameCount - 1) { //todo im being real chief, idk why it's -1 right here.
            Runtime run = Runtime.getRuntime();

            try {
                waifu2xProc = run.exec(upscaleCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * While waifu2x-caffee is upscaling the current batch of images, keep waiting
             */
            synchronized (waifu2xProc) {
                while (waifu2xProc.isAlive()) {
                }
            }

            /**
             * Waifu2x-Caffee will niavely upscale everything in a folder. To mitigate this, we upscale images
             * in batches, in which at a given moment, create a list of all the images in 'outputs', upscale them
             * (the previous command), and now here we delete the ones that upscaled, so the new batch
             * of upscaled images won't be a part of the next batch.
             */

            for (int x = 0; x < upscaledFrames.size(); x++) {
                File tmpDir = new File(upscaledDir + "output_" + DandereUtils.getLexiconValue(lexiConstant, upscaledFrames.get(x)) + ".png");
                if (tmpDir.exists()) {
                    File deleteFile = new File(outputDir + "output_" + DandereUtils.getLexiconValue(lexiConstant, upscaledFrames.get(x)) + ".jpg");
                    if (deleteFile.exists()) {
                        deleteFile.delete();
                        upscaledFrames.remove(x);
                        x--;
                        scaledCount++;
                        System.out.println("removed, scaleCoutn is  " + scaledCount);
                    }
                }
            }
        }
    }

    //todo, test this function

    //if program exits and dandere2xCppProc is still running, close that up
    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (waifu2xProc.isAlive()) {
                    System.out.println("Unexpected shutdown! Waifu2x Process is alive!");
                    waifu2xProc.destroyForcibly();
                    System.out.println("Exiting..");
                }
            }
        });
    }


}
