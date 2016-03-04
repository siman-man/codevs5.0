package main.java.com.siman;

import com.sun.webkit.dom.HTMLAnchorElementImpl;
import test.java.com.siman.Utility;

import java.util.Arrays;
import java.util.List;

/**
 * Created by siman on 3/1/16.
 */
public class PlayerInfo {

    public final int[] DY = {-1, 0, 1, 0};
    public final int[] DX = {0, 1, 0, -1};

    public final int[] DOGY = {-1, 0, 0, 1};
    public final int[] DOGX = {0, -1, 1, 0};

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
    public List<Dog> dogList;

    /**
     * ニンジャソウルのリスト
     */
    public List<NinjaSoul> soulList;

    /**
     * フィールド
     */
    public Cell[][] field;

    /**
     * フィールドの状態を保存
     */
    public int[][] savedField;

    /**
     * 一時保存用
     */
    public int[][] tempSavedField;

    /**
     * 任意の2点間のセルの最短距離
     */
    public int[][] eachCellDist;

    /**
     * 任意の2点間のセルの最短距離（岩動かさない）
     */
    public int[][] eachCellDistNonPush;

    /**
     * 高速移動モード
     */
    public boolean highSpeedMode;

    /**
     * このターンに分身を召喚している
     */
    public boolean summonsAvator;

    /**
     * 分身が召喚された場所
     */
    public int avatorId;

    /**
     * プレイヤーの術利用回数履歴
     */
    public int[] useSkill;

    public PlayerInfo() {
        this.useSkill = new int[Codevs.MAX_SKILL_COUNT];
        this.savedField = new int[Field.HEIGHT][Field.WIDTH];
        this.tempSavedField = new int[Field.HEIGHT][Field.WIDTH];
    }

    /**
     * 自身の情報をクリアする
     */
    public void clean() {
        this.highSpeedMode = false;
        this.summonsAvator = false;
        this.avatorId = -1;
    }

