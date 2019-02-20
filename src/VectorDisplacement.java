/**
 * Structure like object to help organize and sort Fragments
 * A visual explanation of a fragment would be this:
 * <p>
 * 000000
 * 000110
 * 001100
 * 000000
 * <p>
 * Given this array, the fragment would encapsulate all the 1's in the array, so it would
 * look like
 * <p>
 * 000000
 * 001110
 * 001110
 * 000000
 * <p>
 * x = 1
 * y = 1
 * width = 3
 * height = 2
 **/
public class VectorDisplacement implements java.io.Serializable {
    public int size;
    public int x;
    public int y;
    public int newX;
    public int newY;

    public VectorDisplacement(int x, int y, int newX, int newY) {
        this.x = x;
        this.y = y;
        this.newX = newX;
        this.newY = newY;
    }


    @Override
    public String toString() {
        return " x " + x + " y " + y + " newX " + newX + " newY " + newY;
    }

}
