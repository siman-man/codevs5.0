package test.java.com.siman;

import main.java.com.siman.Cell;
import main.java.com.siman.Codevs;
import main.java.com.siman.Field;
import main.java.com.siman.PlayerInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by siman on 3/3/16.
 */
public class PlayerInfoTest {
    Codevs codevs;

    @Before
    public void setup() throws Exception {
        this.codevs = new Codevs();
        this.codevs.init();
        File file = new File("src/test/resources/sample.in");
        Scanner sc = new Scanner(file);
        this.codevs.readTurnInfo(sc);
    }

    @Test
    public void testGetCell() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        Cell cellA = my.getCell(0);
        assertThat(cellA.y, is(0));
        assertThat(cellA.x, is(0));

        Cell cellB = my.getCell(Field.WIDTH);
        assertThat(cellB.y, is(1));
        assertThat(cellB.x, is(0));

        Cell cellC = my.getCell(Field.WIDTH+1);
        assertThat(cellC.y, is(1));
        assertThat(cellC.x, is(1));
    }
}