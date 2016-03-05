package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FallRockTest {
    Codevs codevs;

    @Before
    public void setup() throws Exception {
        this.codevs = new Codevs();
        this.codevs.init();
        this.codevs.skillCost = new int[8];
    }

    @Test
    public void case1() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample1.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample1.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        Ninja ninjaA = enemy.ninjaList[0];
        assertThat(ninjaA.targetSoulId, is(0));

        enemy.fallRockAttack(commandList);

        String expect = NinjaSkill.fallrockEnemy(1, 1);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case2() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample2.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample2.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        enemy.fallRockAttack(commandList);

        String expect = NinjaSkill.fallrockEnemy(12, 4);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case3() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample3.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample3.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        enemy.fallRockAttack(commandList);

        String expect = NinjaSkill.fallrockEnemy(12, 4);
        // TODO : 相手のベストな行動に対して失敗した時に忍犬にぶつかる場合は石を落とす
        // assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case4() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample4.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample4.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        Ninja ninja = enemy.ninjaList[0];
        enemy.setStone(4, 9);
        ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

        assertTrue(Field.existNinja(enemy.field[5][9].state));
        assertFalse(enemy.canMove(5, 9, 0));
        assertFalse(enemy.canMove(5, 9, 1));
        assertFalse(enemy.canMove(5, 9, 2));
        assertFalse(enemy.canMove(5, 9, 3));

        enemy.fallRockAttack(commandList);

        String expect = "";
        assertThat(commandList.spell, is(expect));
    }
}
