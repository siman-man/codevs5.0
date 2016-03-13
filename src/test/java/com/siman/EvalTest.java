package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class EvalTest {
    Codevs codevs;

    @Before
    public void setup() throws Exception {
        this.codevs = new Codevs();
        this.codevs.init();
    }

    @Test
    public void case1() throws Exception {
        File file = new File("src/test/resources/sample2.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        Ninja ninjaA = my.ninjaList[0];
        NinjaSoul soul3 = my.soulList.get(3);
        //assertThat(ninjaA.targetId, is(soul3.sid));

        ActionInfo[] actions = my.action(enemy, commandList);

        //assertThat(ninjaA.targetId, is(soul3.sid));
        int id2_2 = Utility.getId(2, 2);
        int id6_3 = Utility.getId(6, 3);
        int id4_2 = Utility.getId(4, 2);

        assertThat(my.dogCount, is(0));
        assertThat(my.eachCellDistDogBlock[id2_2][id6_3], is(5));
        assertThat(my.eachCellDistDogBlock[id4_2][id6_3], is(3));
        assertTrue(Field.existStone(my.field[3][3].state));
        assertTrue(Field.existSoul(my.field[6][3].state));
        NinjaSoul soul = my.soulList.get(3);

        assertThat(soul.y, is(6));
        assertThat(soul.x, is(3));
    }

    @Test
    public void case2() throws Exception {
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(enemy, "src/test/resources/eval/sample2.in");
        enemy.updateDogValue();

        assertThat(enemy.getAliveCellCount(1, 1), is(2));
        assertThat(enemy.getAliveCellCount(15, 1), is(2));
    }

    @Test
    public void case3() throws Exception {
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(enemy, "src/test/resources/eval/sample3.in");

        enemy.updateEachCellDist();
        enemy.updateDogValue();

        assertThat(enemy.getAliveCellCount(10, 9), is(5));
    }

    @Test
    public void case4() throws Exception {
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(enemy, "src/test/resources/eval/sample4.in");

        enemy.updateEachCellDist();
        enemy.updateDogValue();

        assertThat(enemy.getAliveCellCount(5, 9), is(2));
    }

    @Test
    public void case5() throws Exception {
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(enemy, "src/test/resources/eval/sample5.in");

        enemy.updateEachCellDist();
        enemy.updateDogValue();

        assertThat(enemy.getAliveCellCount(3, 1), is(PlayerInfo.INF));
    }
}
