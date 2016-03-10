package main.java.com.siman;

import test.java.com.siman.Utility;

import java.util.*;

/**
 * Created by siman on 3/1/16.
 */
public class PlayerInfo {

    public final int[] DY = {-1, 0, 1, 0, 0};
    public final int[] DX = {0, 1, 0, -1, 0};

    public final int[] DOGY = {-1, 0, 0, 1};
    public final int[] DOGX = {0, -1, 1, 0};

    public static int INF = 99;
    public static int DEATH = 9999999;

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
     * 犬をコストが高い障害物として見た場合
     */
    public int[][] eachCellDistDogBlock;

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

    public boolean BAction;
    public boolean AAction;

    /**
     * 自身の情報をクリアする
     */
    public void clean() {
        this.AAction = false;
        this.BAction = false;
        this.highSpeedMode = false;
        this.summonsAvator = false;
        this.avatorId = -1;
    }

    /**
     * 忍術を使用する
     */
    public void spell(PlayerInfo enemy, CommandList commandList) {
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int dogCntA = getAroundDogCount(ninjaA.y, ninjaA.x);
        int dogCntB = getAroundDogCount(ninjaB.y, ninjaB.x);
        int minDogCnt = Math.max(dogCntA, dogCntB);

        int aliveA = getAliveCellCount(ninjaA.y, ninjaA.x);
        int aliveB = getAliveCellCount(ninjaB.y, ninjaB.x);
        int minAliveCnt = Math.min(aliveA, aliveB);

        int avatorCost = Codevs.skillCost[NinjaSkill.MY_AVATAR] + 2;
        int rockCost = Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL];

        summonEnemyAvator(enemy, commandList);

        if (commandList.useSkill) {
            return;
        }

        if (minAliveCnt > 8 && this.soulPower >= rockCost) {
            fallRockAttack(enemy, commandList);

            if (commandList.eval >= 300000) {
                commandList.useSkill = true;
                return;
            }

            if ((rockCost <= 5 && this.soulPower >= 15) || this.soulPower >= 20) {
                fallRockAttackEasy(enemy, commandList);

                if (commandList.eval >= 300000) {
                    commandList.useSkill = true;
                    return;
                }
            }
        }

