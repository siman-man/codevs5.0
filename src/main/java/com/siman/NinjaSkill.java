package main.java.com.siman;

/**
 * Created by siman on 3/2/16.
 */
public class NinjaSkill {
    public static int SUPER_HIGH_SPEED = 0;
    public static int MY_ROCKFALL = 1;
    public static int ENEMY_ROCKFALL = 2;
    public static int MY_LIGHTNING_ATTACK = 3;
    public static int ENEMY_LIGHTNING_ATTACK = 4;
    public static int MY_AVATAR = 5;
    public static int ENEMY_AVATAR = 6;
    public static int ROTATION_ZAN = 7;

    public static String fallrockEnemy(int y, int x) {
        return String.format("%d %d %d", ENEMY_ROCKFALL, y, x);
    }

    public static String summonMyAvator(int y, int x) {
        return String.format("%d %d %d", MY_AVATAR, y, x);
    }

    public static String summonEnemyAvator(int y, int x) {
        return String.format("%d %d %d", ENEMY_AVATAR, y, x);
    }

    public static String breakMyStone(int y, int x) {
        return String.format("%d %d %d", MY_LIGHTNING_ATTACK, y, x);
    }
}
