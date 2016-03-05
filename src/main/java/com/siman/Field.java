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

    /**
     * セルの数
     */
    public static int CELL_COUNT = HEIGHT * WIDTH;

    public static int WALL = 1;
    public static int FLOOR = 2;
    public static int STONE = 4;
    public static int SOUL = 8;
    public static int DOG = 16;
    public static int NINJA_A = 32;
    public static int NINJA_B = 64;
    public static int FIX_STONE = 128;

    public static int DELETE_NINJA_A = 95;
    public static int DELETE_NINJA_B = 63;
    public static int DELETE_DOG = 495;
    public static int DELETE_SOUL = 503;
    public static int DELETE_STONE = 507;

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
            case 'A':
                return NINJA_A;
            case 'B':
                return NINJA_B;
            default:
                throw new RuntimeException("予期せぬ入力");
        }
    }


    /**
     * 壁かどうかの判定を行う
     *
     * @param state フィールドの状態
     * @return true(壁が存在している)
     */
    public static boolean isWall(int state) {
        return (state & WALL) == WALL;
    }

    /**
     * 床かどうかの判定を行う
     *
     * @param state フィールドの状態
     * @return true(床が存在している)
     */
    public static boolean isFloor(int state) {
        return (state == FLOOR || state == (FLOOR | SOUL));
    }

    /**
     * 石かどうかの判定を行う
     *
     * @param state フィールドの状態
     * @return true(石が存在している)
     */
    public static boolean existStone(int state) {
        return ((state & STONE) == STONE || existFixStone(state));
    }

    /**
     * 忍犬がいるかどうかのチェックを行う
     *
     * @param state フィールドの状態
     * @return true(忍犬が存在している)
     */
    public static boolean existDog(int state) {
        return (state & DOG) == DOG;
    }

    /**
     * ニンジャソウルかどうかの判定を行う
     *
     * @param state フィールドの状態
     * @return true(ニンジャソウルが存在している)
     */
    public static boolean existSoul(int state) {
        return (state & SOUL) == SOUL;
    }

    /**
     * 忍者が存在しているかどうか
     *
     * @param state フィールドの状態
     * @return true(忍者が存在している)
     */
    public static boolean existNinja(int state) {
        return ((state & NINJA_A) == NINJA_A || (state & NINJA_B) == NINJA_B);
    }

    /**
     * 固定石かどうか
     * @param state フィールドの状態
     * @return true(固定石が存在している)
     */
    public static boolean existFixStone(int state) {
        return (state & FIX_STONE) == FIX_STONE;
    }

    /**
     * 固定物かどうかを調べる(石、壁)
     * @param state フィールドの状態
     * @return true(固定物)
     */
    public static boolean existSolidObject(int state) {
        return (existStone(state) || isWall(state));
    }

    /**
     * 石と重なっても大丈夫なオブジェクトかどうかを判定
     * (忍者・忍犬・石・壁が存在しない）
     *
     * @param state フィールドの状態
     * @return true(石と重ねても大丈夫)
     */
    public static boolean isMovableObject(int state) {
        return !(existNinja(state) || existDog(state) || existStone(state) || isWall(state));
    }

    /**
     * 忍犬が移動できるオブジェクトかどうかを判定
     * @param state フィールドの状態
     * @return true(忍犬が移動できる)
     */
    public static boolean isDogMovableObject(int state) {
        return !(existDog(state) || existStone(state) || isWall(state));
    }
}
