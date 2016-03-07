package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DogTest {
    Codevs codevs;

    @Before
    public void setup() throws Exception {
        this.codevs = new Codevs();
        this.codevs.init();
    }

    @Test
    public void case1() throws Exception {
        File file = new File("src/test/resources/dog/sample1.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        my.avatorId = 110;
        my.summonsAvator = true;
        my.updateDogPosition(false);

        Dog dog0 = my.dogList.get(0);
        assertThat(dog0.y, is(3));
        assertThat(dog0.x, is(2));

        Dog dog12 = my.dogList.get(12);
        assertThat(dog12.y, is(3));
        assertThat(dog12.x, is(1));

        Dog dog14 = my.dogList.get(14);
        assertThat(dog14.y, is(5));
        assertThat(dog14.x, is(1));
    }

    @Test
    public void case2() throws Exception {
        File file = new File("src/test/resources/dog/sample2.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        int aid = Utility.getId(1, 7);
        my.avatorId = aid;
        my.summonsAvator = true;

        ActionInfo[] actions = my.action();
        assertTrue(Field.existDog(my.field[3][4].state));
        //assertThat(my.field[4][4].dangerValue, is(PlayerInfo.DETH));
    }

    @Test
    public void case3() throws Exception {
        File file = new File("src/test/resources/dog/sample3.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        this.codevs.beforeProc(commandList);

        int aid = Utility.getId(10, 8);
        my.avatorId = aid;
        my.summonsAvator = true;

        int id1_7 = Utility.getId(1, 7);

        my.updateDogPosition(false);

        Dog dog17 = my.dogList.get(17);
        assertThat(dog17.y, is(1));
        assertThat(dog17.x, is(8));

        //assertThat(my.field[4][4].dangerValue, is(PlayerInfo.DETH));
    }
}
