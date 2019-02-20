import java.io.*;
import java.util.List;
import java.util.Properties;

public class main {
    public static PrintStream console = System.out; //global variable for output messages

    public static void main(String[] args) throws Exception {
        Dandere2x sesh = new Dandere2x("config.txt");
        sesh.printDandereSession();
        PrintStream o = new PrintStream(new File(sesh.workspace + "filelog.txt"));
        System.setOut(o);
        sesh.start();
    }
}
