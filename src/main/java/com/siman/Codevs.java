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

    public final int DY[] = {1, 0, -1, 0};
    public final int DX[] = {0, 1, 0, -1};
    public final char DS[] = {'L', 'U', 'R', 'D'};

    /**
     * プレイヤーリスト
     */
    public PlayerInfo[] playerInfoList;

    /**
     * 忍者リスト
     */
    public Ninja[][] ninjaList;

    /**
     * 忍犬リスト
     */
    public Dog[][] dogList;

    /**
     * ニンジャソウルのリスト
     */
    public NinjaSoul[][] ninjaSoulList;

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

    /**
     * フィールド (playerId, y, x)
     */
    public int[][][] field;


    public void Codevs() {
    }

    /**
     * ゲームの初期化処理を行う
     */
    public void init() {
        initPlayer();
        initNinja();
        initDogList();
        initNinjaSoulList();
        initField();
    }

    public void initPlayer() {
        this.playerInfoList = new PlayerInfo[PLAYER_NUM];

        for (int i = 0; i < PLAYER_NUM; i++) {
            this.playerInfoList[i] = new PlayerInfo();
        }
    }

    public void initNinja() {
        this.ninjaList = new Ninja[PLAYER_NUM][NINJA_NUM];

        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            for (int i = 0; i < NINJA_NUM; i++) {
                this.ninjaList[playerId][i] = new Ninja();
            }
        }
    }

    public void initDogList() {
        this.dogList = new Dog[PLAYER_NUM][MAX_DOG_NUM];

        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            for (int i = 0; i < MAX_DOG_NUM; i++) {
                this.dogList[playerId][i] = new Dog();
            }
        }
    }

    public void initNinjaSoulList() {
        this.ninjaSoulList = new NinjaSoul[PLAYER_NUM][MAX_SOUL_NUM];

        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            for (int i = 0; i < MAX_SOUL_NUM; i++) {
                this.ninjaSoulList[playerId][i] = new NinjaSoul();
            }
        }
    }

    public void initField() {
        this.field = new int[PLAYER_NUM][][];
    }

    /**
     * ターンの最初に与えられる情報を読み込む
     *
     * @param sc 入力元
     */
    public void readTurnInfo(Scanner sc) {
        StringBuilder res = new StringBuilder();
        this.remainTime = sc.nextLong();
        this.skills = sc.nextInt();
        this.skillCost = new int[this.skills];

        for (int i = 0; i < this.skills; i++) {
            this.skillCost[i] = sc.nextInt();
        }

        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];

            playerInfo.soulPower = sc.nextInt();
            this.height = sc.nextInt();
            this.width = sc.nextInt();
            this.field[playerId] = new int[this.height][this.width];
            for(int y = 0; y < this.height; y++){
                Arrays.fill(this.field[playerId][y], 0);
            }

            for (int y = 0; y < this.height; y++) {
                String line = sc.next();

                for (int x = 0; x < this.width; x++) {
                    char type = line.charAt(x);
                    this.field[playerId][y][x] |= Field.toInteger(type);
                }
            }

            int ninjaCount = sc.nextInt();
            for (int ninjaId = 0; ninjaId < ninjaCount; ninjaId++) {
                int id = sc.nextInt();
                this.ninjaList[playerId][id].y = sc.nextInt();
                this.ninjaList[playerId][id].x = sc.nextInt();
            }

            playerInfo.dogCount = sc.nextInt();
            for (int i = 0; i < playerInfo.dogCount; i++) {
                int dogId = sc.nextInt();
                this.dogList[playerId][dogId].y = sc.nextInt();
                this.dogList[playerId][dogId].x = sc.nextInt();
            }

            playerInfo.soulCount = sc.nextInt();
            for (int i = 0; i < playerInfo.soulCount; i++) {
                this.ninjaSoulList[playerId][i].y = sc.nextInt();
                this.ninjaSoulList[playerId][i].x = sc.nextInt();
            }

            /**
             * スキルの使用回数をセット
             */
            for (int i = 0; i < this.skills; i++) {
                playerInfo.useSkill[i] = sc.nextInt();
            }
        }
    }
}
