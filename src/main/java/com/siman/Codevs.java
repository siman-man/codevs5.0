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
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.ninjaList = new Ninja[NINJA_NUM];

            for (int i = 0; i < NINJA_NUM; i++) {
                playerInfo.ninjaList[i] = new Ninja();
            }
        }
    }

    public void initDogList() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.dogList = new Dog[MAX_DOG_NUM];

            for (int i = 0; i < MAX_DOG_NUM; i++) {
                playerInfo.dogList[i] = new Dog();
            }
        }
    }

    public void initNinjaSoulList() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.soulList = new NinjaSoul[MAX_SOUL_NUM];

            for (int i = 0; i < MAX_SOUL_NUM; i++) {
                playerInfo.soulList[i] = new NinjaSoul();
            }
        }
    }

    public void initField() {
        for (int playerId = 0; playerId < PLAYER_NUM; playerId++) {
            PlayerInfo playerInfo = this.playerInfoList[playerId];
            playerInfo.field = new Cell[Field.HEIGHT][Field.WIDTH];

            for (int y = 0; y < Field.HEIGHT; y++) {
                for (int x = 0; x < Field.WIDTH; x++) {
                    playerInfo.field[y][x] = new Cell();
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

            for (int y = 0; y < this.height; y++) {
                String line = sc.next();

                for (int x = 0; x < this.width; x++) {
                    char type = line.charAt(x);
                    Cell cell = playerInfo.field[y][x];
                    cell.clear();

                    cell.state |= Field.toInteger(type);
                }
            }

            int ninjaCount = sc.nextInt();
            for (int ninjaId = 0; ninjaId < ninjaCount; ninjaId++) {
                int id = sc.nextInt();
                playerInfo.ninjaList[id].y = sc.nextInt();
                playerInfo.ninjaList[id].x = sc.nextInt();
            }

            playerInfo.dogCount = sc.nextInt();
            for (int i = 0; i < playerInfo.dogCount; i++) {
                int dogId = sc.nextInt();
                playerInfo.dogList[dogId].y = sc.nextInt();
                playerInfo.dogList[dogId].x = sc.nextInt();
            }

            playerInfo.soulCount = sc.nextInt();
            for (int i = 0; i < playerInfo.soulCount; i++) {
                playerInfo.soulList[i].y = sc.nextInt();
                playerInfo.soulList[i].x = sc.nextInt();
            }

            /**
             * スキルの使用回数をセット
             */
            for (int i = 0; i < this.skills; i++) {
                playerInfo.useSkill[i] = sc.nextInt();
            }
        }
    }

    public boolean canMove(int playerId, int y, int x, int direct) {
        PlayerInfo player = this.playerInfoList[playerId];
        Cell[][] field = player.field;
        int ny = y + DY[direct];
        int nx = x + DX[direct];

        // 床であれば無条件で移動出来る
        if (Field.isFloor(field[ny][nx].state)) return true;
        // 壁は移動出来ない
        if (Field.isWall(field[ny][nx].state)) return false;

        // 石の場合は次の座標を見て、石を押せるかどうかを判定する
        if (Field.isStone(field[ny][nx].state)) {
            int nny = ny + DY[direct];
            int nnx = nx + DX[direct];

            return Field.isMovableObject(field[nny][nnx].state);
        } else {
            return true;
        }
    }
}