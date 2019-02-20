import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

import static java.io.File.separator;

public class Dandere2x {

    String dandereDir;
    String workspace;
    String fileDir;
    String timeFrame;
    String duration;
    String audioLayer;
    String dandere2xCppDir;
    String waifu2xCaffeCUIDir;
    String fileLocation;
    String outLocation;
    String upscaledLocation;
    String mergedDir;
    String inversion_dataDir;
    String pframe_dataDir;
    String debugDir;
    String noiseLevel;
    String processType;

    int frameRate;
    int frameCount;
    int blockSize;
    int stepSize;
    double tolerance;
    Properties prop;

    public Dandere2x(String waifu2xCaffeCUIDir, String dandereDir, String workspace, String fileDir, String timeFrame, String duration,
                     String audioLayer, String dandere2xCppDir, int blockSize, double tolerance, int stepSize) {
        this.waifu2xCaffeCUIDir = waifu2xCaffeCUIDir;
        this.dandereDir = dandereDir;
        this.workspace = workspace;
        this.fileDir = fileDir;
        this.timeFrame = timeFrame;
        this.duration = duration;
        this.audioLayer = audioLayer;
        this.dandere2xCppDir = dandere2xCppDir;
        this.blockSize = blockSize;
        this.tolerance = tolerance;
        this.stepSize = stepSize;

        //add noise level
        setDirs();

    }


    //code from https://www.mkyong.com/java/java-properties-file-examples/
    //load settings from a settings.txt file
    public Dandere2x(String settingsDir){

        prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(settingsDir);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            this.dandereDir = prop.getProperty("dandereDir");
            this.workspace = prop.getProperty("workspace");
            this.fileDir = prop.getProperty("fileDir");
            this.timeFrame = prop.getProperty("timeFrame");
            this.duration = prop.getProperty("duration");
            this.audioLayer = prop.getProperty("audioLayer");
            this.dandere2xCppDir = prop.getProperty("dandere2xCppDir");
            this.blockSize = Integer.parseInt(prop.getProperty("blockSize"));
            this.stepSize = Integer.parseInt(prop.getProperty("stepSize"));
            this.tolerance = Double.parseDouble(prop.getProperty("tolerance"));
            this.waifu2xCaffeCUIDir = prop.getProperty("waifu2xCaffeCUIDir");
            this.noiseLevel = prop.getProperty("noiseLevel");
            this.processType = prop.getProperty("processType");
            this.frameRate = Integer.parseInt(prop.getProperty("frameRate"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        setDirs();


    }

    void setDirs(){
        fileLocation = workspace + "inputs" + separator;
        outLocation = workspace + "outputs" + separator;
        upscaledLocation = workspace + "upscaled" + separator;
        mergedDir = workspace + "merged" + separator;
        inversion_dataDir = workspace + "inversion_data" + separator;
        pframe_dataDir = workspace + "pframe_data" + separator;
        debugDir = workspace + "debug" + separator;
    }


    //https://alvinalexander.com/blog/post/java/print-all-java-system-properties
    void printDandereSession(){
        Enumeration keys = prop.keys();
        main.console.println("DANDERE2x SESSION");
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)prop.get(key);
            System.out.println(key + ": " + value);
        }
    }

    void start() throws IOException, InterruptedException {
        DandereUtils.extractFrames(workspace, timeFrame, fileDir, duration);
        DandereUtils.extractAudio(workspace, timeFrame, duration, fileDir, audioLayer);

        System.out.println(workspace + "inputs" + separator);
        frameCount = DandereUtils.getSecondsFromDuration(duration) * frameRate;

        System.out.println("frame count" + frameCount);

        initialSetup(workspace, frameCount, fileLocation, outLocation, upscaledLocation, mergedDir, inversion_dataDir, pframe_dataDir, dandereDir, debugDir);
        startThreadedProcesses();

    }

    public void startThreadedProcesses() throws IOException, InterruptedException {

        //start the process for dandere2x cpp side to upscale frames
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", "start", dandere2xCppDir,
                workspace, frameCount + "", blockSize + "", tolerance + "", stepSize +"");
        System.out.println(dandere2xCppDir + " " +
                workspace + " " + frameCount + "");

        Process p = builder.start();


        //start the process for create inversions from dandere2x frames
        Thread t1 = new Thread() {
            public void run() {
                Difference inv = new Difference(blockSize, 2, workspace, frameCount);
                inv.run();
            }
        };
        t1.start();


        //start the process for merging upscaled frames
        Thread t2 = new Thread() {
            public void run() {
                Merge dif = new Merge(blockSize, 2, workspace, frameCount);
                dif.run();
            }
        };
        t2.start();

        //manually upscale merged_1.jpg (the basis frame)
        Waifu2xCaffe.upscaleFile(waifu2xCaffeCUIDir, fileLocation + "frame1.jpg",
                mergedDir + "merged_" + 1 + ".jpg", processType,noiseLevel,"2");


        //start the process of upscaling every inversion
        Waifu2xCaffe waifu = new Waifu2xCaffe(waifu2xCaffeCUIDir, outLocation, upscaledLocation, frameCount, processType,noiseLevel,"2");
        Thread t3 = new Thread() {
            public void run() {
                waifu.upscale();
            }
        };
        t3.start();

        t1.join();
        t2.join();
        t3.join();
        while (p.isAlive()) {

        }
    }

    public void initialSetup(String workSpace, int frameCount, String fileLocation,
                             String outLocation, String upscaledLocation, String mergedDir, String inversion_dataDir,
                             String pframe_dataDir, String dandereDir, String debugDir) {

        new File(outLocation).mkdir();
        new File(upscaledLocation).mkdir();
        new File(mergedDir).mkdir();

        new File(inversion_dataDir).mkdir();
        new File(pframe_dataDir).mkdir();
        new File(debugDir).mkdir();


        StringBuilder frames = new StringBuilder();
        StringBuilder commands = new StringBuilder();

        //create commands to be used by user call
        commands.append("Copy lines 2 and 3 as one command and paste them into a terminal window cd'd at your waifu2x dir\n");
        commands.append("th " + dandereDir + " -m noise_scale -noise_level 3 -i " + fileLocation + "frame1.jpg" +
                " -o " + mergedDir + "merged_1.jpg && \n");

        commands.append("th " + dandereDir + " -m noise_scale -noise_level 3 -resume 1 -l "
                + workSpace + "frames.txt -o " + upscaledLocation + "upscaled_%d.png\n\n");

        commands.append("Run these commands to put frames into a video with audio\n");
        commands.append("ffmpeg -f image2 -framerate " + this.frameRate + " -i " + mergedDir + "merged_%d.jpg -r 24 " + workSpace + "nosound.mp4\n");

        commands.append("ffmpeg -i " + workSpace + "nosound.mp4" + " -i " + workSpace + "audio.mp3 -c copy "
                + workSpace + "sound.mp4\n");


        //add all frames needing to be upscaled to a text file
        for (int x = 1; x < frameCount; x++)
            frames.append(outLocation + "output_" + x + ".jpg" + "\n");


        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(workSpace + separator + "frames" + ".txt"));
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(workSpace + separator + "commands" + ".txt"));
            writer1.write(frames.toString());
            writer1.close();
            writer2.write(commands.toString());
            writer2.close();
        } catch (IOException e) {
        }
    }

}
