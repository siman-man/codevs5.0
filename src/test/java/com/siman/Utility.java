package test.java.com.siman;

import main.java.com.siman.Cell;
import main.java.com.siman.Field;

import java.io.File;
import java.util.Scanner;

/**
 * Created by siman on 3/2/16.
 */
public class Utility {
    public static void readFieldInfo(Cell[][] field, String filePath) throws Exception {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        for (int y = 0; y < Field.HEIGHT; y++) {
            String line = sc.next();

            for (int x = 0; x < Field.WIDTH; x++) {
                char type = line.charAt(x);
                Cell cell = field[y][x];
                cell.clear();

                cell.state |= Field.toInteger(type);
            }
        }
    }
}
