/**
 * Created by wessel on 9/26/16.
 */
public class Main {
    public static void main(String[] args){
        try {
            Controller controller = new Controller(args);
            controller.run();
        } catch (Exception e) { System.out.println("Application quit due to: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}