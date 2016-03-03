package main.java.com.siman;

import java.util.Arrays;

/**
 * Created by siman on 3/1/16.
 */
public class PlayerInfo {

    public final int DY[] = {-1, 0, 1, 0};
    public final int DX[] = {0, 1, 0, -1};

    /**
     * プレイヤーの忍者ソウル・パワー
     */
    public int soulPower;

    /**
     * プレイヤーのフィールドに存在している忍犬の数
     */
    public int dogCount;

    /**
     * プレイヤーのフィールド上に存在しているニンジャソウルの数
     */
    public int soulCount;

    /**
     * 忍者リスト
     */
    public Ninja[] ninjaList;

    /**
     * 忍犬リスト
     */
    public Dog[] dogList;

    /**
     * ニンジャソウルのリスト
     */
    public NinjaSoul[] soulList;

    /**
     * フィールド
     */
    public Cell[][] field;

    /**
     * フィールドの状態を保存
     */
    public int[][] savedField;

    /**
     * 任意の2点間のセルの最短距離
     */
    public int[][] eachCellDist;

    /**
     * プレイヤーの術利用回数履歴
     */
    public int[] useSkill;

    public PlayerInfo() {
        this.useSkill = new int[Codevs.MAX_SKILL_COUNT];
        this.savedField = new int[Field.HEIGHT][Field.WIDTH];
    }

    public void setTargetSoul() {
        int minDist = Integer.MAX_VALUE;
        int targetA = -1;
        int targetB = -1;

        for (int soulIdA = 0; soulIdA < this.soulCount; soulIdA++) {
            NinjaSoul soulA = this.soulList[soulIdA];

            for (int soulIdB = 0; soulIdB < this.soulCount; soulIdB++) {
                if (soulIdA == soulIdB) continue;
                NinjaSoul soulB = this.soulList[soulIdB];
            }
        }
    }

    /**
     * 任意の2点間のセルの距離を更新する
     */
    public void updateEachCellDist() {
        this.eachCellDist = new int[Field.CELL_COUNT][Field.CELL_COUNT];

        for (int y = 0; y < Field.HEIGHT; y++) {
            Arrays.fill(this.eachCellDist[y], Integer.MAX_VALUE);
        }

        for (int k = 0; k < Field.CELL_COUNT; k++) {
            Cell cellA = getCell(k);
            if (Field.isWall(cellA.state)) continue;

            for (int i = 0; i < Field.CELL_COUNT; i++) {
                Cell cellB = getCell(i);
                if (Field.isWall(cellB.state)) continue;

                for (int j = 0; j < Field.CELL_COUNT; j++) {
                    Cell cellC = getCell(j);
                    if (Field.isWall(cellC.state)) continue;
                }
            }
        }
    }

    /**
     * フィールドの状態を保存する
     */
    public void saveField() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];
                this.savedField[y][x] = cell.state;
            }
        }
    }

    /**
     * フィールドの状態を保存時に戻す
     */
    public void rollbackField() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];
                cell.state = this.savedField[y][x];
            }
        }
    }

    /**
     * 自分のフィールドのセルを取得する
     *
     * @param id セルのID
     */
    public Cell getCell(int id) {
        int y = id / Field.WIDTH;
        int x = id % Field.WIDTH;

        return this.field[y][x];
    }
}
