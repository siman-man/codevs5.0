package main.java.com.siman;

import java.util.Arrays;

/**
 * Created by siman on 3/1/16.
 */
public class PlayerInfo {

    public final int DY[] = {-1, 0, 1, 0};
    public final int DX[] = {0, 1, 0, -1};

    public static int INF = 9999;

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

    /**
     * 忍者の行動を決める
     */
    public void action() {
        char[][] movePattern = Ninja.NORMAL_MOVE_PATTERN;

        for (char[] patternA : movePattern) {
            for (char[] patternB : movePattern) {
            }

            this.rollbackField();
        }
    }

    /**
     * 決定した行動を出力する
     */
    public void output() {
    }

    public void setTargetSoul() {
        int minDist = Integer.MAX_VALUE;
        int targetA = -1;
        int targetB = -1;

        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int nidA = getId(ninjaA.y, ninjaA.x);
        int nidB = getId(ninjaB.y, ninjaB.x);

        for (int soulIdA = 0; soulIdA < this.soulCount; soulIdA++) {
            NinjaSoul soulA = this.soulList[soulIdA];
            int sidA = getId(soulA.y, soulA.x);
            int distA_1 = this.eachCellDist[nidA][sidA];
            int distA_2 = this.eachCellDist[sidA][nidA];

            if (distA_1 != distA_2) continue;

            for (int soulIdB = 0; soulIdB < this.soulCount; soulIdB++) {
                if (soulIdA == soulIdB) continue;
                NinjaSoul soulB = this.soulList[soulIdB];
                int sidB = getId(soulB.y, soulB.x);
                int distB_1 = this.eachCellDist[nidB][sidB];
                int distB_2 = this.eachCellDist[sidB][nidB];

                if (distB_1 != distB_2) continue;

                int totalDist = distA_1 + distB_1;

                if (minDist > totalDist) {
                    minDist = totalDist;
                    targetA = soulIdA;
                    targetB = soulIdB;
                }
            }
        }

        ninjaA.targetSoulId = targetA;
        ninjaB.targetSoulId = targetB;
    }

    /**
     * 任意の2点間のセルの距離を更新する
     */
    public void updateEachCellDist() {
        this.eachCellDist = new int[Field.CELL_COUNT][Field.CELL_COUNT];

        for (int y = 0; y < Field.CELL_COUNT; y++) {
            Arrays.fill(this.eachCellDist[y], INF);
        }

        /**
         * 各セル間のコストを求める
         */
        for (int y = 1; y < Field.HEIGHT - 1; y++) {
            for (int x = 1; x < Field.WIDTH - 1; x++) {
                Cell fromCell = this.field[y][x];

                // 自分自身のコストは0で初期化
                this.eachCellDist[fromCell.id][fromCell.id] = 0;

                for (int i = 0; i < 4; i++) {
                    int ny = y + DY[i];
                    int nx = x + DX[i];

                    Cell toCell = this.field[ny][nx];
                    if (Field.isWall(toCell.state)) continue;

                    if (canMove(y, x, i)) {
                        this.eachCellDist[fromCell.id][toCell.id] = 1;
                    }
                }
            }
        }

        for (int k = 0; k < Field.CELL_COUNT; k++) {
            Cell cellK = getCell(k);
            if (Field.isWall(cellK.state)) continue;

            for (int i = 0; i < Field.CELL_COUNT; i++) {
                Cell cellI = getCell(i);
                if (Field.isWall(cellI.state)) continue;

                for (int j = 0; j < Field.CELL_COUNT; j++) {
                    Cell cellJ = getCell(j);
                    if (Field.isWall(cellJ.state)) continue;

                    int distA = this.eachCellDist[i][j];
                    int distB = this.eachCellDist[i][k] + this.eachCellDist[k][j];
                    this.eachCellDist[i][j] = Math.min(distA, distB);
                }
            }
        }
    }

    /**
     * 忍者を移動させる
     * 事前にcanMove()を使用して有効な移動かどうかを判定しておくこと
     */
    public void move(int playerId, int ninjaId, int direct) {
        Ninja ninja = this.ninjaList[ninjaId];

        Cell cell = this.field[ninja.y][ninja.x];

        int ny = ninja.y + DY[direct];
        int nx = ninja.x + DX[direct];

        Cell ncell = this.field[ny][nx];

        // 忍者の位置を更新
        ninja.y = ny;
        ninja.x = nx;
        cell.state &= (ninjaId == 0) ? Field.DELETE_NINJA_A : Field.DELETE_NINJA_B;
        ncell.state |= (ninjaId == 0) ? Field.NINJA_A : Field.NINJA_B;

        // 石が存在する場合は石を押す
        if (Field.existStone(ncell.state)) {
            int nny = ny + DY[direct];
            int nnx = nx + DX[direct];

            Cell nncell = this.field[nny][nnx];

            ncell.state ^= Field.STONE;
            nncell.state |= Field.STONE;
        }
    }

    /**
     * 指定の座標に移動できるかどうかを調べる
     *
     * @param y      移動元のy座標
     * @param x      移動元のx座標
     * @param direct 移動する方向
     * @return true(移動できる)
     */
    public boolean canMove(int y, int x, int direct) {
        int ny = y + DY[direct];
        int nx = x + DX[direct];

        // 床であれば無条件で移動出来る
        if (Field.isFloor(this.field[ny][nx].state)) return true;
        // 壁は移動出来ない
        if (Field.isWall(this.field[ny][nx].state)) return false;

        // 石の場合は次の座標を見て、石を押せるかどうかを判定する
        if (Field.existStone(this.field[ny][nx].state)) {
            int nny = ny + DY[direct];
            int nnx = nx + DX[direct];

            return Field.isMovableObject(this.field[nny][nnx].state);
        } else {
            return true;
        }
    }

    /**
     * 石の種類を調べる
     */
    public void updateStoneStatus() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];

                if (Field.existStone(cell.state)) {
                    int uy = y + DY[0];
                    int ux = x + DX[0];
                    Cell cellU = this.field[uy][ux];

                    int ry = y + DY[1];
                    int rx = x + DX[1];
                    Cell cellR = this.field[ry][rx];

                    int dy = y + DY[2];
                    int dx = x + DX[2];
                    Cell cellD = this.field[dy][dx];

                    int ly = y + DY[3];
                    int lx = x + DX[3];
                    Cell cellL = this.field[ly][lx];

                    boolean existV = Field.existStone(cellU.state) || Field.existStone(cellD.state);
                    boolean existH = Field.existStone(cellR.state) || Field.existStone(cellL.state);

                    if (existV && existH) {
                        cell.state |= Field.FIX_STONE;
                    }
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

    public int getId(int y, int x) {
        return (y * Field.WIDTH) + x;
    }
}
