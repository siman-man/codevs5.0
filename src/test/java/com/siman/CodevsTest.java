package test.java.com.siman;

import main.java.com.siman.Codevs;
import main.java.com.siman.NinjaSkill;
import main.java.com.siman.Player;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by siman on 3/1/16.
 */
public class CodevsTest {
    /**
     * ターン情報を読み込めてるかどうかをテスト
     */
    @Test
    public void testReadTurnInfo() throws Exception {
        Codevs codevs = new Codevs();
        codevs.init();
        File file = new File("src/test/resources/sample.in");
        Scanner sc = new Scanner(file);

        codevs.readTurnInfo(sc);

        assertThat(codevs.remainTime, is(299987L));
        assertThat(codevs.skillCost[NinjaSkill.SUPER_HIGH_SPEED], is(4));
        assertThat(codevs.skillCost[NinjaSkill.MY_ROCKFALL], is(6));
        assertThat(codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL], is(6));
        assertThat(codevs.skillCost[NinjaSkill.MY_LIGHTNING_ATTACK], is(6));
        assertThat(codevs.skillCost[NinjaSkill.ENEMY_LIGHTNING_ATTACK], is(4));
        assertThat(codevs.skillCost[NinjaSkill.MY_AVATAR], is(4));
        assertThat(codevs.skillCost[NinjaSkill.ENEMY_AVATAR], is(4));
        assertThat(codevs.skillCost[NinjaSkill.ROTATION_ZAN], is(25));

        Player my = codevs.playerList[Codevs.MY_ID];
        assertThat(my.soulPower, is(6));
        assertThat(my.useSkill[NinjaSkill.SUPER_HIGH_SPEED], is(0));
        assertThat(my.useSkill[NinjaSkill.ENEMY_ROCKFALL], is(2));

        Player enemy = codevs.playerList[Codevs.ENEMY_ID];
        assertThat(enemy.soulPower, is(4));
        assertThat(enemy.useSkill[NinjaSkill.SUPER_HIGH_SPEED], is(0));
        assertThat(enemy.useSkill[NinjaSkill.ENEMY_ROCKFALL], is(0));
    }
}