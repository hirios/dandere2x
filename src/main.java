import dandere2x.Dandere2x;
import static java.lang.System.exit;


public class main {

    public static void main(String[] args) throws Exception {
        Dandere2x sesh = new Dandere2x("./config.txt");
        if (sesh.isValid()) {
            sesh.start();
        } else {
            System.out.println("Invalid Config.  Check terminal or error log");
            exit(1);
        }
    }
}
