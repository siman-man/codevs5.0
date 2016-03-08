package main.java.com.siman;

import test.java.com.siman.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by siman on 3/1/16.
 */
public class Codevs {

    /**
     * プレイヤーの数
     */
    public static int PLAYER_NUM = 2;

    /**
     * 忍者の数
     */
    public static int NINJA_NUM = 2;

    /**
     * 忍術の数
     */
    public static int MAX_SKILL_COUNT = 8;

    /**
     * 自分のID
     */
    public static int MY_ID = 0;

    /**
     * 敵のID
     */
    public static int ENEMY_ID = 1;

    /**
     * 現在のターン
     */
    public int turn;

    /**
     * プレイヤーリスト
     */
    public PlayerInfo[] playerInfoList;

    /**
     * 残り時間(msec)
     */
    public long remainTime;

    /**
     * 忍術の数
     */
    public int skills;

    /**
     * 忍術のコスト
     */
    public static int[] skillCost;

    /**
     * フィールドの高さ
     */
    public int height;

    /**
     * フィールドの横幅
     */
    public int width;

    public void Codevs() {
    }

    /**
     * ゲームの初期化処理を行う
     */
    public void init() {
        this.turn = 0;

        initPlayer();
        initNinja();
        initField();
    }

    /**
     * プレイヤー情報の初期化を行う
     */
    public void initPlayer() {
        this.playerInfoList = new PlayerInfo[PLAYER_NUM];

        for (int i = 0; i < PLAYER_NUM; i++) {
            this.playerInfoList[i] = new PlayerInfo();
        }
    }

    /**
     * 忍者情報の初期化を行う
     */
    public void initNinja() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.ninjaList = new Ninja[NINJA_NUM];

            for (int id = 0; id < NINJA_NUM; id++) {
                playerInfo.ninjaList[id] = new Ninja(id);
            }
        }
    }

    /**
     * フィールド情報の初期化を行う
     */
    public void initField() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.field = new Cell[Field.HEIGHT][Field.WIDTH];

            for (int y = 0; y < Field.HEIGHT; y++) {
                for (int x = 0; x < Field.WIDTH; x++) {
                    int id = Utility.getId(y, x);
                    playerInfo.field[y][x] = new Cell(id);
                }
            }
        }
    }

    /**
     * ターンの最初に与えられる情報を読み込む
     *
     * @param sc 入力元
     */
    public void readTurnInfo(Scanner sc) {
        this.turn++;
        this.remainTime = sc.nextLong();
        this.skills = sc.nextInt();
        this.skillCost = new int[this.skills];

        for (int i = 0; i < this.skills; i++) {
            this.skillCost[i] = sc.nextInt();
        }

        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo player = this.playerInfoList[playerId];

            player.soulPower = sc.nextInt();
            this.height = sc.nextInt();
            this.width = sc.nextInt();

            for (int y = 0; y < this.height; y++) {
                String line = sc.next();

                for (int x = 0; x < this.width; x++) {
                    char type = line.charAt(x);
                    Cell cell = player.field[y][x];
                    cell.clear();

                    // 壁以外は全て床属性を持つ
                    if (type != 'W') {
                        cell.state |= Field.FLOOR;
                    }

                    cell.state |= Field.toInteger(type);
                }
            }


            int ninjaCount = sc.nextInt();
            for (int ninjaId = 0; ninjaId < ninjaCount; ninjaId++) {
                int id = sc.nextInt();
                int ninjaY = sc.nextInt();
                int ninjaX = sc.nextInt();

                Ninja ninja = player.ninjaList[id];

                ninja.nid = Utility.getId(ninjaY, ninjaX);
                ninja.y = ninjaY;
                ninja.x = ninjaX;

                player.field[ninjaY][ninjaX].state |= (ninjaId == 0) ? Field.NINJA_A : Field.NINJA_B;
            }

            player.dogCount = sc.nextInt();
            player.dogList = new ArrayList<>();
            for (int id = 0; id < player.dogCount; id++) {
                int dogId = sc.nextInt();
                int dogY = sc.nextInt();
                int dogX = sc.nextInt();

                Dog dog = new Dog(id, dogY, dogX);
                player.dogList.add(dog);
                player.setDog(dogY, dogX);
            }

            player.soulCount = sc.nextInt();
            player.soulList = new ArrayList<>();
            for (int id = 0; id < player.soulCount; id++) {
                int soulY = sc.nextInt();
                int soulX = sc.nextInt();

                NinjaSoul soul = new NinjaSoul(id, soulY, soulX);
                player.soulList.add(soul);
                player.setSoul(soulY, soulX);
            }

            /**
             * スキルの使用回数をセット
             */
            for (int i = 0; i < this.skills; i++) {
                player.useSkill[i] = sc.nextInt();
            }
        }
    }

    /**
     * 思考を始める前に自前に行う処理
     */
    public void beforeProc(CommandList commandList) {
        PlayerInfo my = this.playerInfoList[MY_ID];
        my.clean();
        my.updateStoneStatus();
        my.updateDogValue();
        my.updateEachCellDist();
        my.updateSoulPower();
        my.saveDogStatus();
        my.setTargetSoulId();
        my.saveNinjaStatus();
        my.saveField();

        PlayerInfo enemy = this.playerInfoList[ENEMY_ID];
        enemy.clean();
        enemy.updateStoneStatus();
        enemy.updateDogValue();
        enemy.updateEachCellDist();
        enemy.updateSoulPower();

        enemy.setTargetSoulId();
        enemy.saveNinjaStatus();
        enemy.saveField();
        enemy.saveDogStatus();

        my.spell(enemy, commandList);
    }

    /**
     * アクション
     */
    public ActionInfo[] action() {
        PlayerInfo my = this.playerInfoList[MY_ID];
        ActionInfo[] actions = my.action();

        return actions;
    }

    /**
     * 出力
     */
    public void output(CommandList commandList) {
        if (commandList.useSkill) {
            System.out.println(3);
            System.out.println(commandList.spell);
        } else {
            System.out.println(2);
        }

        for (ActionInfo action : commandList.actions) {
            System.out.println(action.commandList);
        }

        System.out.flush();
    }
}
