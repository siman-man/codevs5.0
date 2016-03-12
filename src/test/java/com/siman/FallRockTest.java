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

        assertThat(ninjaA.y, is(2));
        assertThat(ninjaA.x, is(1));
        assertTrue(Field.existDog(enemy.field[3][1].state));

        ActionInfo bestAction = enemy.getMaxNinjaEval(ninjaA);

        int id1_8 = Utility.getId(1, 8);
        assertThat(bestAction.commandList, is("UR"));
        assertThat(ninjaA.targetId, is(id1_8));

        enemy.fallRockAttackEasy(enemy, commandList);

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

        enemy.fallRockAttackEasy(enemy, commandList);

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

        enemy.fallRockAttackEasy(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(12, 4);
        assertThat(commandList.spell, is(expect));
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

        enemy.fallRockAttackEasy(enemy, commandList);

        String expect = "";
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case5() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample5.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample5.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        Ninja ninja = enemy.ninjaList[0];
        ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

        System.err.println(bestAction);

        assertThat(bestAction.ninjaY, is(3));
        assertThat(bestAction.ninjaX, is(4));

        enemy.fallRockAttackEasy(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(4, 4);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case6() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample6.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample6.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        enemy.fallRockAttackEasy(enemy, commandList);

        Ninja ninja = enemy.ninjaList[0];
        ActionInfo info = enemy.getMaxNinjaEval(ninja);

        String expect = NinjaSkill.fallrockEnemy(4, 1);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case7() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample7.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample7.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        enemy.fallRockAttackEasy(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(6, 6);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case8() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample8.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample8.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        Ninja ninja = enemy.ninjaList[0];
        ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

        enemy.fallRockAttackEasy(enemy, commandList);

        assertThat(commandList.spell, is(""));
    }

    @Test
    public void case9() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample9.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample9.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        Ninja ninja = enemy.ninjaList[0];
        ActionInfo bestAction = enemy.getMaxNinjaEval(ninja);

        enemy.fallRockAttackEasy(enemy, commandList);

        assertThat(commandList.spell, is(""));
    }

    @Test
    public void case10() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fallrock/sample10.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/fallrock/sample10.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        enemy.fallRockAttackEasy(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(15, 12);
        assertThat(commandList.spell, is(expect));
    }
}
