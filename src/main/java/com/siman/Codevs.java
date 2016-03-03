package main.java.com.siman;

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
     * 忍犬の最大値
     */
    public static int MAX_DOG_NUM = 255;

    /**
     * ニンジャソウルの最大数
     */
    public static int MAX_SOUL_NUM = 255;

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

    public final int DY[] = {-1, 0, 1, 0};
    public final int DX[] = {0, 1, 0, -1};
    public final char DS[] = {'L', 'U', 'R', 'D'};

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
    public int[] skillCost;

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
        initDogList();
        initNinjaSoulList();
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
     * 忍犬情報の初期化を行う
     */
    public void initDogList() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.dogList = new Dog[MAX_DOG_NUM];

            for (int i = 0; i < MAX_DOG_NUM; i++) {
                playerInfo.dogList[i] = new Dog();
            }
        }
    }

    /**
     * ニンジャソウル情報の初期化を行う
     */
    public void initNinjaSoulList() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.soulList = new NinjaSoul[MAX_SOUL_NUM];

            for (int i = 0; i < MAX_SOUL_NUM; i++) {
                playerInfo.soulList[i] = new NinjaSoul();
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
                    int id = (y * Field.WIDTH) + x;
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
        System.err.println(String.format("turn %d", this.turn));
        this.remainTime = sc.nextLong();
        System.err.println(String.format("remainTime = %d", this.remainTime));
        this.skills = sc.nextInt();
        System.err.println(String.format("skills = %d", this.skills));
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

                ninja.y = ninjaY;
                ninja.x = ninjaX;
                ninja.saveStatus();

                player.field[ninjaY][ninjaX].state |= (ninjaId == 0) ? Field.NINJA_A : Field.NINJA_B;
            }

            player.dogCount = sc.nextInt();
            for (int i = 0; i < player.dogCount; i++) {
                int dogId = sc.nextInt();
                int dogY = sc.nextInt();
                int dogX = sc.nextInt();

                player.dogList[dogId].y = dogY;
                player.dogList[dogId].x = dogX;
                player.dogList[dogId].update_at = this.turn;
                player.field[dogY][dogX].state |= Field.DOG;
            }

            player.soulCount = sc.nextInt();
            for (int i = 0; i < player.soulCount; i++) {
                int soulY = sc.nextInt();
                int soulX = sc.nextInt();

                player.soulList[i].y = soulY;
                player.soulList[i].x = soulX;
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
    public void beforeProc() {
        PlayerInfo my = this.playerInfoList[MY_ID];
        my.saveField();
        my.updateStoneStatus();
        my.updateEachCellDist();
        my.setTargetSoulId();
        my.updateDangerValue();

        PlayerInfo enemy = this.playerInfoList[ENEMY_ID];
        enemy.saveField();
        enemy.updateStoneStatus();
        enemy.updateEachCellDist();
        enemy.updateDangerValue();
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
    public void output(ActionInfo[] actions) {
        System.out.println("2");

        for(ActionInfo action : actions) {
            String res = "";

            for(char command : action.commandList) {
                res += command;
            }

            System.out.println(res);
        }

        System.out.flush();
    }
}
