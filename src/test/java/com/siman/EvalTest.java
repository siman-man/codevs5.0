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
        this.codevs.skillCost = new int[8];
    }

    @Test
    public void case1() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/eval/sample1.in");
        Utility.readPlayerInfo(enemy, "src/test/resources/eval/sample1.in");
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);
    }
}
