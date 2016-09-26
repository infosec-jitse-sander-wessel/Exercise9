import org.apache.commons.cli.CommandLine;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by wessel on 9/22/16.
 */
public class RC4 {
    private final String key;

    RC4(String key) {
        this.key = key;
    }

    void run() throws IOException {
        int[] lookupTable = InitKey(key);
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
        byte[] key = arg.getBytes(StandardCharsets.US_ASCII);
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