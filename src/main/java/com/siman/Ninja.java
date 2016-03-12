package main.java.com.siman;

/**
 * Created by siman on 3/1/16.
 */
public class Ninja {

    // 通常時
    public static String[] NORMAL_MOVE_PATTERN = {
            // 1マス移動
            "U",
            "R",
            "D",
            "L",

            // 2マス移動
            "UU",
            "UR",
            "UD",
            "UL",

            "RU",
            "RR",
            "RD",
            "RL",

            "DU",
            "DR",
            "DD",
            "DL",

            "LU",
            "LR",
            "LD",
            "LL",
    };

    // 超高速使用時
    public static String[] SUPER_MOVE_PATTERN = {
            // 1マス移動
            "U",
            "R",
            "D",
            "L",

            // 2マス移動
            "UU",
            "UR",
            "UD",
            "UL",

            "RU",
            "RR",
            "RD",
            "RL",

            "DU",
            "DR",
            "DD",
            "DL",

            "LU",
            "LR",
            "LD",
            "LL",

            // 3マス移動
            "UUU",
            "UUR",
            "UUD",
            "UUL",

            "URU",
            "URR",
            "URD",
            "URL",

            "UDU",
            "UDR",
            "UDD",
            "UDL",

            "ULU",
            "ULR",
            "ULD",
            "ULL",

            "RUU",
            "RUR",
            "RUD",
            "RUL",

            "RRU",
            "RRR",
            "RRD",
            "RRL",

            "RDU",
            "RDR",
            "RDD",
            "RDL",

            "RLU",
            "RLR",
            "RLD",
            "RLL",

            "DUU",
            "DUR",
            "DUD",
            "DUL",

            "DRU",
            "DRR",
            "DRD",
            "DRL",

            "DDU",
            "DDR",
            "DDD",
            "DDL",

            "DLU",
            "DLR",
            "DLD",
            "DLL",

            "LUU",
            "LUR",
            "LUD",
            "LUL",

            "LRU",
            "LRR",
            "LRD",
            "LRL",

            "LDU",
            "LDR",
            "LDD",
            "LDL",

            "LLU",
            "LLR",
            "LLD",
            "LLL",
    };

    /**
     * ID
     */
    public int id;

    /**
     * 今いるセルの位置
     */
    public int nid;

    /**
     * 現在のy座標
     */
    public int y;

    /**
     * 現在のx座標
     */
    public int x;

    public int originY;
    public int originX;

    public int firstY;
    public int firstX;

    /**
     * 保存したy座標
     */
    public int saveY;

    /**
     * 保存したx座標
     */
    public int saveX;

    /**
     * 目的地のID
     */
    public int targetId;

    /**
     * 保存した目標ソウルID
     */
    public int saveTarget;

    public Ninja(int id) {
        this.id = id;
    }

    /**
     * 忍者がいまいる座標のセルIDを返す
     * @return
     */
    public int getNID() {
        return (Field.WIDTH * this.y) + this.x;
    }

    /**
     * 現在の状態を保存
     * 保存する値
     *   - 現在のy座標
     *   - 現在のx座標
     *   - 狙っているニンジャソウルのID
     */
    public void saveStatus() {
        this.saveY = this.y;
        this.saveX = this.x;
        this.saveTarget = this.targetId;
    }

    /**
     * 保存した状態に戻す
     */
    public void rollback() {
        this.y = this.saveY;
        this.x = this.saveX;
        this.targetId = this.saveTarget;
    }

    public String toString() {
        return String.format("id: %d, ninja.y = %d, ninja.x = %d", this.id, this.y, this.x);
    }
}
