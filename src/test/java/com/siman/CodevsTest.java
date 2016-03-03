package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Before;
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
    Codevs codevs;

    @Before
    public void setup(){
        this.codevs = new Codevs();
        this.codevs.init();
    }

    /**
     * ターン情報を読み込めてるかどうかをテスト
     */
    @Test
    public void testReadTurnInfo() throws Exception {
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
        assertThat(my.useSkill[NinjaSkill.ROTATION_ZAN], is(1));

        PlayerInfo enemy = codevs.playerInfoList[Codevs.ENEMY_ID];
        assertThat(enemy.soulPower, is(4));
        assertThat(enemy.dogCount, is(9));
        assertThat(enemy.useSkill[NinjaSkill.SUPER_HIGH_SPEED], is(0));
        assertThat(enemy.useSkill[NinjaSkill.ENEMY_ROCKFALL], is(0));
        assertThat(enemy.useSkill[NinjaSkill.ROTATION_ZAN], is(2));

        Cell[][] field = my.field;
        assertTrue(Field.isFloor(field[1][1].state));
    }

    /**
     * 移動判定のテスト
     */
    @Test
    public void testCanMove() throws Exception {
        Codevs codevs = new Codevs();
        codevs.init();
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Utility.readFieldInfo(my, "src/test/resources/fields/sample_field.in");

        // 壁に向かう
        assertFalse(my.canMove(1, 1, 0));
        // 石push, 忍犬有
        assertFalse(my.canMove(2, 3, 1));
        // 石push, 石有
        assertFalse(my.canMove(1, 4, 2));
        // 石push, 忍者有
        assertFalse(my.canMove(7, 5, 1));

        // 床に向かう
        assertTrue(my.canMove(1, 1, 1));
        // 忍者ソウルに向かう
        assertTrue(my.canMove(5, 5, 0));
        // 忍者に向かう
        assertTrue(my.canMove(1, 1, 2));
        // 忍犬に向かう
        assertTrue(my.canMove(1, 5, 2));
        // 石push, ソウル有
        assertTrue(my.canMove(3, 3, 1));
    }

    /**
     * 忍者の移動ができてるかどうか
     */
    @Test
    public void testMove() throws Exception {
        Codevs codevs = new Codevs();
        codevs.init();
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Utility.readFieldInfo(my, "src/test/resources/fields/sample_field.in");
        Cell[][] field = my.field;

        Ninja ninja = my.ninjaList[0];
        field[1][1].state |= Field.NINJA_A;

        ninja.y = 1;
        ninja.x = 1;

        // 床に向かって移動
        my.move(ninja, 1);
        assertThat(ninja.y, is(1));
        assertThat(ninja.x, is(2));
        assertTrue(Field.existNinja(field[1][2].state));
        assertFalse(Field.existNinja(field[1][1].state));

        ninja.y = 3;
        ninja.x = 3;
        // 最初は石が押されていない
        assertFalse(Field.existStone(field[3][5].state));

        // 石に向かって移動（Next ソウル）
        my.move(ninja, 1);
        assertThat(ninja.y, is(3));
        assertThat(ninja.x, is(4));
        assertTrue(Field.existNinja(field[3][4].state));
        assertFalse(Field.existNinja(field[3][3].state));
        assertTrue(Field.existStone(field[3][5].state));

        ninja.y = 1;
        ninja.x = 5;
        // 忍犬に向かって移動
        my.move(ninja, 2);
        assertThat(ninja.y, is(2));
        assertThat(ninja.x, is(5));
        assertTrue(Field.existNinja(field[2][5].state));
    }

    @Test
    public void testLoop() throws Exception {
        File file = new File("src/test/resources/sample.in");
        Scanner sc = new Scanner(file);
        codevs.readTurnInfo(sc);

        this.codevs.beforeProc();
        ActionInfo[] actions = this.codevs.action();
        this.codevs.output(actions);
    }
}