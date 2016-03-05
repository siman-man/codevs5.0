package main.java.com.siman;

import com.apple.concurrent.Dispatch;
import com.sun.webkit.dom.HTMLAnchorElementImpl;
import test.java.com.siman.Utility;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by siman on 3/1/16.
 */
public class PlayerInfo {

    public final int[] DY = {-1, 0, 1, 0, 0};
    public final int[] DX = {0, 1, 0, -1, 0};

    public final int[] DOGY = {-1, 0, 0, 1};
    public final int[] DOGX = {0, -1, 1, 0};

    public static int INF = 99;
    public static int DETH = 999999;

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
     * 中継ポイントを挟んだ時の距離
     */
    public int[][][] eachCellDistMiddle;

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

        if (this.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL]) {
            fallRockAttack(commandList);

            if (commandList.eval >= 10000) {
                commandList.useSkill = true;
                return;
            }
            if (Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL] <= 3 && commandList.eval >= 3500) {
                commandList.useSkill = true;
                return;
            }
        }

        if (minDogDist <= 6 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_AVATAR]) {
            summonAvator(commandList);
        } else if (Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] <= 2 && this.soulPower >= Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED]) {
            commandList.useSkill = true;
            commandList.spell = "0";
            this.highSpeedMode = true;
        } else if (Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK] <= 3 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK]) {
            breakFixStone(commandList);
        }

    }

    public void summonAvator(CommandList commandList) {
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
            commandList.spell = NinjaSkill.summonMyAvator(maxY, maxX);
            this.summonsAvator = true;
            this.avatorId = Utility.getId(maxY, maxX);
        }
    }

    /**
     * 忍者の行動を決める
     */
    public ActionInfo[] action() {
        String[] movePattern = (this.highSpeedMode) ? Ninja.SUPER_MOVE_PATTERN : Ninja.NORMAL_MOVE_PATTERN;
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];


        int maxEval = Integer.MIN_VALUE;
        ActionInfo bestActionA = new ActionInfo();
        ActionInfo bestActionB = new ActionInfo();

        saveField();
        saveNinjaStatus();

        for (String actionA : movePattern) {
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

            int evalA = infoA.toEval();

            for (String actionB : movePattern) {
                ActionInfo infoB = moveAction(ninjaB, actionB);

                if (infoB.isValid()) {
                    int eval = evalA + infoB.toEval();
                    int nidA = Utility.getId(ninjaA.y, ninjaA.x);
                    int nidB = Utility.getId(ninjaB.y, ninjaB.x);

                    //eval -= this.eachCellDist[nidA][nidB];
                    //eval += calcManhattanDist(ninjaA.y, ninjaA.x, ninjaB.y, ninjaB.x);

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

    public ActionInfo getMaxNinjaEval(Ninja ninja) {
        int maxEval = Integer.MIN_VALUE;
        ActionInfo bestAction = new ActionInfo();
        tempSaveField();
        tempSaveNinja();

        for (String action : Ninja.NORMAL_MOVE_PATTERN) {
            ActionInfo info = moveAction(ninja, action);

            if (info.isValid() && !info.unreach) {
                int eval = info.toEval();

                if (maxEval < eval) {
                    maxEval = eval;
                    bestAction = info;
                }
            }

            tempRollbackField();
            tempRollbackNinja();
        }

        return bestAction;
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
            int distA = this.eachCellDist[nidA][soulA.sid] + cellA.dogValue;

            for (NinjaSoul soulB : this.soulList) {
                if (soulA.id == soulB.id) continue;
                Cell cellB = this.field[soulB.y][soulB.x];

                if (this.eachCellDistNonPush[cellA.id][cellB.id] <= 8) continue;
                int distB = this.eachCellDist[nidB][soulB.sid] + cellB.dogValue;

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

            int dist = this.eachCellDist[nid][soul.sid];
            if (minDist > dist) {
                minDist = dist;
                minId = soul.id;
            }
        }

        ninja.targetSoulId = minId;
        ninja.targetSoulDist = minDist;
    }

    /**
     * 忍犬のターゲットと距離を更新
     */
    public void updateDogTarget() {
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];
        int nidA = Utility.getId(ninjaA.y, ninjaA.x);
        int nidB = Utility.getId(ninjaB.y, ninjaB.x);

        for (Dog dog : this.dogList) {
            int distA = this.eachCellDistNonPush[dog.did][nidA];
            int distB = this.eachCellDistNonPush[dog.did][nidB];
            int tid = (distA > distB) ? nidB : nidA;
            int targetDist = Math.min(distA, distB);

            if (this.summonsAvator) {
                tid = this.avatorId;
                targetDist = this.eachCellDistNonPush[dog.did][tid];
            }

            dog.targetId = tid;
            dog.targetDist = targetDist;
        }
    }

    /**
     * 忍犬の座標を更新する
     */
    public void updateDogPosition() {
        updateDogTarget();
        PriorityQueue<Dog> pque = new PriorityQueue<Dog>(255, new DogComparator());

        for (Dog dog : this.dogList) {
            pque.add(dog);
        }

        while (!pque.isEmpty()) {
            Dog dog = pque.poll();

            for (int i = 0; i < 4; i++) {
                int ny = dog.y + DOGY[i];
                int nx = dog.x + DOGX[i];
                Cell cell = this.field[ny][nx];

                if (Field.isDogMovableObject(cell.state)) {
                    int dist = this.eachCellDistNonPush[cell.id][dog.targetId];

                    // 条件を満たしたら犬を移動
                    if (dog.targetDist > dist) {
                        this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                        this.field[dog.y][dog.x].dangerValue = 0;
                        dog.y = ny;
                        dog.x = nx;
                        this.field[dog.y][dog.x].state |= Field.DOG;
                        break;
                    }
                } else {
                    cell.dangerValue = DETH;
                }
            }
        }
    }

    /**
     * 任意の2点間のセルの距離を更新する
     * TODO : 性能をもっと良くする
     */
    public void updateEachCellDist() {
        this.eachCellDist = new int[Field.CELL_COUNT][Field.CELL_COUNT];
        this.eachCellDistNonPush = new int[Field.CELL_COUNT][Field.CELL_COUNT];
        this.eachCellDistMiddle = new int[Field.CELL_COUNT][Field.CELL_COUNT][Field.CELL_COUNT];

        for (int i = 0; i < Field.CELL_COUNT; i++) {
            Arrays.fill(this.eachCellDist[i], INF);
            Arrays.fill(this.eachCellDistNonPush[i], INF);

            for (int j = 0; j < Field.CELL_COUNT; j++) {
                Arrays.fill(this.eachCellDistMiddle[i][j], 0);
            }
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

                            Cell nCell = this.field[nny][nnx];

                            if (!Field.existStone(nCell.state) && !Field.isWall(nCell.state)) {
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
        Cell fromCell = this.field[ninja.y][ninja.x];

        int ny = ninja.y + DY[direct];
        int nx = ninja.x + DX[direct];

        Cell toCell = this.field[ny][nx];

        // 忍者の位置を更新
        ninja.y = ny;
        ninja.x = nx;
        fromCell.state &= ((ninja.id == 0) ? Field.DELETE_NINJA_A : Field.DELETE_NINJA_B);
        toCell.state |= ((ninja.id == 0) ? Field.NINJA_A : Field.NINJA_B);

        // 石が存在する場合は石を押す
        if (Field.existStone(toCell.state)) {
            int nny = ny + DY[direct];
            int nnx = nx + DX[direct];

            Cell nncell = this.field[nny][nnx];

            removeStone(ny, nx);
            setStone(nny, nnx);
        }
    }

    /**
     * 忍者が行動する
     *
     * @param ninja       移動する忍者
     * @param commandList 移動リスト
     * @return
     */
    public ActionInfo moveAction(Ninja ninja, String commandList) {
        ActionInfo info = new ActionInfo();

        for (char command : commandList.toCharArray()) {
            int direct = Direction.toInteger(command);

            if (canMove(ninja.y, ninja.x, direct)) {
                Cell fromCell = this.field[ninja.y][ninja.x];

                int ny = ninja.y + DY[direct];
                int nx = ninja.x + DX[direct];
                Cell toCell = this.field[ny][nx];
                boolean moveStone = Field.existStone(toCell.state);

                move(ninja, direct);
                int nny = ny + DY[direct];
                int nnx = nx + DX[direct];
                Cell nextCell = this.field[nny][nnx];

                if (Field.existSoul(toCell.state)) {
                    info.getSoulCount += 1;
                    this.removeSoul(ny, nx);
                    removeSoul(ninja.y, ninja.x);
                    updateTargetSoul(ninja);
                }
                if (moveStone) {
                    info.moveStone = true;

                    if (Field.existFixStone(nextCell.state)) {
                        info.createFixStoneCount += 1;
                    }
                    if (Field.existSoul(nextCell.state)) {
                        info.soulHide = true;
                    }
                }

                /*
                int nnny = nny + DY[direct];
                int nnnx = nnx + DX[direct];

                if (isInside(nnny, nnnx)) {
                    Cell nexNexCell = this.field[nny][nnx];

                    if (moveStone && Field.existStone(nexNexCell.state)) {
                        info.moveStone = true;
                        info.createFixStoneCount += 1;
                    }
                }
                */
            } else {
                info.valid = false;
                break;
            }
        }

        Cell cell = this.field[ninja.y][ninja.x];

        int nid = Utility.getId(ninja.y, ninja.x);
        NinjaSoul soul = this.soulList.get(ninja.targetSoulId);
        int targetDist = this.eachCellDist[nid][soul.sid];

        if (targetDist == INF) {
            info.unreach = true;
        }

        info.ninjaY = ninja.y;
        info.ninjaX = ninja.x;
        info.dangerValue = cell.dangerValue;
        info.targetSoulDist = targetDist;
        info.commandList = commandList;

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
            this.field[dog.y][dog.x].dangerValue = DETH;

            for (int y = 1; y < Field.HEIGHT; y++) {
                for (int x = 1; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];
                    int dist = this.eachCellDist[dog.did][cell.id];

                    if (dist <= 1 && !this.summonsAvator) {
                        cell.dangerValue = DETH;
                    } else if (dist <= 2) {
                        //cell.dangerValue += 50;
                    } else if (dist <= 9) {
                        cell.dogValue += 10 - dist;
                        //cell.dangerValue += Math.max(cell.dangerValue, 10 - dist);
                    }
                }
            }

            if (!this.summonsAvator) {
                for (int i = 0; i < 4; i++) {
                    int ny = dog.y + DY[i];
                    int nx = dog.x + DX[i];

                    Cell cell = this.field[ny][nx];

                    if (Field.isWall(cell.state)) continue;
                    cell.dangerValue = DETH;
                }
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

                if (Field.existStone(cell.state) && isFixStone(y, x)) {
                    cell.state |= Field.FIX_STONE;
                }
            }
        }
    }

    /**
     * 指定された石が固定石かどうかを調べる
     *
     * @param y 石のy座標
     * @param x 石のx座標
     */
    public boolean isFixStone(int y, int x) {
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

        boolean existV = Field.existSolidObject(cellU.state) || Field.existSolidObject(cellD.state);
        boolean existH = Field.existSolidObject(cellR.state) || Field.existSolidObject(cellL.state);

        return (existV && existH);
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
     * 相手の嫌なところに岩を落とす
     */
    public void fallRockAttack(CommandList commandList) {
        int minEval = Integer.MAX_VALUE;
        int minY = -1;
        int minX = -1;
        PlayerInfo enemy = Codevs.getEnemyInfo();

        for (Ninja ninja : enemy.ninjaList) {
            ActionInfo baseAction = enemy.getMaxNinjaEval(ninja);
            int basicEval = baseAction.toEval();

            for (int y = -2; y <= 2; y++) {
                for (int x = -2; x <= 2; x++) {
                    int ny = ninja.y + y;
                    int nx = ninja.x + x;

                    if (isOutside(ny, nx)) continue;

                    Cell cell = enemy.field[ny][nx];
                    if (Field.isWall(cell.state)) continue;

                    if (Field.isStonePuttable(cell.state)) {
                        enemy.setStone(ny, nx);

                        ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);
                        int diff = bestAction.toEval() - basicEval;

                        if (minEval > diff && !bestAction.unreach) {
                            minEval = diff;
                            minY = ny;
                            minX = nx;
                        }

                        enemy.removeStone(ny, nx);
                    }
                }
            }
        }

        if (minY != -1 && minEval < -1000) {
            commandList.spell = NinjaSkill.fallrockEnemy(minY, minX);
            commandList.eval = -minEval;
        }
    }

    /**
     * すべての忍犬からの距離を合計する
     */

    public int getAllDogDist(int y, int x) {
        int id = Utility.getId(y, x);
        int totalDist = 0;

        for (Dog dog : this.dogList) {
            int dist = this.eachCellDistNonPush[dog.did][id];
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
            int dist = this.eachCellDistNonPush[soul.sid][id];
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
     * 忍犬の状態を保存する
     */
    public void saveDogStatus() {
        for (Dog dog : this.dogList) {
            dog.saveStatus();
        }
    }

    public void rollbackDogStatus() {
        for (Dog dog : this.dogList) {
            dog.rollback();
        }
    }

    /**
     * 忍者の状態を保存する
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

    /**
     * 忍者の状態を一時的に保存
     */
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

    /**
     * 一時保存した状態に戻す
     */
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

    public void setStone(int y, int x) {
        this.field[y][x].state |= (isFixStone(y, x)) ? Field.FIX_STONE : Field.STONE;
    }

    public void removeStone(int y, int x) {
        this.field[y][x].state &= Field.DELETE_STONE;
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
