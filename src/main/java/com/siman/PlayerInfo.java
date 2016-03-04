package main.java.com.siman;

import com.sun.webkit.dom.HTMLAnchorElementImpl;

import java.util.Arrays;

/**
 * Created by siman on 3/1/16.
 */
public class PlayerInfo {

    public final int DY[] = {-1, 0, 1, 0};
    public final int DX[] = {0, 1, 0, -1};

    public static int INF = 99;

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
     * 任意の2点間のセルの最短距離（岩動かさない）
     */
    public int[][] eachCellDistNonPush;

    /**
     *
     */
    public boolean highSpeedMode;

    /**
     * プレイヤーの術利用回数履歴
     */
    public int[] useSkill;

    public PlayerInfo() {
        this.useSkill = new int[Codevs.MAX_SKILL_COUNT];
        this.savedField = new int[Field.HEIGHT][Field.WIDTH];
    }

    /**
     * 忍術を使用する
     */
    public void spell(CommandList commandList) {
        this.highSpeedMode = false;

        if (Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] <= 2 && this.soulPower >= Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED]) {
            commandList.useSkill = true;
            commandList.spell = "0";
            this.highSpeedMode = true;
        } else if (this.soulPower >= Codevs.skillCost[NinjaSkill.MY_AVATAR]) {
            int maxDist = Integer.MIN_VALUE;
            int maxY = -1;
            int maxX = -1;
            Ninja ninjaA = this.ninjaList[0];
            Ninja ninjaB = this.ninjaList[1];
            int nidA = getId(ninjaA.y, ninjaA.x);
            int nidB = getId(ninjaB.y, ninjaB.x);

            for (int y = 0; y < Field.HEIGHT; y++) {
                for (int x = 0; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];

                    if (Field.isWall(cell.state) || Field.existStone(cell.state)) continue;

                    int distA = this.eachCellDistNonPush[nidA][cell.id];
                    int distB = this.eachCellDistNonPush[nidB][cell.id];

                    if (maxDist < distA + distB) {
                        maxDist = distA + distB;
                        maxY = y;
                        maxX = x;
                    }
                }
            }

            if (maxY != -1) {
                commandList.useSkill = true;
                commandList.spell = String.format("%d %d %d", NinjaSkill.MY_AVATAR, maxY, maxX);
            }
        }
    }

    /**
     * 忍者の行動を決める
     */
    public ActionInfo[] action() {
        char[][] movePattern = (this.highSpeedMode) ? Ninja.SUPER_MOVE_PATTERN : Ninja.NORMAL_MOVE_PATTERN;
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int maxEval = Integer.MIN_VALUE;
        ActionInfo bestActionA = new ActionInfo();
        ActionInfo bestActionB = new ActionInfo();

        for (char[] actionA : movePattern) {
            ActionInfo infoA = moveAction(ninjaA, actionA);
            if (!infoA.isValid()) {
                // 状態を元に戻す
                this.rollbackField();
                this.rollbackNinja();
                continue;
            }

            for (char[] actionB : movePattern) {
                ActionInfo infoB = moveAction(ninjaB, actionB);

                if (infoB.isValid()) {
                    int eval = 0;
                    int nidA = getId(ninjaA.y, ninjaA.x);
                    int nidB = getId(ninjaB.y, ninjaB.x);

                    //eval -= this.eachCellDist[nidA][nidB];
                    eval -= calcManhattanDist(ninjaA.y, ninjaA.x, ninjaB.y, ninjaB.x);

                    eval += 200 * infoA.getSoulCount;
                    eval += 200 * infoB.getSoulCount;

                    eval -= infoA.dangerValue;
                    eval -= infoB.dangerValue;

                    eval -= 3 * infoA.targetSoulDist;
                    eval -= 3 * infoB.targetSoulDist;

                    if (maxEval < eval) {
                        maxEval = eval;
                        bestActionA = infoA;
                        bestActionB = infoB;
                    }
                }

                // Aが行動した状態まで戻す
                this.rollbackField();
                this.rollbackNinja();
                moveAction(ninjaA, actionA);
            }

            this.rollbackField();
            this.rollbackNinja();
        }

        // 最後に元に戻しておく
        this.rollbackField();
        this.rollbackNinja();

        return new ActionInfo[]{bestActionA, bestActionB};
    }

    public void setTargetSoulId() {
        int minDist = Integer.MAX_VALUE;
        int minDistA = INF;
        int minDistB = INF;
        int targetA = 0;
        int targetB = 1;

        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int nidA = getId(ninjaA.y, ninjaA.x);
        int nidB = getId(ninjaB.y, ninjaB.x);

        for (int soulIdA = 0; soulIdA < this.soulCount; soulIdA++) {
            NinjaSoul soulA = this.soulList[soulIdA];
            Cell cellA = this.field[soulA.y][soulA.x];
            if (cellA.dangerValue > 0) continue;
            int sidA = getId(soulA.y, soulA.x);
            int distA_1 = this.eachCellDist[nidA][sidA];
            //int distA_2 = this.eachCellDist[sidA][nidA];

            //if (distA_1 != distA_2) continue;

            for (int soulIdB = 0; soulIdB < this.soulCount; soulIdB++) {
                if (soulIdA == soulIdB) continue;
                NinjaSoul soulB = this.soulList[soulIdB];
                Cell cellB = this.field[soulB.y][soulB.x];
                if (cellB.dangerValue > 0) continue;
                int sidB = getId(soulB.y, soulB.x);
                int distB_1 = this.eachCellDist[nidB][sidB];
                //int distB_2 = this.eachCellDist[sidB][nidB];

                //if (distB_1 != distB_2) continue;

                int totalDist = distA_1 + distB_1;

                if (minDist > totalDist) {
                    minDist = totalDist;
                    targetA = soulIdA;
                    targetB = soulIdB;
                    minDistA = distA_1;
                    minDistB = distB_1;
                }
            }
        }

        ninjaA.targetSoulId = targetA;
        ninjaA.targetSoulDist = minDistA;
        ninjaB.targetSoulId = targetB;
        ninjaB.targetSoulDist = minDistB;
    }

    public void updateTargetSoul(Ninja ninja) {
        int minDist = Integer.MAX_VALUE;

        for (int soulId = 0; soulId < this.soulCount; soulId++) {
            if (ninja.targetSoulId == soulId) continue;
            NinjaSoul soul = this.soulList[soulId];
            Cell cell = this.field[soul.y][soul.x];
            if (cell.dangerValue > 0) continue;
            int nid = getId(ninja.y, ninja.x);
            int sid = getId(soul.y, soul.x);

            int dist = Math.min(this.eachCellDist[nid][sid], this.eachCellDist[sid][nid]);
            if (minDist > dist) {
                minDist = dist;
                ninja.targetSoulId = soulId;
            }
        }
    }

    /**
     * 任意の2点間のセルの距離を更新する
     */
    public void updateEachCellDist() {
        this.eachCellDist = new int[Field.CELL_COUNT][Field.CELL_COUNT];
        this.eachCellDistNonPush = new int[Field.CELL_COUNT][Field.CELL_COUNT];

        for (int y = 0; y < Field.CELL_COUNT; y++) {
            Arrays.fill(this.eachCellDist[y], INF);
            Arrays.fill(this.eachCellDistNonPush[y], INF);
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

                        if (Field.existStone(toCell.state)) {
                            int nny = ny + 2 * DY[i];
                            int nnx = nx + 2 * DX[i];

                            if (isOutside(nny, nnx)) continue;
                            int wd = getWallDist(nny, nnx);

                            Cell nCell = this.field[nny][nnx];

                            if (wd <= 2 && (Field.existStone(nCell.state) || Field.isWall(nCell.state))) {
                                this.eachCellDist[fromCell.id][toCell.id] = 99;
                            } else {
                                this.eachCellDist[fromCell.id][toCell.id] = 1;
                            }

                            this.eachCellDistNonPush[fromCell.id][toCell.id] = 0;
                        } else {
                            this.eachCellDist[fromCell.id][toCell.id] = 1;
                            this.eachCellDistNonPush[fromCell.id][toCell.id] = 1;
                        }
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

                    int distNA = this.eachCellDistNonPush[i][j];
                    int distNB = this.eachCellDistNonPush[i][k] + this.eachCellDistNonPush[k][j];
                    this.eachCellDistNonPush[i][j] = Math.min(distNA, distNB);
                }
            }
        }
    }

    /**
     * 忍者を移動させる
     * 事前にcanMove()を使用して有効な移動かどうかを判定しておくこと
     */
    public void move(Ninja ninja, int direct) {
        Cell cell = this.field[ninja.y][ninja.x];

        int ny = ninja.y + DY[direct];
        int nx = ninja.x + DX[direct];

        Cell ncell = this.field[ny][nx];

        // 忍者の位置を更新
        ninja.y = ny;
        ninja.x = nx;
        cell.state &= (ninja.id == 0) ? Field.DELETE_NINJA_A : Field.DELETE_NINJA_B;
        ncell.state |= (ninja.id == 0) ? Field.NINJA_A : Field.NINJA_B;

        // 石が存在する場合は石を押す
        if (Field.existStone(ncell.state)) {
            int nny = ny + DY[direct];
            int nnx = nx + DX[direct];

            Cell nncell = this.field[nny][nnx];

            ncell.state &= Field.DELETE_STONE;
            nncell.state |= Field.STONE;
        }
    }

    /**
     * 忍者が行動する
     *
     * @param ninja       移動する忍者
     * @param commandList 移動リスト
     * @return
     */
    public ActionInfo moveAction(Ninja ninja, char[] commandList) {
        ActionInfo info = new ActionInfo();

        for (char command : commandList) {
            int direct = Direction.toInteger(command);

            if (canMove(ninja.y, ninja.x, direct)) {
                move(ninja, direct);

                Cell cell = this.field[ninja.y][ninja.x];

                if (Field.existSoul(cell.state)) {
                    info.getSoulCount += 1;
                    updateTargetSoul(ninja);
                }
            } else {
                info.valid = false;
                return info;
            }
        }

        Cell cell = this.field[ninja.y][ninja.x];

        int nid = getId(ninja.y, ninja.x);
        NinjaSoul soul = this.soulList[ninja.targetSoulId];
        int sid = getId(soul.y, soul.x);
        int targetDist = this.eachCellDist[nid][sid];

        info.dangerValue = cell.dangerValue;
        info.targetSoulDist = targetDist;
        info.commandList = commandList.clone();

        return info;
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
     * フィールドの危険度を設定
     */
    public void updateDangerValue() {
        for (int id = 0; id < this.dogCount; id++) {
            Dog dog = this.dogList[id];
            int did = getId(dog.y, dog.x);
            this.field[dog.y][dog.x].dangerValue = 99999;

            for (int y = 1; y < Field.HEIGHT; y++) {
                for (int x = 1; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];
                    int dist = Math.max(this.eachCellDist[did][cell.id], this.eachCellDist[cell.id][did]);

                    if (dist <= 1) {
                        cell.dangerValue = 99999;
                    } else if (dist <= 2) {
                        //cell.dangerValue += 50;
                    } else if (dist <= 4) {
                        //cell.dangerValue += Math.max(cell.dangerValue, 5 - dist);
                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                int ny = dog.y + DY[i];
                int nx = dog.x + DX[i];

                Cell cell = this.field[ny][nx];

                if (Field.isWall(cell.state)) continue;
                cell.dangerValue = 99999;
            }
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
     *
     */
    public void saveNinjaStatus() {
        for (int ninjaId = 0; ninjaId < Codevs.NINJA_NUM; ninjaId++) {
            this.ninjaList[ninjaId].saveStatus();
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
     * 忍者を元の位置に戻す
     */
    public void rollbackNinja() {
        for (Ninja ninja : this.ninjaList) {
            ninja.rollback();
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

    public int calcManhattanDist(int y1, int x1, int y2, int x2) {
        return (Math.abs(y1 - y2) - Math.abs(x1 - x2));
    }

    public boolean isInside(int y, int x) {
        return (0 <= y && y < Field.HEIGHT && 0 <= x && x < Field.WIDTH);
    }

    public boolean isOutside(int y, int x) {
        return (y < 0 || Field.HEIGHT <= y || x < 0 || Field.WIDTH <= x);
    }

    public int getWallDist(int y, int x) {
        int distY = Math.min(y, Field.HEIGHT - y - 1);
        int distX = Math.min(x, Field.WIDTH - x - 1);
        return Math.min(distY, distX);
    }
}
