package test.java.com.siman;

import main.java.com.siman.*;

import java.io.File;
import java.util.ArrayList;
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

    public static void readPlayerInfo(PlayerInfo player, String filePath) throws Exception {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        player.soulPower = sc.nextInt();
        player.soulList = new ArrayList<>();
        player.dogList = new ArrayList<>();
        int soulCount = 0;
        int dogCount = 0;

        for (int y = 0; y < Field.HEIGHT; y++) {
            String line = sc.next();

            for (int x = 0; x < Field.WIDTH; x++) {
                char type = line.charAt(x);
                Cell cell = player.field[y][x];
                cell.clear();

                // 壁以外は全て床属性を持つ
                if (type != 'W') {
                    cell.state |= Field.FLOOR;
                }

                cell.state |= Field.toInteger(type);

                if (type == 'S') {
                    NinjaSoul soul = new NinjaSoul(soulCount++, y, x);
                    soul.sid = getId(y, x);
                    player.soulList.add(soul);
                    player.setSoul(y, x);
                }
                if (type == 'D') {
                    Dog dog = new Dog(dogCount++, y, x);
                    dog.did = getId(y, x);
                    player.dogList.add(dog);
                    player.setDog(y, x);
                }
                if (type == 'A') {
                    Ninja ninja = new Ninja(0);
                    ninja.nid = getId(y, x);
                    ninja.y = y;
                    ninja.x = x;

                    player.ninjaList[0] = ninja;
                    player.field[y][x].state |= Field.NINJA_A;
                }
                if (type == 'B') {
                    Ninja ninja = new Ninja(0);
                    ninja.nid = getId(y, x);
                    ninja.y = y;
                    ninja.x = x;

                    player.ninjaList[1] = ninja;
                    player.field[y][x].state |= Field.NINJA_B;
                }
            }
        }

        player.soulCount = player.soulList.size();
        player.dogCount = player.dogList.size();
    }

    public static int getId(int y, int x) {
        return (y * Field.WIDTH) + x;
    }

    public static void showField(Cell[][] field) {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = field[y][x];

                if (Field.isWall(cell.state)) {
                    System.err.print('W');
                } else if (Field.isFloor(cell.state)) {
                    System.err.print('_');
                } else if (Field.existStone(cell.state)) {
                    System.err.print('O');
                }
            }
            System.err.println("");
        }
    }
}
