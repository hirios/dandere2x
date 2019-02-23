import dandere2x.Dandere2x;

import java.io.File;

import static java.lang.System.exit;


public class main {

    public static void main(String[] args) throws Exception {

        System.out.println(new File("config.txt").getAbsolutePath());
        Dandere2x sesh = new Dandere2x("config.txt");
        if (sesh.isValid()) {
            sesh.start();
        } else {
            System.out.println("Invalid Config.  Check terminal or error log");
            exit(1);
        }
    }
}
