package main.java.com.siman;

/**
 * Created by siman on 3/1/16.
 */
public class Field {
    public static int WALL = 1;
    public static int FLOOR = 2;
    public static int STONE = 4;
    public static int SOUL = 8;
    public static int DOG = 16;
    public static int NINJA = 32;

    /**
     * フィールドの入力を数値に変換する
     *
     * @param type フィールドのタイプ
     * @return 変換された数値
     */
    public static int toInteger(char type) {
        switch(type) {
            case 'W':
                return WALL;
            case '_':
                return FLOOR;
            case 'O':
                return STONE;
            default:
                throw new RuntimeException("予期せぬ入力");
        }
    }
}
