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
}