        if (minAliveCnt <= 8 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_AVATAR]) {
            summonMyAvator(commandList);
        } else if (this.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_AVATAR]) {
            summonEnemyAvator(enemy, commandList);
        } else if (this.soulPower >= 35 && Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK] <= 4 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK]) {
            breakFixStone(commandList);
        } else if (false && Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] <= 2 && this.soulPower >= Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED]) {
            if (Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] == 2 && this.soulPower <= 30) {
                return;
            }
            commandList.useSkill = true;
            commandList.spell = "0";
            this.highSpeedMode = true;
        }
    }

    public void cleanStone(Ninja ninja, String commands, CommandList commandList) {
        String newCommand = "N" + commands;
        boolean isBreak = false;

        for (char command : newCommand.toCharArray()) {
            int direct = Direction.toInteger(command);

            int ny = ninja.y + DY[direct];
            int nx = ninja.x + DX[direct];
            Cell cell = this.field[ny][nx];

            if (cell.dogDist <= 1) {
                isBreak = true;
            }
        }

        if (!isBreak) return;

        for (char command : commands.toCharArray()) {
            int direct = Direction.toInteger(command);

            int ny = ninja.y + DY[direct];
            int nx = ninja.x + DX[direct];
            Cell cell = this.field[ny][nx];

            if (Field.existStone(cell.state)) {
                commandList.useSkill = true;
                commandList.spell = NinjaSkill.breakMyStone(cell.y, cell.x);
            }
        }
    }

    public void summonMyAvator(CommandList commandList) {
        int maxDist = Integer.MIN_VALUE;
        int maxY = -1;
        int maxX = -1;
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];
        int nidA = Utility.getId(ninjaA.y, ninjaA.x);
        int nidB = Utility.getId(ninjaB.y, ninjaB.x);

        for (int y = 1; y < Field.HEIGHT - 1; y++) {
            for (int x = 1; x < Field.WIDTH - 1; x++) {
                Cell cell = this.field[y][x];

                if (Field.isWall(cell.state) || Field.existStone(cell.state)) continue;

                int distA = this.eachCellDistNonPush[nidA][cell.id];
                int distB = this.eachCellDistNonPush[nidB][cell.id];

                if (distA == INF || distB == INF) continue;
                int distC = getAllDogDist(y, x);
                int distD = getAllSoulDist(y, x);
                int eval = distA + distB;
                //int eval = distA + distB + distD - cell.soulValue;

                if (maxDist < eval) {
                    maxDist = eval;
                    maxY = y;
                    maxX = x;
                }
            }
        }

        if (maxY != -1) {
            Cell cell = this.field[maxY][maxX];

            if (!Field.existNinja(cell.state)) {
                commandList.useSkill = true;
                commandList.spell = NinjaSkill.summonMyAvator(maxY, maxX);
                this.summonsAvator = true;
                this.avatorId = Utility.getId(maxY, maxX);
            }
        }
    }

    public void summonEnemyAvator(PlayerInfo enemy, CommandList commandList) {

        for (Ninja ninja : enemy.ninjaList) {
            ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

            if (bestAction.commandList != "N") {
                continue;
            } else {
                commandList.useSkill = true;
                commandList.spell = NinjaSkill.summonEnemyAvator(ninja.y, ninja.x);
            }
        }
    }

    /**
     * 忍者の行動を決める
     */
    public ActionInfo[] action(PlayerInfo enemy, CommandList commandList) {
        String[] movePattern = (this.highSpeedMode) ? Ninja.SUPER_MOVE_PATTERN : Ninja.NORMAL_MOVE_PATTERN;
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        this.AAction = true;

        ActionInfo bestActionA = getBestAction(ninjaA);

        this.AAction = false;

        moveAction(ninjaA, bestActionA.commandList);
        clearDogValue();
        updateDogValue();
        updateEachCellDist();

        saveField();
        saveNinjaStatus();
        setTargetSoulId();
        saveSoulStatus();

        this.BAction = true;

        ActionInfo bestActionB = getBestAction(ninjaB);

        this.BAction = false;

        ActionInfo[] result = new ActionInfo[]{bestActionA, bestActionB};

        if (enemy.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL]) {
            cleanStone(ninjaA, bestActionA.commandList, commandList);
            cleanStone(ninjaB, bestActionB.commandList, commandList);
        }

        return result;
    }

    public ActionInfo getBestAction(Ninja ninja) {
        ActionInfo bestAction = new ActionInfo();
        int maxEval = Integer.MIN_VALUE;
        int beamWidth = 300;
        int limitDepth = 3;

        PriorityQueue<Node> pque = new PriorityQueue<Node>(beamWidth * 21, new NodeComparator());
        Node root = new Node();
        pque.add(root);

        for (int depth = 0; depth < limitDepth; depth++) {
            Queue<Node> que = new ArrayDeque<>();

            while (!pque.isEmpty()) {
                Node node = pque.poll();

                for (String action : Ninja.NORMAL_MOVE_PATTERN) {
                    updateField(ninja, node.actionHistory);
                    ActionInfo info = moveAction(ninja, action);

                    if (info.isValid() && info.toEval() > -DEATH / 3) {
                        Node nextNode = new Node();

                        if (depth == 0) {
                            updateDogPosition(info.moveStone, this.summonsAvator);
                            clearDogValue();
                            updateDogValue();
                        } else {
                            updateDogPosition(info.moveStone, false);
                            clearDogValue();
                            updateDogValue();
                        }

                        info.positionValue = calcPositionEval(ninja);

                        if (depth != 0) {
                            info.sum(node.info);
                        }
                        nextNode.info = info;

                        if (depth == 0) {
                            nextNode.eval = node.eval + 3 * info.toEval();
                        } else if (depth == 1) {
                            nextNode.eval = node.eval + 2 * info.toEval();
                        } else {
                            nextNode.eval = node.eval + info.toEval();
                        }
                        nextNode.actionHistory = new ArrayList(node.actionHistory);
                        nextNode.actionHistory.add(action);

                        if (depth == limitDepth - 1 && maxEval < nextNode.eval) {
                            maxEval = nextNode.eval;
                            bestAction = info;
                            bestAction.commandList = node.actionHistory.get(0);
                        } else if (nextNode.eval > -DEATH / 2) {
                            que.add(nextNode);
                        }

                    }

                    rollbackField();
                    rollbackNinja();
                    rollbackDogStatus();
                }
            }

            int count = 0;

            while (!que.isEmpty()) {
                Node candidate = que.poll();

                if (count < beamWidth) {
                    pque.add(candidate);
                }

                count++;
            }
        }

        return bestAction;
    }

    public void updateField(Ninja ninja, List<String> actionList) {
        int depth = 0;

        for (String action : actionList) {
            ActionInfo info = moveAction(ninja, action);

            if (depth == 0) {
                updateDogPosition(info.moveStone, this.summonsAvator);
            } else {
                updateDogPosition(info.moveStone, false);
            }

            depth++;
        }
    }

    public ActionInfo getMaxNinjaEval(Ninja ninja) {
        int maxEval = -DEATH / 3;
        ActionInfo bestAction = new ActionInfo();
        tempSaveField();
        tempSaveNinja();
        tempSaveDog();

        for (String action : Ninja.NORMAL_MOVE_PATTERN) {
            ActionInfo info = moveAction(ninja, action);

            if (info.isValid()) {
                updateDogPosition(info.moveStone, this.summonsAvator);
                clearDogValue();
                updateDogValue();

                info.positionValue = calcPositionEval(ninja);
                int eval = info.toEval();

                if (maxEval < eval) {
                    maxEval = eval;
                    bestAction = info;
                }
            }

            tempRollbackField();
            tempRollbackNinja();
            tempRollbackDog();
        }

        return bestAction;
    }

    public ActionInfo beamSearch(Ninja ninja) {
        ActionInfo bestAction = new ActionInfo();

        return bestAction;
    }

    /**
     * 狙うニンジャソウルの対象を決める
     */
    public void setTargetSoulId() {
        int minDist = Integer.MAX_VALUE;
        int targetA = -1;
        int targetB = -1;

        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int nidA = ninjaA.getNID();
        int nidB = ninjaB.getNID();

        for (NinjaSoul soulA : this.soulList) {
            Cell cellA = this.field[soulA.y][soulA.x];
            int distA = this.eachCellDistDogBlock[nidA][soulA.sid];
            //if (distA >= 30) continue;
            if (!soulA.exist) continue;
            if (Field.existDog(cellA.state)) continue;

            for (NinjaSoul soulB : this.soulList) {
                if (soulA.id == soulB.id) continue;
                if (!soulB.exist) continue;
                Cell cellB = this.field[soulB.y][soulB.x];

                if (this.eachCellDist[cellA.id][cellB.id] <= 8) continue;
                int distB = this.eachCellDistDogBlock[nidB][soulB.sid];
                //if (distB >= 30) continue;
                if (Field.existDog(cellB.state)) continue;

                int totalDist = distA + distB;

                if (minDist > totalDist) {
                    minDist = totalDist;
                    targetA = soulA.sid;
                    targetB = soulB.sid;
                }
            }
        }

        ninjaA.targetId = targetA;
        ninjaB.targetId = targetB;
    }

    /**
     * 次に狙うニンジャソウルの場所を決める
     *
     * @param ninja
     */
    public void updateTargetSoul(Ninja ninja) {
        int minDist = Integer.MAX_VALUE;
        int nextTargetId = -1;
        int nid = ninja.getNID();

        for (NinjaSoul soul : this.soulList) {
            if (ninja.targetId == soul.sid) {
                soul.exist = false;
                continue;
            }
            if (!soul.exist) continue;

            int dist = this.eachCellDistDogBlock[nid][soul.sid];
            if (minDist > dist) {
                minDist = dist;
                nextTargetId = soul.sid;
            }
        }

        ninja.targetId = nextTargetId;
    }

    /**
     * 忍犬のターゲットと距離を更新
     */
    public void updateDogTarget(boolean moveStone, boolean summon) {
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];
        int nidA = ninjaA.getNID();
        int nidB = ninjaB.getNID();

        for (Dog dog : this.dogList) {
            int distA = this.eachCellDistNonPush[dog.did][nidA];
            int distB = this.eachCellDistNonPush[dog.did][nidB];
            int tid = (distA > distB) ? nidB : nidA;
            int targetDist = Math.min(distA, distB);

            if (this.AAction) {
                tid = nidA;
            }
            if (this.BAction) {
                tid = nidB;
            }
            if (summon) {
                tid = this.avatorId;
                targetDist = this.eachCellDistNonPush[dog.did][tid];
            }

            if (moveStone) {
                for (int i = 0; i < 4; i++) {
                    int ny = dog.y + DY[i];
                    int nx = dog.x + DX[i];
                    Cell cell = this.field[ny][nx];

                    if (Field.existNinja(cell.state)) {
                        /*
                        if (ny == ninjaA.y && nx == ninjaA.x) {
                            tid = nidA;
                        } else {
                            tid = nidB;
                        }
                        */

                        targetDist = 1;
                    }
                }
            }

            dog.targetId = tid;
            dog.targetDist = targetDist;
        }
    }

    /**
     * 石以外にも忍犬をブロックとして扱い
     *
     * @param cy
     * @param cx
     * @return
     */
    public int getAliveCellCount(int cy, int cx) {
        Queue<Integer> queueX = new LinkedList<Integer>();
        Queue<Integer> queueY = new LinkedList<Integer>();
        Queue<Integer> queueD = new LinkedList<Integer>();

        queueY.add(cy);
        queueX.add(cx);
        queueD.add(0);

        int aliveCellCount = 0;

        boolean[][] checkList = new boolean[Field.HEIGHT][Field.WIDTH];
        for (int y = 0; y < Field.HEIGHT; y++) {
            Arrays.fill(checkList[y], false);
        }

        while (!queueX.isEmpty()) {
            int curY = queueY.poll();
            int curX = queueX.poll();
            int dist = queueD.poll();
            Cell cell = this.field[curY][curX];

            if (checkList[curY][curX]) continue;
            if (dist / 2 >= cell.dogDist) continue;
            if (Field.existDog(cell.state)) {
                continue;
            }
            if (getAroundStoneCount(curY, curX) == 4) continue;

            checkList[curY][curX] = true;

            if (cell.dogDist > 1) {
                aliveCellCount++;
            }

            for (int i = 0; i < 4; i++) {
                int ny = curY + DY[i];
                int nx = curX + DX[i];
                Cell ncell = this.field[ny][nx];

                if (canMove(curY, curX, i) && !Field.existDog(ncell.state)) {
                    queueY.add(ny);
                    queueX.add(nx);
                    queueD.add(dist + 1);
                }
            }
        }

        return aliveCellCount;
    }

    public boolean isExistAroundDog(int y, int x) {
        for (int i = 0; i < 4; i++) {
            int ny = y + DY[i];
            int nx = x + DX[i];
            if (isOutside(ny, nx)) continue;
            Cell cell = this.field[ny][nx];


            if (Field.existDog(cell.state)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 周囲8マスに存在している犬の数を調べる
     *
     * @param cy 調べる起点となる座標
     * @param cx 調べる起点となる座標
     * @return
     */
    public int getCircleDogCount(int cy, int cx) {
        int dogCnt = 0;

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                int ny = cy + y;
                int nx = cx + x;

                if (isOutside(ny, nx)) continue;
                Cell cell = this.field[ny][nx];
                if (Field.isWall(cell.state)) continue;

                if (Field.existDog(cell.state)) {
                    dogCnt++;
                }
            }
        }

        return dogCnt;
    }

    /**
     * 指定された座標の周りにある床の数を調べる
     *
     * @param y
     * @param x
     * @return
     */
    public int getAroundFloorCount(int y, int x) {
        int floorCount = 0;

        for (int i = 0; i < 4; i++) {
            int ny = y + DY[i];
            int nx = x + DX[i];
            Cell cell = this.field[ny][nx];

            if (Field.isFloor(cell.state)) {
                floorCount++;
            }
        }

        return floorCount;
    }

    /**
     * 自分の周りにある石の数を調べる
     *
     * @param y
     * @param x
     * @return
     */
    public int getAroundStoneCount(int y, int x) {
        int stoneCnt = 0;

        for (int i = 0; i < 4; i++) {
            int ny = y + DY[i];
            int nx = x + DX[i];
            Cell cell = this.field[ny][nx];

            if (Field.existSolidObject(cell.state)) {
                stoneCnt++;
            }
        }

        return stoneCnt;
    }

    public int getAroundDogCount(int cy, int cx) {
        Queue<Integer> queueX = new LinkedList<Integer>();
        Queue<Integer> queueY = new LinkedList<Integer>();
        Queue<Integer> queueD = new LinkedList<Integer>();

        queueY.add(cy);
        queueX.add(cx);
        queueD.add(0);

        int dogCnt = 0;

        boolean[][] checkList = new boolean[Field.HEIGHT][Field.WIDTH];
        for (int y = 0; y < Field.HEIGHT; y++) {
            Arrays.fill(checkList[y], false);
        }

        while (!queueX.isEmpty()) {
            int curY = queueY.poll();
            int curX = queueX.poll();
            int dist = queueD.poll();
            Cell cell = this.field[curY][curX];

            if (dist > 4) continue;
            if (Field.existSolidObject(cell.state)) continue;
            if (checkList[curY][curX]) continue;
            checkList[curY][curX] = true;

            if (Field.existDog(cell.state)) {
                if (dist <= 1) {
                    dogCnt += 4;
                } else {
                    dogCnt++;
                }
            }

            for (int i = 0; i < 4; i++) {
                int ny = curY + DY[i];
                int nx = curX + DX[i];

                queueY.add(ny);
                queueX.add(nx);
                queueD.add(dist + 1);
            }
        }

        return dogCnt;
    }

    /**
     * 忍犬の座標を更新する
     */
    public void updateDogPosition(boolean moveStone, boolean summon) {
        updateDogTarget(moveStone, summon);
        PriorityQueue<Dog> pque = new PriorityQueue<Dog>(255, new DogComparator());

        // 忍者への最短距離が短いやつから処理をしていく
        for (Dog dog : this.dogList) {
            pque.add(dog);
        }

        while (!pque.isEmpty()) {
            Dog dog = pque.poll();

            if (moveStone) {
                for (int i = 0; i < 4; i++) {
                    int ny = dog.y + DOGY[i];
                    int nx = dog.x + DOGX[i];
                    Cell cell = this.field[ny][nx];

                    if (Field.existNinja(cell.state)) {
                        this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                        dog.y = ny;
                        dog.x = nx;
                        this.field[dog.y][dog.x].state |= Field.DOG;
                        break;
                    }
                }
                continue;
            }

            for (int i = 0; i < 4; i++) {
                int ny = dog.y + DOGY[i];
                int nx = dog.x + DOGX[i];
                Cell cell = this.field[ny][nx];

                if (Field.isDogMovableObject(cell.state)) {
                    int dist = this.eachCellDistNonPush[cell.id][dog.targetId];

                    // 条件を満たしたら犬を移動
                    if (dog.targetDist > dist) {
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
     * TODO : 性能をもっと良くする
     */
    public void updateEachCellDist() {
        this.eachCellDist = new int[Field.CELL_COUNT][Field.CELL_COUNT];
        this.eachCellDistNonPush = new int[Field.CELL_COUNT][Field.CELL_COUNT];
        this.eachCellDistDogBlock = new int[Field.CELL_COUNT][Field.CELL_COUNT];

        for (int i = 0; i < Field.CELL_COUNT; i++) {
            Arrays.fill(this.eachCellDist[i], INF);
            Arrays.fill(this.eachCellDistNonPush[i], INF);
            Arrays.fill(this.eachCellDistDogBlock[i], INF);
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
                this.eachCellDistDogBlock[fromCell.id][fromCell.id] = 0;

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

                            if (Field.existSolidObject(nCell.state)) {
                                this.eachCellDist[fromCell.id][toCell.id] = 1;
                                this.eachCellDistDogBlock[fromCell.id][toCell.id] = 1;
                            }
                        } else if (Field.existDog(toCell.state)) {
                            this.eachCellDistDogBlock[fromCell.id][toCell.id] = 15;
                            this.eachCellDist[fromCell.id][toCell.id] = 1;
                            this.eachCellDistNonPush[fromCell.id][toCell.id] = 1;
                        } else {
                            if (toCell.dogValue > 0) {
                                this.eachCellDistDogBlock[fromCell.id][toCell.id] = toCell.dogValue;
                            } else {
                                this.eachCellDistDogBlock[fromCell.id][toCell.id] = 1;
                            }
                            this.eachCellDist[fromCell.id][toCell.id] = 1;
                            this.eachCellDistNonPush[fromCell.id][toCell.id] = 1;
                        }
                    }
                }
            }
        }

        // ワーシャル - フロイドで全ての最短距離を求める
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

                    int distDA = this.eachCellDistDogBlock[i][j];
                    int distDB = this.eachCellDistDogBlock[i][k] + this.eachCellDistDogBlock[k][j];
                    this.eachCellDistDogBlock[i][j] = Math.min(distDA, distDB);
                }
            }
        }
    }

    /**
     * 移動後のフィールドの状態からの評価値を出す
     *
     * @param ninja 評価したい対象
     * @return 評価値
     */
    public int calcPositionEval(Ninja ninja) {
        Cell cell = this.field[ninja.y][ninja.x];
        int eval = 0;
        eval -= cell.dogValue;
        eval += cell.soulValue;

        if (Field.existDog(cell.state)) {
            eval -= DEATH;
        }

        int aliveCellCount = getAliveCellCount(ninja.y, ninja.x);

        if (aliveCellCount <= 2) {
            eval -= (400000 - aliveCellCount);
        } else if (aliveCellCount <= 4) {
            eval -= (200000 - aliveCellCount);
        } else if (aliveCellCount <= 10) {
            eval -= (1000 - aliveCellCount);
        } else {
            eval += aliveCellCount / 10;
        }

        return eval;
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

            removeStone(ny, nx);
            setStone(nny, nnx);
        }
    }

    /**
     * 忍者を実際に行動させてそれを評価する。
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
                int ny = ninja.y + DY[direct];
                int nx = ninja.x + DX[direct];
                Cell toCell = this.field[ny][nx];
                boolean moveStone = Field.existStone(toCell.state);

                // 実際に移動する
                move(ninja, direct);

                // ニンジャソウルをゲット出来たかどうかを調べる
                if (Field.existSoul(toCell.state)) {
                    info.getSoulCount += 1;
                    removeSoul(ny, nx);
                    // 次に狙うニンジャソウルの場所を決める
                    updateTargetSoul(ninja);
                }
                // 石を動かした場合
                if (moveStone) {
                    info.moveStone = true;

                    // 同じ方向にもう一度進めなくなった場合
                    if (!canMove(ny, nx, direct)) {
                        info.notMoveNextCell = true;
                    }
                }
            } else {
                info.valid = false;
                break;
            }
        }

        int nid = ninja.getNID();
        int targetDist = 0;

        if (ninja.targetId != -1) {
            Cell cell = getCell(ninja.targetId);
            info.targetId = ninja.targetId;
            targetDist = this.eachCellDist[nid][cell.id];

            if (targetDist == INF) {
                //info.unreach = true;
            }
        }

        info.ninjaY = ninja.y;
        info.ninjaX = ninja.x;
        info.targetDist = targetDist;
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

    public void clearDogValue() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];
                cell.dogValue = 0;
            }
        }
    }

    /**
     * 忍犬の密集度を調べる
     */
    public void updateDogValue() {
        for (Dog dog : this.dogList) {
            Queue<Integer> queueX = new LinkedList<Integer>();
            Queue<Integer> queueY = new LinkedList<Integer>();
            Queue<Integer> queueD = new LinkedList<Integer>();

            queueY.add(dog.y);
            queueX.add(dog.x);
            queueD.add(0);

            boolean[][] checkList = new boolean[Field.HEIGHT][Field.WIDTH];
            for (int y = 0; y < Field.HEIGHT; y++) {
                Arrays.fill(checkList[y], false);
            }

            while (!queueX.isEmpty()) {
                int curY = queueY.poll();
                int curX = queueX.poll();
                int dist = queueD.poll();
                Cell cell = this.field[curY][curX];

                if (dist > 10) continue;
                if (checkList[curY][curX]) continue;
                checkList[curY][curX] = true;
                cell.dogValue += 10 - dist;
                cell.dogDist = Math.min(cell.dogDist, dist);

                if (dist <= 2) {
                    cell.dogValue += 5 * (3 - dist);
                }

                for (int i = 0; i < 4; i++) {
                    int ny = curY + DY[i];
                    int nx = curX + DX[i];
                    Cell ncell = this.field[ny][nx];

                    if (Field.isDogMovableObject(ncell.state)) {
                        queueY.add(ny);
                        queueX.add(nx);
                        queueD.add(dist + 1);
                    } else if (dist == 0) {
                        ncell.dogValue += 10;
                        ncell.dogDist = 1;
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void updateSoulValue() {
        for (NinjaSoul soul : this.soulList) {
            for (int y = 1; y < Field.HEIGHT; y++) {
                for (int x = 1; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];
                    int dist = this.eachCellDist[cell.id][soul.sid];

                    if (dist <= 15) {
                        cell.soulValue += 15 - dist;
                    }
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
        int minY = -1;
        int minX = -1;

        for (Ninja ninja : this.ninjaList) {
            for (int i = 0; i < 4; i++) {
                int ny = ninja.y + DY[i];
                int nx = ninja.x + DX[i];
                Cell cell = this.field[ny][nx];

                if (Field.existFixStone(cell.state)) {
                    minY = ny;
                    minX = nx;
                    break;
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
    public void fallRockAttack(PlayerInfo enemy, CommandList commandList) {
        int minEval = Integer.MAX_VALUE;
        int minY = -1;
        int minX = -1;

        for (Ninja ninja : enemy.ninjaList) {
            // 相手の忍者がいま選択できる一番良い行動を調べる
            ActionInfo baseAction = enemy.getMaxNinjaEval(ninja);

            if (baseAction.commandList == "N") continue;
            int basicEval = baseAction.toEval();

            for (int y = -3; y <= 3; y++) {
                for (int x = -3; x <= 3; x++) {
                    int ny = ninja.y + y;
                    int nx = ninja.x + x;

                    if (isOutside(ny, nx)) continue;

                    Cell cell = enemy.field[ny][nx];
                    if (Field.isWall(cell.state)) continue;

                    if (Field.isStonePuttable(cell.state)) {
                        enemy.setStone(ny, nx);

                        ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);
                        int diff = (bestAction.toEval()) - basicEval;

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
     * 相手の嫌なところに岩を落とす
     */
    public void fallRockAttackEasy(PlayerInfo enemy, CommandList commandList) {
        int minEval = Integer.MAX_VALUE;
        int minY = -1;
        int minX = -1;

        for (Ninja ninja : enemy.ninjaList) {
            ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

            if (bestAction.toEval() <= 300) {
                continue;
            }

            for (int y = -3; y <= 3; y++) {
                for (int x = -3; x <= 3; x++) {
                    int ny = ninja.y + y;
                    int nx = ninja.x + x;

                    if (isOutside(ny, nx)) continue;

                    Cell cell = enemy.field[ny][nx];
                    if (Field.isWall(cell.state)) continue;

                    if (Field.isStonePuttable(cell.state)) {
                        enemy.setStone(ny, nx);
                        int noMoveObj = getNoMoveCount(ninja.y, ninja.x);
                        ActionInfo info = enemy.moveAction(ninja, bestAction.commandList);

                        enemy.updateDogPosition(info.moveStone, false);
                        enemy.clearDogValue();
                        enemy.updateDogValue();

                        info.positionValue = enemy.calcPositionEval(ninja);
                        int eval = info.toEval() - bestAction.toEval();

                        if (minEval > eval && noMoveObj <= 3) {
                            minEval = eval;
                            minY = ny;
                            minX = nx;
                        }

                        enemy.removeStone(ny, nx);
                        enemy.tempRollbackField();
                        enemy.tempRollbackNinja();
                        enemy.rollbackDogStatus();
                    }
                }
            }
        }

        if (minY != -1 && minEval < -300000) {
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
            int dist = this.eachCellDistDogBlock[soul.sid][id];
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

    public void saveDogFirst() {
        for (Dog dog : this.dogList) {
            dog.saveFirst();
        }
    }

    public void rollbackDogFirst() {
        for (Dog dog : this.dogList) {
            dog.rollbackFirst();
        }
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

    public void saveSoulStatus() {
        for (NinjaSoul soul : this.soulList) {
            soul.saveStatus();
        }
    }

    public void rollbackSoulStatus() {
        for (NinjaSoul soul : this.soulList) {
            soul.rollback();
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
                cell.save();
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
                cell.rollback();
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
                cell.tempSave();
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
                cell.tempRollback();
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

    public void tempSaveDog() {
        for (Dog dog : this.dogList) {
            dog.tempSave();
        }
    }

    public void tempRollbackDog() {
        for (Dog dog : this.dogList) {
            dog.tempRollback();
        }
    }

    /**
     * 周りの石か壁の数を数える
     *
     * @param y
     * @param x
     * @return
     */
    public int getNoMoveCount(int y, int x) {
        int cnt = 0;

        for (int i = 0; i < 4; i++) {
            int ny = y + DY[i];
            int nx = x + DX[i];

            if (isOutside(ny, nx)) continue;
            Cell cell = this.field[ny][nx];

            if (Field.existStone(cell.state) || Field.isWall(cell.state)) {
                cnt++;
            }
        }

        return cnt;
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
