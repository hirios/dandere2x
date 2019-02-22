package dandere2x.Utilities;

import java.io.File;
import java.util.Properties;

/**
 * A very disinteresting file checking the validity of a Dandere2x session.
 */
public class Parse {


    //to do, need to parse blockSizes and what not

    /**

     */
    public static boolean validProperties(Properties prop) {

        boolean returnStatement = true;

        //file directories
        String dandereDir = prop.getProperty("dandereDir");
        String workspace = prop.getProperty("workspace");
        String fileDir = prop.getProperty("fileDir");
        String dandere2xCppDir = prop.getProperty("dandere2xCppDir");
        String waifu2xCaffeCUIDir = prop.getProperty("waifu2xCaffeCUIDir");

        //session settings
        String timeFrame = prop.getProperty("timeFrame");
        String duration = prop.getProperty("duration");
        String audioLayer = prop.getProperty("audioLayer");
        int width = Integer.parseInt(prop.getProperty("width"));
        int height = Integer.parseInt(prop.getProperty("height"));

        //user settings //TODO
        int blockSize = Integer.parseInt(prop.getProperty("blockSize"));
        int stepSize = Integer.parseInt(prop.getProperty("stepSize"));
        double tolerance = Double.parseDouble(prop.getProperty("tolerance"));
        String noiseLevel = prop.getProperty("noiseLevel");
        String processType = prop.getProperty("processType");
        int frameRate = Integer.parseInt(prop.getProperty("frameRate"));
        int bleed = Integer.parseInt(prop.getProperty("bleed"));


        if (!Parse.blockSizeValid(blockSize, height, width)) {
            System.out.println("--Invalid BlockSize--");
            System.out.println("Your input: " + blockSize);
            System.out.println("Valid blocksizes for your resolution are:");

            for (int x = 1; x < 250; x++) {
                if (height % x == 0 && width % x == 0) {
                    System.out.print(x + ", ");
                }
            }
            System.out.println("");
            returnStatement = false;
        }
        if (!Parse.dandereLuaValid(dandereDir)) {
            System.out.println("--Invalid dandereDir--");
            System.out.println("Your input: " + dandereDir);
            returnStatement = false;
        }

        if (!Parse.workspaceValid(workspace)) {
            System.out.println("--Invalid Workspace--");
            System.out.println("Your input: " + workspace);
            returnStatement = false;
        }

        if (!Parse.fileValid(fileDir)) {
            System.out.println("--Invalid Filedir--");
            System.out.println("Your input: " + fileDir);
            returnStatement = false;
        }

        if (!Parse.fileValid(dandere2xCppDir)) {
            System.out.println("--Invalid Dandere2xCPPDir--");
            System.out.println("Your input: " + dandere2xCppDir);
            returnStatement = false;
        }

        if (!DandereUtils.isLinux()) {
            if (!Parse.fileValid(waifu2xCaffeCUIDir)) {
                System.out.println("--Invalid waifu2xCaffeCUIDir--");
                System.out.println("Your input: " + waifu2xCaffeCUIDir);
                returnStatement = false;
            }
        }

        if (!Parse.timeValid(timeFrame)) {
            System.out.println("--Invalid timeFrame--");
            System.out.println("Your input: " + timeFrame);
            returnStatement = false;
        }

        if (!Parse.timeValid(duration)) {
            System.out.println("--Invalid Duration--");
            System.out.println("Your input: " + duration);
            returnStatement = false;
        }

        if (!Parse.audioLayerValid(audioLayer)) {
            System.out.println("--Invalid audiolayer--");
            System.out.println("Your input: " + audioLayer);
            returnStatement = false;
        }

        return returnStatement;
    }


    /**
     * Doesnt test if lua file is correct, just if it exists
     *
     * @param location
     * @return
     */
    public static boolean dandereLuaValid(String location) {

        if (location == null) {
            System.out.println(" Dandere Location is null / non existent");
            return false;
        }
        if (!new File(location).exists()) {
            System.out.println("dandereLua location not valid");
            return false;
        }

        if (!location.contains("dandere.lua")) {
            System.out.println("input arguments does not contain dandere.lua");
            return false;
        }
        return true;
    }


    /**
     * Verify workspace exists before continuing
     *
     * @param location
     * @return
     */
    public static boolean workspaceValid(String location) {
        if (location == null) {
            System.out.println("Workspace location is null / non existent");
            return false;
        }
        if (!new File(location).exists()) {
            System.out.println("workspace does not exist");
            return false;
        }
        return true;
    }


    public static boolean fileValid(String fileName) {


        if (fileName == null) {
            System.out.println("Movie is null / non existent");
            return false;
        }

        if (!new File(fileName).exists()) {
            System.out.println("movie in workspace does not exist");
            return false;
        }
        return true;
    }

    /**
     * Ensures a user inputs a valid ffmpeg time
     *
     * @param time
     * @return
     */
    public static boolean timeValid(String time) {

        if (time == null) {
            System.out.println("Time input is null / non existent ");
            return false;
        }

        if (time.length() != 8) {
            System.out.println("Invalid time format. Try something like \"00:28:38\"");
            System.out.println("Your input: " + time);
            return false;
        }

        if (time.charAt(2) != ':' && time.charAt(5) != ':') {
            System.out.println("Invalid time format. Try something like \"00:28:38\"");
            System.out.println("Your input: " + time);
            return false;
        }

        for (int x = 0; x < time.length(); x++) {
            if (x == 2 || x == 5)
                continue;
            if (!Character.isDigit(time.charAt(x))) {
                System.out.println("invalid time argument: " + time);
                System.out.println("Try something like \"00:28:38\"");
                return false;
            }
        }

        String[] splitTime = time.split(":");

        for (int x = 0; x < splitTime.length; x++) {
            if (Integer.parseInt(splitTime[x]) >= 60) {
                System.out.println("Invalid time argument - no integer greater than 59. Carry over to next digit.");
                System.out.println("Example: 00:00:60 should be 00:01:00");
                System.out.println("Your input: " + splitTime[x]);
                return false;
            }
        }
        return true;
    }


    /**
     * Ensures user inputs a valid audio track
     *
     * @param track
     * @return
     */
    public static boolean audioLayerValid(String track) {

        if (track == null) {
            System.out.println("Audio track is null");
            return false;
        }

        if (track.length() != 3) {
            System.out.println("invalid audio track input");
            System.out.println("Try something like 0:1");
            System.out.println("Your input: " + track);
            return false;
        }

        if (track.charAt(1) != ':') {
            System.out.println("invalid audio track input");
            System.out.println("Try something like 0:1");
            System.out.println("Your input: " + track);
            return false;
        }
        for (int x = 0; x < track.length(); x++) {
            if (x == 1)
                continue;
            if (!Character.isDigit(track.charAt(x))) {
                System.out.println("invalid audio track argument: " + track);
                System.out.println("Try something like \"0:1\"");
                return false;
            }
        }
        return true;
    }

    public static boolean blockSizeValid(int blocksize, int height, int width) {
        if (height % blocksize != 0 || width % blocksize != 0) {
            return false;
        }
        return true;
    }


}
