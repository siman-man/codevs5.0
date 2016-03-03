package test.java.com.siman;

import main.java.com.siman.Cell;
import main.java.com.siman.Field;
import main.java.com.siman.PlayerInfo;

import java.io.File;
import java.util.Scanner;

/**
 * Created by siman on 3/2/16.
 */
public class Utility {
    public static void readFieldInfo(PlayerInfo player, String filePath) throws Exception {
        Cell[][] field = player.field;
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        for (int y = 0; y < Field.HEIGHT; y++) {
            String line = sc.next();

            for (int x = 0; x < Field.WIDTH; x++) {
                char type = line.charAt(x);
                Cell cell = field[y][x];
                cell.clear();

                if (type != 'W') {
                    cell.state |= Field.FLOOR;
                }

                cell.state |= Field.toInteger(type);

                switch (type) {
                    case 'A':
                        player.ninjaList[0].y = y;
                        player.ninjaList[0].x = x;
                        break;
                    case 'B':
                        player.ninjaList[1].y = y;
                        player.ninjaList[1].x = x;
                        break;
                    case 'D':
                        break;
                    case 'S':
                        break;
                }
            }
        }
    }
}
