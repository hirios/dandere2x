import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;

public class Difference implements Runnable {

    public int blockSize;
    public int bleed;
    public String workspace;
    public int frameCount;
    public int lexiConstant = 6;

    public Difference(int blockSize, int bleed, String workspace, int frameCount) {
        this.blockSize = blockSize;
        this.bleed = bleed;
        this.workspace = workspace;
        this.frameCount = frameCount;
    }

    public void run() {
        for (int x = 1; x < frameCount; x++) {
            Image im2 = DandereUtils.listenImage(workspace + "inputs" + separator + "frame" + (x + 1) + ".jpg");
            saveInversion(x, im2, workspace + "pframe_data" + separator + "pframe_" + x + ".txt",
                    workspace + "inversion_data" + separator + "inversion_" + x + ".txt",
                    workspace + "outputs" + separator + "output_" + DandereUtils.getLexiconValue(lexiConstant, x) + ".jpg");

            saveDebug(x, im2, workspace + "debug" + separator + "debug_" + DandereUtils.getLexiconValue(lexiConstant, x) + ".jpg");
        }
    }


    public boolean saveInversion(int frameNumber, Image inputFile, String pFrameDataDir, String inversionData, String outLocation) {
        List<String> listInversion = DandereUtils.listenText(inversionData);
        List<String> listPredictive = DandereUtils.listenText(pFrameDataDir);


        ArrayList<VectorDisplacement> vectorDisplacements = new ArrayList<>();


        //the size of the image needed is the square root (rougly) im dimensions. Might go over
        //sometimes
        int size = (int) (Math.sqrt(listInversion.size() / 4) + 1) * (blockSize + bleed);
        Image out = new Image(size, size);


        //for every item in the listInversion, create vector displacements out of them the way they were saved
        for (int x = 0; x < listInversion.size() / 4; x++) {
            vectorDisplacements.add(
                    new VectorDisplacement(Integer.parseInt(listInversion.get(x * 4)), Integer.parseInt(listInversion.get(x * 4 + 1)),
                            Integer.parseInt(listInversion.get(x * 4 + 2)),
                            Integer.parseInt(listInversion.get(x * 4 + 3))));
        }

        //create an inversion based off the images we were given
        for (int outer = 0; outer < vectorDisplacements.size(); outer++) {
            for (int x = 0; x < (blockSize + bleed); x++) {
                for (int y = 0; y < (blockSize + bleed); y++) {

                    out.set(
                            vectorDisplacements.get(outer).newX * (blockSize + bleed) + x,
                            vectorDisplacements.get(outer).newY * (blockSize + bleed) + y,
                            inputFile.getNoThrow(vectorDisplacements.get(outer).x + x - bleed / 2,
                                    vectorDisplacements.get(outer).y + y - bleed / 2));

                }
            }
        }


        //in the case where vectorDisplacements is empty but it is a predictive frame, then
        //simply output an irrelevent image, as the entire frame is to be copied .
        if (vectorDisplacements.isEmpty() && !listPredictive.isEmpty()) {
            Image no = new Image(1, 1);
            no.saveFile(outLocation);
            return true;
        }

        //in the case where both lists are empty, then we are upscaling a brand new frame, in which case,
        //otuput the entire image
        if (vectorDisplacements.isEmpty() && listPredictive.isEmpty()) {
            inputFile.saveFile(outLocation);
            return true;
        }


        //if none of the two cases above, we are working with a simple P frame.
        out.saveFile(outLocation);
        return true;
    }


    //this is a poorly designed function only meant for developer debugging. Please consider
    //fixing this in the future, me.
    public boolean saveDebug(int frameNumber, Image image1, String outLocation) {
        List<String> listPredictive = DandereUtils.listenText(workspace + separator + "pframe_data" + separator
                + "pframe_" + frameNumber + ".txt");

        int xBounds = image1.width;
        int yBounds = image1.height;

        Image PFrame = new Image(xBounds, yBounds);

        ArrayList<VectorDisplacement> blocks = new ArrayList<>();


        //read every predictive vector and put it into an arraylist
        for (int x = 0; x < listPredictive.size() / 4; x++) {
            blocks.add(
                    new VectorDisplacement(Integer.parseInt(listPredictive.get(x * 4)), Integer.parseInt(listPredictive.get(x * 4 + 1)),
                            Integer.parseInt(listPredictive.get(x * 4 + 2)),
                            Integer.parseInt(listPredictive.get(x * 4 + 3))));
        }


        for (int outer = 0; outer < blocks.size(); outer++) {
            for (int x = 0; x < blockSize; x++) {
                for (int y = 0; y < blockSize; y++) {
                    PFrame.set(x + blocks.get(outer).x, y + blocks.get(outer).y,
                            image1.getNoThrow(x + blocks.get(outer).newX, y + blocks.get(outer).newY));
                }
            }
        }

        PFrame.saveFile(outLocation);

        return true;
    }

}
