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
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action();
        ActionInfo actionB = bestAction[1];

        assertThat(actionB.commandList, is("LL"));
    }

    @Test
    public void case2() throws Exception {
        File file = new File("src/test/resources/action/sample2.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action();
        ActionInfo actionB = bestAction[1];

        assertTrue(Field.existDog(my.field[10][2].state));
        assertThat(actionB.commandList, is("DD"));
    }

    @Test
    public void case3() throws Exception {
        File file = new File("src/test/resources/action/sample3.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action();
        ActionInfo actionB = bestAction[1];

        assertThat(actionB.commandList, is("RR"));
    }

    @Test
    public void case4() throws Exception {
        File file = new File("src/test/resources/action/sample4.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action();
        ActionInfo actionB = bestAction[0];

        assertThat(actionB.commandList, is("RD"));
    }

    @Test
    public void case5() throws Exception {
        File file = new File("src/test/resources/action/sample5.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        ActionInfo[] bestAction = my.action();
        ActionInfo actionA = bestAction[0];

        assertThat(actionA.commandList, is("RD"));
    }
}
