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
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        assertTrue(Field.existStone(my.field[3][3].state));
        ActionInfo[] actions = my.action();

        ActionInfo actionA = actions[0];

        assertThat(actionA.ninjaY, is(4));
        assertThat(actionA.ninjaX, is(2));
    }

    @Test
    public void case2() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/eval/sample2.in");

        assertThat(my.getAliveCellCount(1, 1), is(2));
        assertThat(my.getAliveCellCount(15, 1), is(2));
    }

    @Test
    public void case3() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/eval/sample3.in");

        my.updateEachCellDist();
        my.updateDogValue();

        assertThat(my.getAliveCellCount(10, 8), is(3));
    }

    @Test
    public void case4() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/eval/sample4.in");

        my.updateEachCellDist();
        my.updateDogValue();

        assertThat(my.getAliveCellCount(5, 9), is(1));
    }
}
