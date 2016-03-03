package test.java.com.siman;

import main.java.com.siman.*;
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

        Cell cellC = my.getCell(Field.WIDTH + 1);
        assertThat(cellC.y, is(1));
        assertThat(cellC.x, is(1));
    }

    @Test
    public void testRollbackField() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Utility.readFieldInfo(my, "src/test/resources/fields/sample_field.in");
        Cell[][] field = my.field;

        my.saveField();

        assertTrue(Field.isFloor(field[7][5].state));
        assertFalse(Field.existNinja(field[7][6].state));
        assertTrue(Field.existNinja(field[7][7].state));

        my.move(my.ninjaList[0], 3);

        assertFalse(Field.isFloor(field[7][5].state));
        assertTrue(Field.existStone(field[7][5].state));
        assertTrue(Field.existNinja(field[7][6].state));
        assertTrue(Field.isFloor(field[7][7].state));

        my.rollbackField();

        assertTrue(Field.isFloor(field[7][5].state));
        assertFalse(Field.existNinja(field[7][6].state));
        assertTrue(Field.existNinja(field[7][7].state));
    }

    @Test
    public void testUpdateEachCellDist() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Utility.readFieldInfo(my, "src/test/resources/fields/cost_field.in");

        my.updateEachCellDist();

        assertThat(my.eachCellDist[15][15], is(0));
        assertThat(my.eachCellDist[15][16], is(9999));
        assertThat(my.eachCellDist[15][17], is(9999));
        assertThat(my.eachCellDist[15][29], is(1));
        assertThat(my.eachCellDist[29][15], is(1));
        assertThat(my.eachCellDist[15][75], is(8));
        assertThat(my.eachCellDist[75][15], is(8));
    }

    @Test
    public void testSetTargetSoul() throws Exception {
        codevs.beforeProc();
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        Ninja ninjaA = my.ninjaList[0];
        Ninja ninjaB = my.ninjaList[1];

        assertThat(ninjaA.targetSoulId, is(0));
        assertThat(ninjaB.targetSoulId, is(2));
    }

    @Test
    public void testAction() throws Exception {
        codevs.beforeProc();
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        my.action();
    }
}