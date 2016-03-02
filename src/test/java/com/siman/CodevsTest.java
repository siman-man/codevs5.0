package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Test;
import java.io.File;
import java.util.Scanner;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

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

        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        assertThat(my.soulPower, is(6));
        assertThat(my.dogCount, is(2));
        assertThat(my.useSkill[NinjaSkill.SUPER_HIGH_SPEED], is(0));
        assertThat(my.useSkill[NinjaSkill.ENEMY_ROCKFALL], is(2));

        PlayerInfo enemy = codevs.playerInfoList[Codevs.ENEMY_ID];
        assertThat(enemy.soulPower, is(4));
        assertThat(enemy.dogCount, is(9));
        assertThat(enemy.useSkill[NinjaSkill.SUPER_HIGH_SPEED], is(0));
        assertThat(enemy.useSkill[NinjaSkill.ENEMY_ROCKFALL], is(0));
    }

    /**
     * 移動判定が出来るかどうか
     */
    @Test
    public void testCanMove() throws Exception {
        Codevs codevs = new Codevs();
        codevs.init();
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Utility.readFieldInfo(my.field, "src/test/resources/fields/sample_field.in");

        // 壁に向かう
        assertFalse(codevs.canMove(Codevs.MY_ID, 1, 1, 0));
        // 石push, 忍犬有
        assertFalse(codevs.canMove(Codevs.MY_ID, 2, 3, 1));
        // 石push, 石有
        assertFalse(codevs.canMove(Codevs.MY_ID, 1, 4, 2));

        // 床に向かう
        assertTrue(codevs.canMove(Codevs.MY_ID, 1, 1, 1));
        // 忍者ソウルに向かう
        assertTrue(codevs.canMove(Codevs.MY_ID, 5, 5, 0));
        // 忍者に向かう
        assertTrue(codevs.canMove(Codevs.MY_ID, 1, 1, 2));
        // 忍犬に向かう
        assertTrue(codevs.canMove(Codevs.MY_ID, 1, 5, 2));
        // 石push, ソウル有
        assertTrue(codevs.canMove(Codevs.MY_ID, 3, 3, 1));
    }

    /**
     * 忍者の移動ができてるかどうか
     */
    @Test
    public void testMove() throws Exception {
        Codevs codevs = new Codevs();
        codevs.init();
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Utility.readFieldInfo(my.field, "src/test/resources/fields/sample_field.in");
        Cell[][] field = my.field;

        Ninja ninja = my.ninjaList[0];
        field[1][1].state |= Field.NINJA_A;

        ninja.y = 1;
        ninja.x = 1;

        // 床に向かって移動
        codevs.move(Codevs.MY_ID, 0, 1);
        assertThat(ninja.y, is(1));
        assertThat(ninja.x, is(2));
        assertTrue(Field.existNinja(field[1][2].state));
        assertFalse(Field.existNinja(field[1][1].state));
    }
}