import java.io.*;

/**
 * Created by wessel on 9/22/16.
 */
public class Controller {
    private final String[] args;

    private Controller(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) throws IOException {
        Controller controller = new Controller(args);
        InputStream in = System.in;
        OutputStream out = System.out;

        try {
            System.setIn(new FileInputStream(args[1]));
            System.setOut(new PrintStream(new FileOutputStream(args[2])));
            controller.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            System.in.close();
            System.out.close();
            System.setIn(in);
            System.setOut((PrintStream) out);
        }
    }

    private void run() throws IOException {
        int[] lookupTable = InitKey(args[0]);
        int i = 0, j = 0;

        while (System.in.available() != 0) {
            int currentChar = System.in.read();
            int keyStreamByte = getKeyStreamByte(lookupTable, i, j);
            int encryptedChar = currentChar ^ keyStreamByte;
            System.out.write(encryptedChar);
        }
    }

    private int getKeyStreamByte(int[] lookupTable, int i, int j) {
        i = (i + 1) % 256;
        j = (j + lookupTable[i]) % 256;
        swap(lookupTable, i, j);
        int t = (lookupTable[i] + lookupTable[j]) % 256;
        return lookupTable[t];
    }

    private int[] InitKey(String arg) {
        //// TODO: 9/26/16 maybe specify a charset
        byte[] key = arg.getBytes();
        int[] lookupTable = new int[256];
        int[] expendedKey = new int[256];

        for (int i = 0; i < 256; i++) {
            lookupTable[i] = i;
            expendedKey[i] = key[i % key.length];
        }

        scrambleLookupTable(lookupTable, expendedKey);

        return lookupTable;
    }

    private static void swap(int[] lookupTable, int i, int j) {
        int temp = lookupTable[i];
        lookupTable[i] = lookupTable[j];
        lookupTable[j] = temp;
    }

    private static void scrambleLookupTable(int[] lookupTable, int[] expendedKey) {
        for (int i = 0, j = 0; i < 256; i++) {
            j = (j + lookupTable[i] + expendedKey[i]) % 256;
            swap(lookupTable, lookupTable[i], j);
        }
    }
}