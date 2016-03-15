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

    public int playerId;

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

    public boolean summonsEnemyAvator;

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
    }

    public boolean BAction;
    public boolean AAction;
    public boolean EAAction;
    public boolean EBAction;


    /**
     * 自身の情報をクリアする
     */
    public void clean() {
        this.AAction = false;
        this.BAction = false;
        this.EAAction = false;
        this.EBAction = false;
        this.highSpeedMode = false;
        this.summonsAvator = false;
        this.summonsEnemyAvator = false;
        this.avatorId = -1;
    }

    /**
     * 忍術を使用する
     */
    public void spell(PlayerInfo enemy, CommandList commandList) {
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int dogCntA = getNearDogCount(ninjaA.y, ninjaA.x);
        int dogCntB = getNearDogCount(ninjaB.y, ninjaB.x);
        int minDogCnt = Math.max(dogCntA, dogCntB);


        int aliveA = getAliveCellCount(ninjaA.y, ninjaA.x);
        int aliveB = getAliveCellCount(ninjaB.y, ninjaB.x);
        int minAliveCnt = Math.min(aliveA, aliveB);

        ActionInfo bestActionA = getMaxNinjaEval(ninjaA);
        ActionInfo bestActionB = getMaxNinjaEval(ninjaB);

        int avatorCost = Codevs.skillCost[NinjaSkill.MY_AVATAR] + 4;
        int rockCost = Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL];

        for (Ninja ninja : this.ninjaList) {
            int aliveCount = getAliveCellCount(ninja.y, ninja.x);

            if (aliveCount <= 2 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_AVATAR]) {
                summonMyAvator(commandList);

                if (commandList.useSkill) {
                    return;
                }
            }
        }

        if (minDogCnt >= avatorCost && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_AVATAR]) {
            summonMyAvator(commandList);

            if (commandList.useSkill) {
                return;
            }
        }

        if (this.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_AVATAR]) {
            summonEnemyAvator(enemy, commandList);

            if (commandList.useSkill) {
                return;
            }
        }

        if (minAliveCnt > avatorCost && this.soulPower >= rockCost) {
            /*
            fallRockAttack(enemy, commandList);

            if (commandList.eval >= 300000) {
                commandList.useSkill = true;
                return;
            }
            */

            if (this.soulPower >= rockCost) {
                fallRockAttackEasy(enemy, commandList);

                if (commandList.eval >= 300000) {
                    commandList.useSkill = true;
                    return;
                }
                if (rockCost <= 4 && this.soulPower >= 12 && commandList.eval >= 200000) {
                    commandList.useSkill = true;
                    return;
                }
            }
        }

        int zanCost = Codevs.skillCost[NinjaSkill.ROTATION_ZAN];

        for (Ninja ninja : this.ninjaList) {
            int circleDogCount = getCircleDogCount(ninja.y, ninja.x);

            if (this.soulPower >= zanCost && zanCost <= 10 && circleDogCount >= 3) {
                commandList.useSkill = true;
                commandList.spell = String.format("%d %d", NinjaSkill.ROTATION_ZAN, ninja.id);
                return;
            }
        }

        if (this.soulPower >= 50 && Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK] <= 4 && this.soulPower >= Codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK]) {
            breakFixStone(commandList);

            if (commandList.useSkill) {
                return;
            }
        }

        if (this.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_AVATAR] && enemy.useSkill[NinjaSkill.MY_AVATAR] > 0) {
            summonEnemyAvatorSoul(enemy, commandList);

            if (commandList.useSkill) {
                return;
            }
        }

        if (false && Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] <= 2 && this.soulPower >= Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED]) {
            if (Codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED] == 2 && this.soulPower <= 30) {
                return;
            }
            commandList.useSkill = true;
            commandList.spell = "0";
            this.highSpeedMode = true;
        }
    }

    public void cleanStone(Ninja ninja, String commands, CommandList commandList, boolean getSoul) {
        int[] FY = new int[6];
        int[] FX = new int[6];
        int[] NY = new int[3];
        int[] NX = new int[3];
        int[] DD = new int[2];
        int SECOND_FAILED = 0;
        int FIRST_FAILED = 1;

        if (!getSoul && (commandList.useSkill || this.summonsAvator || this.summonsEnemyAvator)) return;

        int i = 0;
        int ninjaY = ninja.originY;
        int ninjaX = ninja.originX;

        for (char command : commands.toCharArray()) {
            int direct = Direction.toInteger(command);

            DD[i] = direct;
            FY[i * 3] = ninjaY + DY[direct];
            FX[i * 3] = ninjaX + DX[direct];
            FY[i * 3 + 1] = ninjaY + 2 * DY[direct];
            FX[i * 3 + 1] = ninjaX + 2 * DX[direct];
            FY[i * 3 + 2] = ninjaY + 3 * DY[direct];
            FX[i * 3 + 2] = ninjaX + 3 * DX[direct];


            ninjaY = ninjaY + DY[direct];
            ninjaX = ninjaX + DX[direct];

            if (i == 0) {
                // 2回目が失敗したときの座標
                NY[0] = ninjaY;
                NX[0] = ninjaX;
            } else if (i == 1) {
                // 1回目が失敗したときの座標
                NY[1] = ninja.originY + DY[direct];
                NX[1] = ninja.originX + DX[direct];
            } else {
                // 全部失敗したときの座標
                NY[2] = ninja.originY;
                NX[2] = ninja.originX;
            }

            i++;
        }

        // 1回目が失敗
        int fy = NY[FIRST_FAILED];
        int fx = NX[FIRST_FAILED];
        Cell fCell = this.field[fy][fx];
        Cell oCell = this.field[ninja.originY][ninja.originX];

        // 2回目の行動の時の座標に犬がいる
        if ((canMove(ninja.originY, ninja.originX, DD[1]) && fCell.dogDist <= 1) || oCell.dogDist <= 1) {
            if (isInside(FY[0], FX[0]) && isInside(FY[1], FX[1])) {
                Cell f0Cell = this.field[FY[0]][FX[0]];
                Cell f1Cell = this.field[FY[1]][FX[1]];

                if (Field.existStone(f0Cell.state) && Field.isStonePuttable(f1Cell.state)) {
                    commandList.useSkill = true;
                    commandList.spell = NinjaSkill.breakMyStone(f0Cell.y, f0Cell.x);
                    return;
                }

                if (Field.existStone(f1Cell.state) && Field.isStonePuttable(f0Cell.state)) {
                    commandList.useSkill = true;
                    commandList.spell = NinjaSkill.breakMyStone(f1Cell.y, f1Cell.x);
                    return;
                }

                if (isInside(FY[2], FX[2])) {
                    Cell f2Cell = this.field[FY[2]][FX[2]];

                    if (Field.existStone(f0Cell.state) && Field.isStonePuttable(f2Cell.state)) {
                        commandList.useSkill = true;
                        commandList.spell = NinjaSkill.breakMyStone(f0Cell.y, f0Cell.x);
                        return;
                    }
                }
            }
        }

        // 2回目が失敗
        int sy = NY[SECOND_FAILED];
        int sx = NX[SECOND_FAILED];
        Cell sCell = this.field[sy][sx];

        // 1回目の行動の時の座標に犬がいる
        if (canMove(ninja.originY, ninja.originX, DD[0]) && sCell.dogDist <= 1) {
            if (isInside(FY[3], FX[3]) && isInside(FY[4], FX[4])) {
                Cell f3Cell = this.field[FY[3]][FX[3]];
                Cell f4Cell = this.field[FY[4]][FX[4]];

                if (Field.existStone(f3Cell.state) && Field.isStonePuttable(f4Cell.state)) {
                    commandList.useSkill = true;
                    commandList.spell = NinjaSkill.breakMyStone(f3Cell.y, f3Cell.x);
                    return;
                }

                if (Field.existStone(f4Cell.state) && Field.isStonePuttable(f3Cell.state)) {
                    commandList.useSkill = true;
                    commandList.spell = NinjaSkill.breakMyStone(f4Cell.y, f4Cell.x);
                    return;
                }
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
                int eval = distA + distB - cell.soulValue;
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

            enemy.updateDogPosition(false, false);

            int safePointCount = enemy.getSafePointCount(ninja);

            if (bestAction.commandList != "N") {
                continue;
            } else if (safePointCount == 0 || enemy.useSkill[NinjaSkill.MY_AVATAR] == 0){
                this.summonsEnemyAvator = true;
                commandList.useSkill = true;
                commandList.spell = NinjaSkill.summonEnemyAvator(ninja.y, ninja.x);
                return;
            } else {
                int maxAlive = Integer.MIN_VALUE;
                int maxY = -1;
                int maxX = -1;

                for (int y = -2; y <= 2; y++) {
                    for (int x = -2; x <= 2; x++) {
                        int ny = ninja.y + y;
                        int nx = ninja.x + x;

                        if (isOutside(ny, nx)) continue;
                        Cell cell = enemy.field[ny][nx];

                        if (Field.existSolidObject(cell.state)) continue;
                        if (calcManhattanDist(ninja.y, ninja.x, ny, nx) > 2) continue;
                        int aliveCount = enemy.getAliveCellCount(ny, nx);

                        if (maxAlive < aliveCount) {
                            maxAlive = aliveCount;
                            maxY = ny;
                            maxX = nx;
                        }
                    }
                }

                if (maxY != -1) {
                    this.summonsEnemyAvator = true;
                    commandList.useSkill = true;
                    commandList.spell = NinjaSkill.summonEnemyAvator(maxY, maxX);
                }
            }

            enemy.rollbackField();
            enemy.rollbackNinja();
            enemy.rollbackDogStatus();
        }
    }

    public void summonEnemyAvatorSoul(PlayerInfo enemy, CommandList commandList) {
        for (NinjaSoul soul : enemy.soulList) {
            Cell cell = enemy.field[soul.y][soul.x];

            if (soul.ninjaDist == 2 && cell.dogDist <= 1) {
                this.summonsEnemyAvator = true;
                commandList.useSkill = true;
                commandList.spell = NinjaSkill.summonEnemyAvator(soul.y, soul.x);
                return;
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

        int dogCountA = getNearDogCount(ninjaA.y, ninjaA.x);
        int dogCountB = getNearDogCount(ninjaB.y, ninjaB.x);
        int minDogCnt = Math.max(dogCountA, dogCountB);

        this.AAction = true;

        ActionInfo bestActionA = getBestAction(ninjaA);

        this.AAction = false;

        if ((dogCountA >= 4 || bestActionA.getSoulCountFirst > 0) && enemy.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL]) {
            cleanStone(ninjaA, bestActionA.commandList, commandList, bestActionA.getSoulCountFirst > 0);
        }

        moveAction(ninjaA, bestActionA.commandList);
        updateEachCellDist();

        this.BAction = true;

        saveField();
        saveNinjaStatus();
        saveSoulStatus();


        ActionInfo bestActionB = getBestAction(ninjaB);

        this.BAction = false;

        ActionInfo[] result = new ActionInfo[]{bestActionA, bestActionB};

        if ((dogCountB >= 4 || bestActionB.getSoulCountFirst > 0) && enemy.soulPower >= Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL]) {
            cleanStone(ninjaB, bestActionB.commandList, commandList, bestActionB.getSoulCountFirst > 0);
        }

        return result;
    }

    public ActionInfo getBestAction(Ninja ninja) {
        ActionInfo bestAction = new ActionInfo();
        int maxEval = Integer.MIN_VALUE;
        int beamWidth = 1000;
        int limitDepth = 3;

        clearSoulValue();
        updateSoulValue();

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

                    if (info.isValid()) {
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
                        if (depth == 0) {
                            info.getSoulCountFirst = info.getSoulCount;
                        }
                        nextNode.info = info;

                        if (depth == 0) {
                            nextNode.eval = node.eval + info.toEval(this.playerId);
                        } else {
                            nextNode.eval = node.eval + info.toEval(this.playerId);
                        }
                        nextNode.actionHistory = new ArrayList(node.actionHistory);
                        nextNode.actionHistory.add(action);

                        if (depth == limitDepth - 1 && maxEval < nextNode.eval) {
                            maxEval = nextNode.eval;
                            bestAction = info;
                            bestAction.commandList = nextNode.actionHistory.get(0);
                        } else if (info.toEval(this.playerId) > -DEATH / 2){
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

    public int getSafePointCount(Ninja ninja) {
        int safePointCount = 0;

        Queue<Integer> queueX = new LinkedList<Integer>();
        Queue<Integer> queueY = new LinkedList<Integer>();
        Queue<Integer> queueD = new LinkedList<Integer>();

        queueY.add(ninja.y);
        queueX.add(ninja.x);
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

            if (checkList[curY][curX]) continue;
            if (dist > 2) continue;

            checkList[curY][curX] = true;

            if (dist > 1 && !Field.existDog(cell.state) && !Field.existSolidObject(cell.state)) {
                safePointCount++;
            }

            for (int i = 0; i < 4; i++) {
                int ny = curY + DY[i];
                int nx = curX + DX[i];

                if (canMove(curY, curX, i)) {
                    queueY.add(ny);
                    queueX.add(nx);
                    queueD.add(dist + 1);
                }
            }
        }

        return safePointCount;
    }

    public ActionInfo getMaxNinjaEval(Ninja ninja) {
        String hash = "";
        int maxEval = -DEATH / 3;
        ActionInfo bestAction = new ActionInfo();
        int alivePathCount = 0;

        for (String action : Ninja.NORMAL_MOVE_PATTERN) {
            ActionInfo info = moveAction(ninja, action);

            if (info.isValid()) {
                if (this.playerId == Codevs.MY_ID) {
                    updateDogPosition(info.moveStone, this.summonsAvator);
                } else {
                    updateDogPosition(info.moveStone, false);
                }
                clearDogValue();
                updateDogValue();

                info.positionValue = calcPositionEval(ninja);
                int eval = info.toEval(this.playerId);

                if (info.toEval(this.playerId) > -300000) {
                    alivePathCount++;
                    String ch = String.valueOf(action.charAt(0));

                    if (!hash.contains(ch)) {
                        hash += ch;
                    }
                }

                if (maxEval < eval) {
                    maxEval = eval;
                    bestAction = info;
                }
            }

            rollbackField();
            rollbackNinja();
            rollbackDogStatus();
        }

        //bestAction.alivePathCount = alivePathCount;
        bestAction.alivePathCount = hash.length();

        return bestAction;
    }

    public ActionInfo beamSearch(Ninja ninja) {
        ActionInfo bestAction = new ActionInfo();

        return bestAction;
    }

    /**
     * 次に狙うニンジャソウルの場所を決める
     *
     * @param ninja
     */
    public void updateTargetSoul(Ninja ninja) {
        for (NinjaSoul soul : this.soulList) {
            if (ninja.targetId == soul.sid) {
                soul.exist = false;
                continue;
            }
            if (!soul.exist) continue;
        }
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
            if (this.playerId == Codevs.MY_ID && summon) {
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
            if (dist > 1 && dist / 2 >= cell.dogDist) continue;
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

        return Math.min(aliveCellCount, INF);
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

    /**
     * 自分の周りにある犬の数を調べる
     *
     * @param y
     * @param x
     * @return
     */
    public int getAroundDogCount(int y, int x) {
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

    public int getNearDogCount(int cy, int cx) {
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

                    if (this.AAction) {
                        if (Field.existNinjaA(cell.state)) {
                            this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                            dog.y = ny;
                            dog.x = nx;
                            this.field[dog.y][dog.x].state |= Field.DOG;
                            break;
                        }
                    } else {
                        if (Field.existNinjaB(cell.state)) {
                            this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                            dog.y = ny;
                            dog.x = nx;
                            this.field[dog.y][dog.x].state |= Field.DOG;
                            break;
                        }
                    }

                    if (this.playerId == Codevs.ENEMY_ID) {
                        if (Field.existNinjaA(cell.state)) {
                            this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                            dog.y = ny;
                            dog.x = nx;
                            this.field[dog.y][dog.x].state |= Field.DOG;
                            break;
                        }

                        if (Field.existNinjaB(cell.state)) {
                            this.field[dog.y][dog.x].state &= Field.DELETE_DOG;
                            dog.y = ny;
                            dog.x = nx;
                            this.field[dog.y][dog.x].state |= Field.DOG;
                            break;
                        }
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
                        this.field[ny][nx].state |= Field.DOG;

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

        for (int i = 0; i < Field.CELL_COUNT; i++) {
            Arrays.fill(this.eachCellDist[i], INF);
            Arrays.fill(this.eachCellDistNonPush[i], INF);
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

                            if (Field.existSolidObject(nCell.state)) {
                                this.eachCellDist[fromCell.id][toCell.id] = INF;
                                this.eachCellDist[toCell.id][nCell.id] = INF;
                            } else {
                                this.eachCellDist[fromCell.id][toCell.id] = 1;
                            }

                        } else if (Field.existDog(toCell.state)) {
                            this.eachCellDist[fromCell.id][toCell.id] = 1;
                            this.eachCellDistNonPush[fromCell.id][toCell.id] = 1;
                        } else {
                            this.eachCellDist[fromCell.id][toCell.id] = 1;
                            this.eachCellDistNonPush[fromCell.id][toCell.id] = 1;
                        }
                    } else {
                        this.eachCellDist[fromCell.id][toCell.id] = INF;
                        this.eachCellDistNonPush[fromCell.id][toCell.id] = INF;
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
        int floorCount = getAroundFloorCount(ninja.y, ninja.x);
        int dogCount = getAroundDogCount(ninja.y, ninja.x);
        eval -= cell.dogValue;
        eval += 50 * cell.soulValue;

        if (Field.existDog(cell.state)) {
            eval -= DEATH;
        }

        if (floorCount == 0 && dogCount > 0) {
            eval -= 1000;
        }

        int aliveCellCount = getAliveCellCount(ninja.y, ninja.x);

        if (aliveCellCount <= 2) {
            eval -= (400000 - aliveCellCount);
        } else if (aliveCellCount <= 4) {
            eval -= (200000 - aliveCellCount);
        } else if (aliveCellCount <= 6) {
            eval -= (10000 - aliveCellCount);
        } else if (aliveCellCount <= 20) {
            //eval -= (1000 - aliveCellCount);
        } else {
            //eval += aliveCellCount / 10;
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

        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];

        int nidA = Utility.getId(ninjaA.originY, ninjaA.originX);
        int nidB = Utility.getId(ninjaB.originY, ninjaB.originX);

        for (char command : commandList.toCharArray()) {
            int direct = Direction.toInteger(command);

            if (canMove(ninja.y, ninja.x, direct)) {
                int ny = ninja.y + DY[direct];
                int nx = ninja.x + DX[direct];
                Cell toCell = this.field[ny][nx];
                boolean moveStone = Field.existStone(toCell.state);

                int distA = this.eachCellDist[nidA][toCell.id];
                int distB = this.eachCellDist[nidB][toCell.id];

                // 実際に移動する
                move(ninja, direct);

                // ニンジャソウルをゲット出来たかどうかを調べる
                if (Field.existSoul(toCell.state)) {
                    if ((this.AAction && distA <= distB) || (this.BAction && distB < distA)) {
                        removeSoul(ny, nx);

                        info.getSoulCount += 1;
                        // 次に狙うニンジャソウルの場所を決める
                        updateTargetSoul(ninja);
                    }
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

            int limit = 10;

            boolean[][] checkList = new boolean[Field.HEIGHT][Field.WIDTH];
            for (int y = 0; y < Field.HEIGHT; y++) {
                Arrays.fill(checkList[y], false);
            }

            while (!queueX.isEmpty()) {
                int curY = queueY.poll();
                int curX = queueX.poll();
                int dist = queueD.poll();
                Cell cell = this.field[curY][curX];

                if (dist >= limit) continue;
                if (checkList[curY][curX]) continue;
                checkList[curY][curX] = true;
                cell.dogValue += limit - dist;
                cell.dogDist = Math.min(cell.dogDist, dist);

                if (dist <= 2) {
                    cell.dogValue += 500 * (3 - dist);
                } else if (dist <= 4) {
                    cell.dogValue += 100 * (5 - dist);
                } else {
                    cell.dogValue += limit - dist;
                }

                for (int i = 0; i < 4; i++) {
                    int ny = curY + DY[i];
                    int nx = curX + DX[i];
                    Cell ncell = this.field[ny][nx];

                    if (Field.isDogMovableObject(ncell.state)) {
                        queueY.add(ny);
                        queueX.add(nx);
                        queueD.add(dist + 1);
                    } else if (dist <= 1) {
                        ncell.dogValue += 500 * (3 - dist);
                        ncell.dogDist = Math.min(ncell.dogDist, dist + 1);
                    }
                }
            }
        }
    }

    public void clearSoulValue() {
        for (int y = 0; y < Field.HEIGHT; y++) {
            for (int x = 0; x < Field.WIDTH; x++) {
                Cell cell = this.field[y][x];
                cell.soulValue = 0;
            }
        }
    }

    /**
     *
     */
    public void updateSoulValue() {
        int limit = 10;
        Ninja ninjaA = this.ninjaList[0];
        Ninja ninjaB = this.ninjaList[1];
        int nidA = ninjaA.getNID();
        int nidB = ninjaB.getNID();

        for (NinjaSoul soul : this.soulList) {
            int distA = this.eachCellDist[nidA][soul.sid];
            int distB = this.eachCellDist[nidB][soul.sid];
            soul.ninjaDist = Math.min(distA, distB);

            if (!soul.exist) continue;
            //if (getNearDogCount(soul.y, soul.x) >= 8) continue;

            if (this.AAction && distB < distA) {
                soul.owner = ninjaB.id;
                continue;
            }
            if (this.BAction && distA < distB) {
                soul.owner = ninjaA.id;
                continue;
            }

            for (int y = 1; y < Field.HEIGHT; y++) {
                for (int x = 1; x < Field.WIDTH; x++) {
                    Cell cell = this.field[y][x];
                    int dist = this.eachCellDist[cell.id][soul.sid];

                    if (dist <= limit) {
                        cell.soulValue = Math.min(60, cell.soulValue + (limit - dist));
                    }
                    if (dist <= 1) {
                        cell.soulValue = Math.min(60, cell.soulValue + (limit - dist));
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
    public void fallRockAttackEasy(PlayerInfo enemy, CommandList commandList) {
        int minEval = Integer.MAX_VALUE;
        int minY = -1;
        int minX = -1;

        for (Ninja ninja : enemy.ninjaList) {
            Cell currentCell = enemy.field[ninja.y][ninja.x];

            ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

            if (this.soulPower < 20 && bestAction.toEval(this.playerId) <= 10000 && bestAction.alivePathCount > 1) {
                continue;
            }
            if (bestAction.toEval(this.playerId) <= 500 && bestAction.alivePathCount > 1) {
                continue;
            }
            if (bestAction.commandList == "N") continue;

            if (currentCell.dogDist > 3) continue;


            for (int y = -3; y <= 3; y++) {
                for (int x = -3; x <= 3; x++) {
                    int ny = ninja.y + y;
                    int nx = ninja.x + x;
                    int dist = calcManhattanDist(ninja.y, ninja.x, ny, nx);

                    if (isOutside(ny, nx)) continue;
                    if (dist > 3) continue;

                    Cell cell = enemy.field[ny][nx];
                    if (Field.isWall(cell.state)) continue;

                    if (Field.isStonePuttable(cell.state)) {
                        enemy.setStone(ny, nx);
                        int noMoveObj = enemy.getNoMoveCount(ninja.y, ninja.x);
                        ActionInfo info = enemy.moveAction(ninja, bestAction.commandList);

                        enemy.updateDogPosition(info.moveStone, false);
                        enemy.clearDogValue();
                        enemy.updateDogValue();

                        info.positionValue = enemy.calcPositionEval(ninja);
                        int eval = info.toEval(this.playerId) - bestAction.toEval(this.playerId);

                        if (minEval > eval && noMoveObj <= 3) {
                            minEval = eval;
                            minY = ny;
                            minX = nx;
                        }

                        enemy.removeStone(ny, nx);
                        enemy.rollbackField();
                        enemy.rollbackNinja();
                        enemy.rollbackDogStatus();
                    }
                }
            }
        }

        if (minY != -1 && minEval < -100000) {
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
     * 忍者を元の位置に戻す
     */
    public void rollbackNinja() {
        for (Ninja ninja : this.ninjaList) {
            ninja.rollback();
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
