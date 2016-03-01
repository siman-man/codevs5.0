package main.java.com.siman;

/**
 * Created by siman on 3/1/16.
 */
public class Player {
    public int soulPower;

    public int[] useSkill;

    public Player() {
        this.useSkill = new int[Codevs.MAX_SKILL_COUNT];
    }
}
