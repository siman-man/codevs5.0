package main.java.com.siman;

/**
 * Created by siman on 3/1/16.
 */
public class Ninja {

    // 通常時
    public static char[][] NORMAL_MOVE_PATTERN = {
            // 1マス移動
            {'U'},
            {'R'},
            {'D'},
            {'L'},

            // 2マス移動
            {'U', 'U'},
            {'U', 'R'},
            {'U', 'D'},
            {'U', 'L'},

            {'R', 'U'},
            {'R', 'R'},
            {'R', 'D'},
            {'R', 'L'},

            {'D', 'U'},
            {'D', 'R'},
            {'D', 'D'},
            {'D', 'L'},

            {'L', 'U'},
            {'L', 'R'},
            {'L', 'D'},
            {'L', 'L'},
    };

    // 超高速使用時
    public static char[][] SUPER_MOVE_PATTERN = {
            // 1マス移動
            {'U'},
            {'R'},
            {'D'},
            {'L'},

            // 2マス移動
            {'U', 'U'},
            {'U', 'R'},
            {'U', 'D'},
            {'U', 'L'},

            {'R', 'U'},
            {'R', 'R'},
            {'R', 'D'},
            {'R', 'L'},

            {'D', 'U'},
            {'D', 'R'},
            {'D', 'D'},
            {'D', 'L'},

            {'L', 'U'},
            {'L', 'R'},
            {'L', 'D'},
            {'L', 'L'},

            // 3マス移動
            {'U', 'U', 'U'},
            {'U', 'U', 'R'},
            {'U', 'U', 'D'},
            {'U', 'U', 'L'},

            {'U', 'R', 'U'},
            {'U', 'R', 'R'},
            {'U', 'R', 'D'},
            {'U', 'R', 'L'},

            {'U', 'D', 'U'},
            {'U', 'D', 'R'},
            {'U', 'D', 'D'},
            {'U', 'D', 'L'},

            {'U', 'L', 'U'},
            {'U', 'L', 'R'},
            {'U', 'L', 'D'},
            {'U', 'L', 'L'},

            {'R', 'U', 'U'},
            {'R', 'U', 'R'},
            {'R', 'U', 'D'},
            {'R', 'U', 'L'},

            {'R', 'R', 'U'},
            {'R', 'R', 'R'},
            {'R', 'R', 'D'},
            {'R', 'R', 'L'},

            {'R', 'D', 'U'},
            {'R', 'D', 'R'},
            {'R', 'D', 'D'},
            {'R', 'D', 'L'},

            {'R', 'L', 'U'},
            {'R', 'L', 'R'},
            {'R', 'L', 'D'},
            {'R', 'L', 'L'},

            {'D', 'U', 'U'},
            {'D', 'U', 'R'},
            {'D', 'U', 'D'},
            {'D', 'U', 'L'},

            {'D', 'R', 'U'},
            {'D', 'R', 'R'},
            {'D', 'R', 'D'},
            {'D', 'R', 'L'},

            {'D', 'D', 'U'},
            {'D', 'D', 'R'},
            {'D', 'D', 'D'},
            {'D', 'D', 'L'},

            {'D', 'L', 'U'},
            {'D', 'L', 'R'},
            {'D', 'L', 'D'},
            {'D', 'L', 'L'},

            {'L', 'U', 'U'},
            {'L', 'U', 'R'},
            {'L', 'U', 'D'},
            {'L', 'U', 'L'},

            {'L', 'R', 'U'},
            {'L', 'R', 'R'},
            {'L', 'R', 'D'},
            {'L', 'R', 'L'},

            {'L', 'D', 'U'},
            {'L', 'D', 'R'},
            {'L', 'D', 'D'},
            {'L', 'D', 'L'},

            {'L', 'L', 'U'},
            {'L', 'L', 'R'},
            {'L', 'L', 'D'},
            {'L', 'L', 'L'},
    };

    /**
     * ID
     */
    public int id;

    /**
     * 現在のy座標
     */
    public int y;

    /**
     * 現在のx座標
     */
    public int x;

    /**
     * 保存したy座標
     */
    public int saveY;

    /**
     * 保存したx座標
     */
    public int saveX;

    /**
     * 目標のニンジャソウルID
     */
    public int targetSoulId;

    public Ninja(int id) {
        this.id = id;
    }

    /**
     * 現在の状態を保存
     */
    public void saveStatus() {
        this.saveY = this.y;
        this.saveX = this.x;
    }

    /**
     * 保存した状態に戻す
     */
    public void rollback() {
        this.y = this.saveY;
        this.x = this.saveX;
    }
}
