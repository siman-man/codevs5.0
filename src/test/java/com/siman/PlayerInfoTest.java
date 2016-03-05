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
        assertThat(my.eachCellDist[15][16], is(PlayerInfo.INF));
        assertThat(my.eachCellDist[15][17], is(PlayerInfo.INF));
        assertThat(my.eachCellDist[15][18], is(27));
        assertThat(my.eachCellDist[15][29], is(1));
        assertThat(my.eachCellDist[15][47], is(6));
        assertThat(my.eachCellDist[29][15], is(1));
        assertThat(my.eachCellDist[15][75], is(8));
        assertThat(my.eachCellDist[75][15], is(8));
        assertThat(my.eachCellDist[131][145], is(1));
        assertThat(my.eachCellDist[145][131], is(1));
        assertThat(my.eachCellDist[130][145], is(2));
        assertThat(my.eachCellDist[130][146], is(3));
        assertThat(my.eachCellDist[146][130], is(3));
        assertThat(my.eachCellDist[144][145], is(1));
        assertThat(my.eachCellDist[145][144], is(1));
        assertThat(my.eachCellDist[144][146], is(2));
        assertThat(my.eachCellDist[146][144], is(4));

        assertThat(my.eachCellDistNonPush[15][15], is(0));
        assertThat(my.eachCellDistNonPush[15][16], is(PlayerInfo.INF));
        assertThat(my.eachCellDistNonPush[15][17], is(PlayerInfo.INF));
        assertThat(my.eachCellDistNonPush[15][18], is(27));
        assertThat(my.eachCellDistNonPush[15][29], is(1));
        assertThat(my.eachCellDistNonPush[15][47], is(8));
        assertThat(my.eachCellDistNonPush[29][15], is(1));
        assertThat(my.eachCellDistNonPush[15][75], is(8));
        assertThat(my.eachCellDistNonPush[75][15], is(8));
        assertThat(my.eachCellDistNonPush[144][145], is(PlayerInfo.INF));
        assertThat(my.eachCellDistNonPush[144][146], is(PlayerInfo.INF));
    }

    @Test
    public void testSetTargetSoul() throws Exception {
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        Ninja ninjaA = my.ninjaList[0];
        Ninja ninjaB = my.ninjaList[1];

        assertThat(ninjaA.targetSoulId, is(1));
        assertThat(ninjaB.targetSoulId, is(3));
    }

    @Test
    public void testIsInside() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        assertTrue(my.isInside(0, 0));
        assertTrue(my.isInside(1, 1));
        assertTrue(my.isInside(Field.HEIGHT - 1, Field.WIDTH - 1));

        assertFalse(my.isInside(-1, 0));
        assertFalse(my.isInside(Field.HEIGHT, Field.WIDTH));
    }

    @Test
    public void testManhattanDist() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        assertThat(my.calcManhattanDist(0, 0, 1, 1), is(2));
        assertThat(my.calcManhattanDist(0, 2, 5, 1), is(6));
        assertThat(my.calcManhattanDist(10, 2, 5, 7), is(10));
    }

    @Test
    public void testAction() throws Exception {
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        my.action();
    }

    @Test
    public void testWallDist() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        assertThat(my.getWallDist(1, 1), is(1));
        assertThat(my.getWallDist(5, 3), is(3));
        assertThat(my.getWallDist(10, 10), is(3));
    }

    @Test
    public void testSetRemoveSoul() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Cell cell = my.field[5][5];

        assertFalse(Field.existSoul(cell.state));

        my.setSoul(5, 5);
        assertTrue(Field.existSoul(cell.state));

        my.removeSoul(5, 5);
        assertFalse(Field.existSoul(cell.state));
    }

    @Test
    public void testSetRemoevDog() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Cell cell = my.field[5][5];

        assertFalse(Field.existDog(cell.state));

        my.setDog(5, 5);
        assertTrue(Field.existDog(cell.state));

        my.removeDog(5, 5);
        assertFalse(Field.existDog(cell.state));
    }

    @Test
    public void testIsFixStone() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];

        assertTrue(my.isFixStone(1, 1));
        assertTrue(my.isFixStone(12, 4));
    }

    @Test
    public void testSetRemoveStone() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        Cell cell = my.field[5][5];

        assertFalse(Field.existStone(cell.state));

        my.setStone(5, 5);
        assertTrue(Field.existStone(cell.state));

        my.removeStone(5, 5);
        assertFalse(Field.existStone(cell.state));
    }
    @Test
    public void testMoveAction() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);
        Utility.readFieldInfo(my, "src/test/resources/fields/eval_field.in");

        Ninja ninja = my.ninjaList[0];

        ninja.y = 1;
        ninja.x = 3;
        ninja.targetSoulId = 0;

        char[] action = {'L', 'L'};

        my.setStone(1, 2);
        ActionInfo info = my.moveAction(ninja, action);

        assertThat(info.ninjaY, is(1));
        assertThat(info.ninjaX, is(2));
        assertTrue(info.moveStone);
    }

    @Test
    public void testGetMaxEval() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);
        Utility.readFieldInfo(my, "src/test/resources/fields/eval_field.in");

        Ninja ninja = my.ninjaList[0];

        ninja.y = 1;
        ninja.x = 3;
        ninja.targetSoulId = 0;

        assertFalse(Field.existStone(my.field[1][2].state));
        assertThat(my.getMaxNinjaEval(ninja), is(473));

        my.setStone(1, 2);
        assertTrue(Field.existStone(my.field[1][2].state));
        assertThat(my.getMaxNinjaEval(ninja), is(-6));

        my.removeStone(1, 2);
        my.setStone(2, 1);
        assertThat(my.getMaxNinjaEval(ninja), is(473));
    }
}