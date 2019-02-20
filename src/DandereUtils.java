import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.io.File.separator;

public class DandereUtils {

    //https://stackoverflow.com/questions/14288185/detecting-windows-or-linux
    public static boolean isLinux() {
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static List<String> readListInFile(String fileName) {

        List<String> lines = Collections.emptyList();
        try {
            lines =
                    Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {

            // do something
            e.printStackTrace();
        }
        return lines;
    }

    public static void systemWait(int n) {
        try {

            Thread.sleep(n);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Returns the files in folder that contain png's.
     * Used for when generating a list of all files outputed by ffmpeg
     *
     * @param path
     * @return
     */
    public static ArrayList<String> getJpgsInFolder(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileList = new ArrayList<String>();

        for (File x : listOfFiles) {
            if (x.toString().contains(".jpg"))
                fileList.add(x.toString());
        }
        Collections.sort(fileList);
        return fileList;
    }


    /**
     * Listens in to see if a file is valid or not.
     * Continue to cycle within a time limit. Currently time limit is 10000 * 100 = 100 seconds.
     * Returns image when it is loaded. If not, keep waiting.
     *
     * @param baseName absolute URL to file
     * @return baseImage if successfully load image.
     */
    public static Image listenImage(String baseName) {

        boolean dneErrorShown = false;
        Image imageLoad = null;
        int timeLimit = 10000;

        for (long x = 0; x < timeLimit; x++) {
            if (!new File(baseName).exists()) {
                if (!dneErrorShown) {
                    System.out.println("Could not find file " + baseName + ".. waiting...");
                    dneErrorShown = true;
                }
                systemWait(100);
                continue;
            }

            try {
                imageLoad = new Image(baseName);
            } catch (NullPointerException e) {
                System.out.println("null pointer exception in image");
                systemWait(100);
                continue;
            } catch (IllegalArgumentException e) {
                System.out.println("error reading png on image, waiting - ");
                systemWait(100);
                continue;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("out of bounds reading png on image, waiting - ");
                systemWait(100);
                continue;
            }

            if (imageLoad != null)
                break;
        }

        if (imageLoad == null)
            throw new IllegalArgumentException("Could not find " + baseName + " within 100 seconds, execution halted");

        return imageLoad;
    }

    /**
     * Listens in to see if a file is valid or not.
     * Continue to cycle within a time limit. Currently time limit is 10000 * 100 = 100 seconds.
     * Returns image when it is loaded. If not, keep waiting.
     *
     * @return baseImage if successfully load image.
     */
    public static List listenText(String input) {

        boolean dneErrorShown = false;
        Image imageLoad = null;
        int timeLimit = 10000;

        for (long x = 0; x < timeLimit; x++) {
            if (!new File(input).exists()) {
                if (!dneErrorShown) {
                    System.out.println("Could not find file " + input + ".. waiting...");
                    dneErrorShown = true;
                }
                systemWait(100);
                continue;
            } else
                break;
        }

        return readListInFile(input);
    }

    //00:26:49
    //given a valid duration string, return how many seconds will pass
    public static int getSecondsFromDuration(String duration) {
        String[] splitted = duration.split(":");

        int hours_in_seconds = (Integer.parseInt(splitted[0])) * 3600;
        int minutes_in_seconds = (Integer.parseInt(splitted[1])) * 60;
        int seconds = (Integer.parseInt(splitted[2]));
        return hours_in_seconds + minutes_in_seconds + seconds;
    }

    public static String getLexiconValue(int digits, int x) {
        String input = x + "";

        while (input.length() < digits) {
            input = "0" + input;
        }

        return input;
    }

    /**
     * Extracts audio in the same regard as 'extract frames' would function.
     *
     * @param workspace
     * @param timeFrame
     * @param fileName
     * @param audioLayer
     * @param duration
     */
    public static void extractAudio(String workspace, String timeFrame, String duration, String fileName, String audioLayer) {

        Process proc = null;
        Runtime run = Runtime.getRuntime();
        String audioExtract = "cmd.exe /C start ffmpeg  -ss " + timeFrame + " -i " + fileName +
                " -t " + duration + " -map " + audioLayer + " " + workspace + "audio.mp3";
        System.out.println(audioExtract);


        try {
            proc = run.exec(audioExtract);
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (proc) {
            while (proc.isAlive()) {
                try {
                    System.out.println("proc is alive");
                    proc.wait(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronized (proc) {
            try {
                proc.wait(1000);
            } catch (InterruptedException e) {

            }
        }
    }


    public static void extractFrames(String workspace, String timeFrame, String fileName, String duration) {

        System.out.println("Extracting frames at " + workspace);
        new File(workspace + "inputs").mkdir();

        Process proc = null;
        Runtime run = Runtime.getRuntime();

        String upscaleCommand = "cmd.exe /C start ffmpeg  -ss " + timeFrame + " -i " + fileName + " -qscale:v 2 " +
                " -t " + duration + " " + workspace + "inputs" + separator + "frame%01d.jpg";
        System.out.println(upscaleCommand);

        try {
            proc = run.exec(upscaleCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (proc) {
            while (proc.isAlive()) {
                try {
                    System.out.println("proc is alive");
                    proc.wait(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("finished");
    }


}
