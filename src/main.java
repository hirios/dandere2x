import dandere2x.Dandere2x;

import java.io.PrintStream;

import static java.lang.System.exit;


public class main {
    public static PrintStream console = System.out; //global variable for output messages

    public static void main(String[] args) throws Exception {
//        File currentDir = new File("");
//        console.println(currentDir.getAbsoluteFile());
//         console.print(new File("dandere2x_cpp_module").exists());
        Dandere2x sesh = new Dandere2x("config.txt");
        if (sesh.isValid()) {
            sesh.start();
        } else {
            System.out.println("Invalid Config.  Check terminal or error log");
            exit(1);
        }
    }
}
