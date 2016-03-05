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
}
