import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;

public class Merge implements Runnable {


    public int blockSize;
    public int bleed;
    public String workspace;
    public int frameCount;
    public int lexiConstant = 6;

    public Merge(int blockSize, int bleed, String workspace, int frameCount) {
        this.blockSize = blockSize;
        this.bleed = bleed;
        this.workspace = workspace;
        this.frameCount = frameCount;
    }


    public void run() {
        Image base = DandereUtils.listenImage(workspace + "merged" + separator + "merged_" + 1 + ".jpg");

        for (int x = 1; x < frameCount; x++) {
            main.console.println("Mering frame " + x);
            Image inversion = DandereUtils.listenImage(workspace + "upscaled" + separator + "output_" +  DandereUtils.getLexiconValue(lexiConstant, x) + ".png");
            base = createPredictive(inversion, base,
                    workspace + "pframe_data" + separator + "pframe_" + x + ".txt",
                    workspace + "inversion_data" + separator + "inversion_" + x + ".txt",
                    workspace + "merged" + separator + "merged_" + (x + 1) + ".jpg");

        }
    }

    //interesting, faster loading .pngs that are larger than .jpgs
    public Image createPredictive(Image inversion, Image base, String pFrameDataDir, String inversionDir, String outLocation) {
        Image out = new Image(base.width, base.height);
        List<String> listPredictive = DandereUtils.listenText(pFrameDataDir);
        List<String> listInversion = DandereUtils.listenText(inversionDir);

        ArrayList<VectorDisplacement> vectorDisplacements = new ArrayList<>();
        ArrayList<VectorDisplacement> inversionDisplacements = new ArrayList<>();


        //read every predictive vector and put it into an arraylist
        for (int x = 0; x < listPredictive.size() / 4; x++) {
            vectorDisplacements.add(
                    new VectorDisplacement(Integer.parseInt(listPredictive.get(x * 4)), Integer.parseInt(listPredictive.get(x * 4 + 1)),
                            Integer.parseInt(listPredictive.get(x * 4 + 2)),
                            Integer.parseInt(listPredictive.get(x * 4 + 3))));
        }

        //read every inversion vector and put it into an arraylist
        for (int x = 0; x < listInversion.size() / 4; x++) {
            inversionDisplacements.add(
                    new VectorDisplacement(Integer.parseInt(listInversion.get(x * 4)), Integer.parseInt(listInversion.get(x * 4 + 1)),
                            Integer.parseInt(listInversion.get(x * 4 + 2)),
                            Integer.parseInt(listInversion.get(x * 4 + 3))));
        }


        //if it is the case that both lists are empty, then the upscaled image is the new frame.
        if (inversionDisplacements.isEmpty() && vectorDisplacements.isEmpty()) {
            out = inversion;
            out.saveFile(outLocation);
            return out;
        }

        //if it is a pFrame but we don't have any inversion items, then simply copy the previous frame.
        if (inversionDisplacements.isEmpty() && !vectorDisplacements.isEmpty()) {
            base.saveFile(outLocation);
            return base;
        }

        //piece together the image using predictive information
        for (int outer = 0; outer < vectorDisplacements.size(); outer++) {
            for (int x = 0; x < blockSize * 2; x++) {
                for (int y = 0; y < blockSize * 2; y++) {
                    out.set(x + 2 * vectorDisplacements.get(outer).x, y + 2 * vectorDisplacements.get(outer).y,
                            base.getNoThrow(x + 2 * vectorDisplacements.get(outer).newX, y + 2 * vectorDisplacements.get(outer).newY));
                }
            }
        }
        //put inversion (the missing) information into the image
        for (int outer = 0; outer < inversionDisplacements.size(); outer++) {
            for (int x = 0; x < (blockSize * 2); x++) {
                for (int y = 0; y < (blockSize * 2); y++) {
                    out.set(inversionDisplacements.get(outer).x * 2 + x, inversionDisplacements.get(outer).y * 2 + y,
                            inversion.get(inversionDisplacements.get(outer).newX * (2 * (blockSize + bleed)) + x + bleed, inversionDisplacements.get(outer).newY * (2 * (blockSize + bleed)) + y + bleed));

                }
            }
        }

        //save the new predictive frame
        out.saveFile(outLocation);

        //reduce time needed at runtime by returning the new image as to not have to load it again
        return out;
    }


}