    /**
     * 忍術を使用する
     */
    public void spell(CommandList commandList) {
        int minDogDist = getMostNearDogDist();

        if (Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] <= 1 && this.soulPower >= Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED]) {
            commandList.useSkill = true;
            commandList.spell = "0";
            this.highSpeedMode = true;
        } else if (minDogDist <= 4 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_AVATAR]) {
            int maxDist = Integer.MIN_VALUE;
            int maxY = -1;
            int maxX = -1;
            Ninja ninjaA = this.ninjaList[0];
            Ninja ninjaB = this.ninjaList[1];
            int nidA = Utility.getId(ninjaA.y, ninjaA.x);
            int nidB = Utility.getId(ninjaB.y, ninjaB.x);

            for (int y = 0; y < Field.HEIGHT; y++) {
                for (int x = 0; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];

                    if (Field.isWall(cell.state) || Field.existStone(cell.state)) continue;

                    int distA = this.eachCellDistNonPush[nidA][cell.id];
                    int distB = this.eachCellDistNonPush[nidB][cell.id];

                    if (distA == INF || distB == INF) continue;
                    int distC = getAllDogDist(y, x);
                    int distD = getAllSoulDist(y, x);

                    if (maxDist < distA + distB - 2 * distC + distD) {
                        maxDist = distA + distB - 2 * distC + distD;
                        maxY = y;
                        maxX = x;
                    }
                }
            }

            if (maxY != -1) {
                commandList.useSkill = true;
                commandList.spell = String.format("%d %d %d", NinjaSkill.MY_AVATAR, maxY, maxX);
                this.summonsAvator = true;
                this.avatorId = Utility.getId(maxY, maxX);
            }
        } else if (this.soulPower >= Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK]) {
            //breakFixStone(commandList);
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
                rollbackField();
                rollbackNinja();
                continue;
            } else {
                tempSaveField();
                tempSaveNinja();
            }

            for (char[] actionB : movePattern) {
                ActionInfo infoB = moveAction(ninjaB, actionB);

                if (infoB.isValid()) {
                    int eval = 0;
                    int nidA = Utility.getId(ninjaA.y, ninjaA.x);
                    int nidB = Utility.getId(ninjaB.y, ninjaB.x);

                    //eval -= this.eachCellDist[nidA][nidB];
                    eval += calcManhattanDist(ninjaA.y, ninjaA.x, ninjaB.y, ninjaB.x);

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

                tempRollbackField();
                tempRollbackNinja();
            }

            rollbackField();
            rollbackNinja();
        }

        // 最後に元に戻しておく
        rollbackField();
        rollbackNinja();

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

        int nidA = Utility.getId(ninjaA.y, ninjaA.x);
        int nidB = Utility.getId(ninjaB.y, ninjaB.x);

        for (NinjaSoul soulA : this.soulList) {
            Cell cellA = this.field[soulA.y][soulA.x];
            if (cellA.dangerValue > 50) continue;
            int distA = this.eachCellDist[nidA][soulA.sid];

            for (NinjaSoul soulB : this.soulList) {
                if (soulA.id == soulB.id) continue;
                Cell cellB = this.field[soulB.y][soulB.x];

                if (this.eachCellDistNonPush[cellA.id][cellB.id] <= 4) continue;
                if (cellB.dangerValue > 50) continue;
                int distB = this.eachCellDist[nidB][soulB.sid];

                int totalDist = distA + distB;

                if (minDist > totalDist) {
                    minDist = totalDist;
                    targetA = soulA.id;
                    targetB = soulB.id;
                    minDistA = distA;
                    minDistB = distB;
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
        int minId = -1;

        for (NinjaSoul soul : this.soulList) {
            if (ninja.targetSoulId == soul.id) {
                continue;
            }

            Cell cell = this.field[soul.y][soul.x];
            //if (cell.dangerValue > 0) continue;
            int nid = Utility.getId(ninja.y, ninja.x);

            int dist = Math.min(this.eachCellDist[nid][soul.sid], this.eachCellDist[soul.sid][nid]);
            if (minDist > dist) {
                minDist = dist;
                minId = soul.id;
            }
        }

        ninja.targetSoulId = minId;
        ninja.targetSoulDist = minDist;
    }

    /**
     * 忍犬の座標を更新する
     */
    public void updateDogPosition() {
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];
        int nidA = Utility.getId(ninjaA.y, ninjaA.x);
        int nidB = Utility.getId(ninjaB.y, ninjaB.x);

        for (Dog dog : this.dogList) {
            int distA = this.eachCellDistNonPush[dog.did][nidA];
            int distB = this.eachCellDistNonPush[dog.did][nidB];
            int tid = (distA > distB) ? nidB : nidA;
            int targetDist = Math.min(distA, distB);

            for (int i = 0; i < 4; i++) {
                int ny = dog.y + DOGY[i];
                int nx = dog.x + DOGX[i];
                Cell cell = this.field[ny][nx];

                if (Field.isDogMovableObject(cell.state)) {
                    int dist = this.eachCellDistNonPush[cell.id][tid];

                    // 条件を満たしたら犬を移動
                    if (targetDist > dist) {
                        this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                        dog.y = ny;
                        dog.x = nx;
                        this.field[dog.y][dog.x].state |= Field.DOG;
                        break;
                    }
                }
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
                this.eachCellDistNonPush[fromCell.id][fromCell.id] = 0;

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
                    removeSoul(ninja.y, ninja.x);
                    updateTargetSoul(ninja);
                }
            } else {
                info.valid = false;
                return info;
            }
        }

        Cell cell = this.field[ninja.y][ninja.x];

        int nid = Utility.getId(ninja.y, ninja.x);
        NinjaSoul soul = this.soulList.get(ninja.targetSoulId);
        int targetDist = this.eachCellDist[nid][soul.sid];

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
        for (Dog dog : this.dogList) {
            this.field[dog.y][dog.x].dangerValue = 99999;

            for (int y = 1; y < Field.HEIGHT; y++) {
                for (int x = 1; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];
                    int dist = Math.max(this.eachCellDist[dog.did][cell.id], this.eachCellDist[cell.id][dog.did]);

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
        for (int y = 1; y < Field.HEIGHT - 1; y++) {
            for (int x = 1; x < Field.WIDTH - 1; x++) {
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
     * 近くの固定石を破壊する
     */
    public void breakFixStone(CommandList commandList) {
        int minDist = Integer.MAX_VALUE;
        int minY = -1;
        int minX = -1;
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        for (int y = 1; y < Field.HEIGHT - 1; y++) {
            for (int x = 1; x < Field.WIDTH - 1; x++) {
                Cell cell = this.field[y][x];

                if (Field.existFixStone(cell.state)) {
                    int distA = calcManhattanDist(y, x, ninjaA.y, ninjaA.x);
                    int distB = calcManhattanDist(y, x, ninjaB.y, ninjaB.x);
                    int dist = Math.min(distA, distB);

                    if (minDist > dist) {
                        minDist = dist;
                        minY = y;
                        minX = x;
                    }
                }
            }
        }

        if (minY != -1) {
            commandList.useSkill = true;
            commandList.spell = String.format("%d %d %d", NinjaSkill.MY_LIGHTNING_ATTACK, minY, minX);
        }
    }


    /**
     * すべての忍犬からの距離を合計する
     */
    public int getAllDogDist(int y, int x) {
        int id = Utility.getId(y, x);
        int totalDist = 0;

        for (Dog dog : this.dogList) {
            int dist = this.calcManhattanDist(y, x, dog.y, dog.x);
            totalDist += (dist == INF) ? 0 : dist;
        }

        return totalDist;
    }

    /**
     * 全てのニンジャソウルからの距離を合計する
     *
     * @return
     */
    public int getAllSoulDist(int y, int x) {
        int id = Utility.getId(y, x);
        int totalDist = 0;

        for (NinjaSoul soul : this.soulList) {
            int dist = this.calcManhattanDist(y, x, soul.y, soul.x);
            totalDist += (dist == INF) ? 0 : dist;
        }

        return totalDist;
    }

    public int getMostNearDogDist() {
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int nidA = Utility.getId(ninjaA.y, ninjaA.x);
        int nidB = Utility.getId(ninjaB.y, ninjaB.x);
        int minDist = Integer.MAX_VALUE;

        for (Dog dog : this.dogList) {
            int distA = this.eachCellDistNonPush[dog.did][nidA];
            int distB = this.eachCellDistNonPush[dog.did][nidB];
            int dist = Math.min(distA, distB);

            if (minDist > dist) {
                minDist = dist;
            }
        }

        return minDist;
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
     * 一時的にフィールドを保存
     */
    public void tempSaveField() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];
                this.tempSavedField[y][x] = cell.state;
            }
        }
    }

    public void tempSaveNinja() {
        for (Ninja ninja : this.ninjaList) {
            ninja.tempSaveStatus();
        }
    }

    /**
     * 一時フィールドに戻す
     */
    public void tempRollbackField() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];
                cell.state = this.tempSavedField[y][x];
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

    public void tempRollbackNinja() {
        for (Ninja ninja : this.ninjaList) {
            ninja.tempRollback();
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

    public void setSoul(int y, int x) {
        this.field[y][x].state |= Field.SOUL;
    }

    public void removeSoul(int y, int x) {
        this.field[y][x].state &= Field.DELETE_SOUL;
    }

    public void setDog(int y, int x) {
        this.field[y][x].state |= Field.DOG;
    }

    public void removeDog(int y, int x) {
        this.field[y][x].state &= Field.DELETE_DOG;
    }

    public int calcManhattanDist(int y1, int x1, int y2, int x2) {
        return (Math.abs(y1 - y2) + Math.abs(x1 - x2));
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
