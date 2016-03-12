package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by siman on 3/7/16.
 */
public class BestActionTest {
    Codevs codevs;

    @Before
    public void setup() throws Exception {
        this.codevs = new Codevs();
        this.codevs.init();
    }

    @Test
    public void case1() throws Exception {
        File file = new File("src/test/resources/action/sample1.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action(enemy, commandList);
        ActionInfo actionB = bestAction[1];

        assertThat(actionB.commandList, is("LL"));
    }

    @Test
    public void case2() throws Exception {
        File file = new File("src/test/resources/action/sample2.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action(enemy, commandList);
        ActionInfo actionB = bestAction[1];

        assertTrue(Field.existDog(my.field[10][2].state));
        assertThat(actionB.commandList, is("DR"));
    }

    @Test
    public void case3() throws Exception {
        File file = new File("src/test/resources/action/sample3.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action(enemy, commandList);
        ActionInfo actionB = bestAction[1];

        System.err.println(actionB);

        assertThat(actionB.commandList, is("UU"));
    }

    @Test
    public void case4() throws Exception {
        File file = new File("src/test/resources/action/sample4.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action(enemy, commandList);
        ActionInfo actionB = bestAction[0];

        assertThat(actionB.commandList, is("RD"));
    }

    @Test
    public void case5() throws Exception {
        File file = new File("src/test/resources/action/sample5.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action(enemy, commandList);
        ActionInfo actionA = bestAction[0];

        assertThat(actionA.commandList, is("RR"));
    }

    @Test
    public void case6() throws Exception {
        File file = new File("src/test/resources/action/sample6.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        int aliveCnt = my.getAliveCellCount(7, 6);
    }

    @Test
    public void case7() throws Exception {
        File file = new File("src/test/resources/action/sample7.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);
        Ninja ninja = my.ninjaList[1];

        assertFalse(my.canMove(12, 1, 1));
        assertThat(commandList.spell, is(NinjaSkill.breakMyStone(13, 1)));
    }

    @Test
    public void case8() throws Exception {
        File file = new File("src/test/resources/action/sample8.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);
        assertThat(my.field[8][8].dogDist, is(PlayerInfo.INF));

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertFalse(Field.existDog(my.field[8][8].state));
        assertThat(my.field[8][8].dogDist, is(PlayerInfo.INF));
        assertThat(commandList.spell, is(""));
    }

    @Test
    public void case9() throws Exception {
        File file = new File("src/test/resources/action/sample9.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        //assertThat(commandList.spell, is(""));
    }

    @Test
    public void case10() throws Exception {
        File file = new File("src/test/resources/action/sample10.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        String expect = NinjaSkill.breakMyStone(7, 5);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case11() throws Exception {
        File file = new File("src/test/resources/action/sample11.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(commandList.spell, is(""));
    }

    @Test
    public void case12() throws Exception {
        File file = new File("src/test/resources/action/sample12.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        String expect = NinjaSkill.breakMyStone(3, 7);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case13() throws Exception {
        File file = new File("src/test/resources/action/sample13.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        String expect = NinjaSkill.breakMyStone(13, 3);
        assertThat(my.field[13][3].dogDist, is(1));
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case14() throws Exception {
        File file = new File("src/test/resources/action/sample14.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(11, 1);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case15() throws Exception {
        File file = new File("src/test/resources/action/sample15.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(6, 11);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case16() throws Exception {
        File file = new File("src/test/resources/action/sample16.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        String expect = NinjaSkill.fallrockEnemy(4, 9);
        assertThat(commandList.spell, is(expect));
    }

    @Test
    public void case17() throws Exception {
        File file = new File("src/test/resources/action/sample17.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(commandList.spell, is(NinjaSkill.fallrockEnemy(13, 2)));
    }

    @Test
    public void case18() throws Exception {
        File file = new File("src/test/resources/action/sample18.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(commandList.spell, is(NinjaSkill.fallrockEnemy(6, 6)));
    }

    @Test
    public void case19() throws Exception {
        File file = new File("src/test/resources/action/sample19.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(commandList.spell, is(NinjaSkill.summonEnemyAvator(3, 12)));
    }

    @Test
    public void case20() throws Exception {
        File file = new File("src/test/resources/action/sample20.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];

        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(commandList.spell, is(NinjaSkill.fallrockEnemy(9, 9)));
    }

    @Test
    public void case21() throws Exception {
        File file = new File("src/test/resources/action/sample21.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(commandList.spell, is(""));
    }

    @Test
    public void case22() throws Exception {
        File file = new File("src/test/resources/action/sample22.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestActions = my.action(enemy, commandList);

        assertThat(my.field[6][6].dogDist, is(2));
        assertThat(commandList.spell, is(""));
    }

    @Test
    public void case23() throws Exception {
        File file = new File("src/test/resources/action/sample23.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        assertThat(commandList.spell, is(NinjaSkill.fallrockEnemy(9, 3)));
    }
}
