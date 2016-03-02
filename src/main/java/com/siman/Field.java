package main.java.com.siman;

/**
 * Created by siman on 3/1/16.
 */
public class Field {
    /**
     * フィールドの縦幅
     */
    public static int HEIGHT = 17;

    /**
     * フィールドの横幅
     */
    public static int WIDTH = 14;

    public static int WALL = 1;
    public static int FLOOR = 2;
    public static int STONE = 4;
    public static int SOUL = 8;
    public static int DOG = 16;
    public static int NINJA = 32;

    /**
     * フィールドの入力を数値に変換する、一部はテストで使用
     *
     * @param type フィールドのタイプ
     * @return 変換された数値
     */
    public static int toInteger(char type) {
        switch (type) {
            case 'W':
                return WALL;
            case '_':
                return FLOOR;
            case 'O':
                return STONE;
            case 'D':
                return DOG;
            case 'S':
                return SOUL;
            case 'N':
                return NINJA;
            default:
                throw new RuntimeException("予期せぬ入力");
        }
    }


    /**
     * 壁かどうかの判定を行う
     */
    public static boolean isWall(int state) {
        return (state & WALL) == WALL;
    }

    /**
     * 床かどうかの判定を行う
     */
    public static boolean isFloor(int state) {
        return (state & FLOOR) == FLOOR;
    }

    /**
     * 石かどうかの判定を行う
     */
    public static boolean isStone(int state) {
        return (state & STONE) == STONE;
    }

    /**
     * ニンジャソウルかどうかの判定を行う
     */
    public static boolean isSoul(int state) {
        return (state & SOUL) == SOUL;
    }

    /**
     * 石と重なっても大丈夫なオブジェクトかどうかを判定
     */
    public static boolean isMovableObject(int state) {
        return (isSoul(state) || isFloor(state));
    }
}
