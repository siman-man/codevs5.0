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

        int id1_1 = Utility.getId(1, 1);
        int id1_2 = Utility.getId(1, 2);
        int id1_3 = Utility.getId(1, 3);
        int id2 = Utility.getId(10, 6);
        int id3 = Utility.getId(9, 4);
        int id1_4 = Utility.getId(1, 4);
        int id1_5 = Utility.getId(1, 5);
        int id2_1 = Utility.getId(2, 1);
        int id2_3 = Utility.getId(2, 3);
        int id2_4 = Utility.getId(2, 4);
        int id2_5 = Utility.getId(2, 5);
        int id3_3 = Utility.getId(3, 3);
        int id3_4 = Utility.getId(3, 4);
        int id3_5 = Utility.getId(3, 5);
        int id3_13 = Utility.getId(3, 13);
        int id5_5 = Utility.getId(5, 5);
        int id5_11 = Utility.getId(5, 11);
        int id1_12 = Utility.getId(1, 12);
        int id2_12 = Utility.getId(2, 12);
        int id3_12 = Utility.getId(3, 12);

        assertThat(my.eachCellDist[id1_1][id1_1], is(0));
        //assertThat(my.eachCellDist[id2_3][id2_4], is(3));
        //assertThat(my.eachCellDist[id2_4][id2_5], is(3));
        assertThat(my.eachCellDist[id2_4][id2_3], is(PlayerInfo.INF));
        //assertThat(my.eachCellDist[id2_5][id2_4], is(3));

        assertThat(my.eachCellDist[id2_5][id1_4], is(2));
        //assertThat(my.eachCellDist[id2_3][id1_4], is(PlayerInfo.INF));
        //assertThat(my.eachCellDist[id3_4][id1_4], is(PlayerInfo.INF));
        //assertThat(my.eachCellDist[id3_4][id1_5], is(PlayerInfo.INF));
        //assertThat(my.eachCellDist[id3_5][id1_5], is(PlayerInfo.INF));
        //assertThat(my.eachCellDist[id3_4][id2_4], is(1));
        //assertThat(my.eachCellDist[id2_4][id1_4], is(1));
        //assertThat(my.eachCellDist[id3_5][id1_4], is(PlayerInfo.INF));
        //assertThat(my.eachCellDist[id3_3][id1_4], is(PlayerInfo.INF));

        assertThat(my.eachCellDist[id1_1][id1_2], is(PlayerInfo.INF));
        assertThat(my.eachCellDist[id1_1][id1_3], is(PlayerInfo.INF));
        assertThat(my.eachCellDist[id1_1][id2_1], is(1));
        assertThat(my.eachCellDist[id1_1][id5_11], is(14));
        assertThat(my.eachCellDist[id1_1][id3_12], is(17));
        assertThat(my.eachCellDist[id1_1][id2_12], is(18));
        assertThat(my.eachCellDist[id1_1][id1_12], is(19));
        assertThat(my.eachCellDist[id1_1][id1_4], is(27));
        assertThat(my.eachCellDist[id1_1][29], is(1));
        assertThat(my.eachCellDist[id1_1][id3_5], is(6));
        assertThat(my.eachCellDist[29][id1_1], is(1));
        assertThat(my.eachCellDist[id1_1][id5_5], is(8));
        assertThat(my.eachCellDist[75][id1_1], is(8));
        assertThat(my.eachCellDist[131][145], is(1));
        assertThat(my.eachCellDist[145][131], is(1));
        assertThat(my.eachCellDist[130][145], is(2));
        assertThat(my.eachCellDist[130][146], is(3));
        //assertThat(my.eachCellDist[id2][id3], is(3));
        assertThat(my.eachCellDist[144][145], is(1));
        assertThat(my.eachCellDist[145][144], is(1));
        assertThat(my.eachCellDist[144][146], is(2));
        //assertThat(my.eachCellDist[146][144], is(4));

        assertThat(my.eachCellDistNonPush[id2_3][id1_4], is(24));
        assertThat(my.eachCellDistNonPush[id3_4][id1_4], is(22));
        assertThat(my.eachCellDistNonPush[id3_4][id1_5], is(21));
        assertThat(my.eachCellDistNonPush[id3_5][id1_5], is(20));
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

        /*
        assertThat(ninjaA.targetSoulId, is(0));
        assertThat(ninjaB.targetSoulId, is(8));
         */
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
        PlayerInfo enemy = codevs.playerInfoList[Codevs.ENEMY_ID];

        my.action(enemy, commandList);
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
        PlayerInfo enemy = codevs.playerInfoList[Codevs.ENEMY_ID];
        Cell cell = my.field[5][5];
        Cell ecell = enemy.field[5][5];

        assertFalse(Field.existStone(cell.state));

        my.setStone(5, 5);
        assertTrue(Field.existStone(cell.state));
        assertFalse(Field.existStone(ecell.state));

        my.removeStone(5, 5);
        assertFalse(Field.existStone(cell.state));


        assertFalse(Field.existStone(ecell.state));

        enemy.setStone(5, 5);
        assertTrue(Field.existStone(ecell.state));
        assertFalse(Field.existStone(cell.state));

        enemy.removeStone(5, 5);
        assertFalse(Field.existStone(ecell.state));

        cell = my.field[3][10];
        my.setStone(3, 10);
        assertTrue(Field.existStone(cell.state));

        my.removeStone(3, 10);
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
        ninja.targetId = 0;

        String action = "LL";

        my.setStone(1, 2);
        ActionInfo info = my.moveAction(ninja, action);

        assertThat(info.ninjaY, is(1));
        assertThat(info.ninjaX, is(2));
        assertTrue(info.moveStone);
    }

    @Test
    public void testGetMaxNinjaEval() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);
        Utility.readFieldInfo(my, "src/test/resources/fields/eval_field.in");

        assertTrue(Field.existStone(my.field[12][12].state));
        Ninja ninja = my.ninjaList[0];

        ninja.y = 1;
        ninja.x = 3;
        ninja.targetId = 0;

        /* 評価値がコロコロ変わるのでテストしない
        assertFalse(Field.existStone(my.field[1][2].state));
        assertThat(my.getMaxNinjaEval(ninja).toEval(), is(4820));

        my.setStone(1, 2);
        assertTrue(Field.existStone(my.field[1][2].state));
        assertThat(my.getMaxNinjaEval(ninja).toEval(), is(-40));

        my.removeStone(1, 2);
        my.setStone(2, 1);
        assertThat(my.getMaxNinjaEval(ninja).toEval(), is(4820));
        */
    }

    @Test
    public void testFallRockAttack() throws Exception {
        PlayerInfo my = codevs.playerInfoList[Codevs.MY_ID];
        CommandList commandList = new CommandList();
        Codevs.skillCost[NinjaSkill.ENEMY_ROCKFALL] = 3;
        codevs.beforeProc(commandList);
        Utility.readFieldInfo(my, "src/test/resources/fields/rockfall_field.in");

        /* ソウル取得の邪魔はしない
        assertTrue(commandList.useSkill);
        assertThat(commandList.spell, is("2 14 1"));
        */
    }

    @Test
    public void testUpdateDangerValue() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fields/dog.in");
        CommandList commandList = new CommandList();

        codevs.beforeProc(commandList);

        // TODO : 評価前に出すのではなくて評価時に出すようにする
        // assertThat(my.field[13][2].dangerValue, is(my.DETH));
    }

    @Test
    public void testUpdateDogTarget() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fields/dog.in");
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);

        Ninja ninjaA = my.ninjaList[0];
        Ninja ninjaB = my.ninjaList[1];

        my.updateDogTarget(false, false);

        Dog dog0 = my.dogList.get(0);
        Dog dog9 = my.dogList.get(9);

        assertThat(my.dogCount, is(10));
        assertThat(dog0.targetId, is(ninjaA.nid));
        assertThat(dog0.targetDist, is(5));
        assertThat(dog9.targetId, is(ninjaB.nid));
        assertThat(dog9.targetDist, is(10));

        my.summonsAvator = true;
        my.avatorId = Utility.getId(1, 1);

        my.updateDogTarget(false, my.summonsAvator);
        assertThat(dog0.targetId, is(my.avatorId));
        assertThat(dog0.targetDist, is(7));
        assertThat(dog9.targetId, is(my.avatorId));
        assertThat(dog9.targetDist, is(17));
    }

    @Test
    public void testUpdateDogPosition() throws Exception {
        PlayerInfo my = this.codevs.playerInfoList[Codevs.MY_ID];
        Utility.readPlayerInfo(my, "src/test/resources/fields/dog.in");
        CommandList commandList = new CommandList();
        codevs.beforeProc(commandList);

        my.summonsAvator = true;
        my.avatorId = Utility.getId(1, 1);

        my.updateDogPosition(false, my.summonsAvator);

        Dog dog0 = my.dogList.get(0);
        assertThat(dog0.y, is(2));
        assertThat(dog0.x, is(6));

        Dog dog7 = my.dogList.get(7);
        assertThat(dog7.y, is(13));
        assertThat(dog7.x, is(4));

        Dog dog8 = my.dogList.get(8);
        assertThat(dog8.y, is(14));
        assertThat(dog8.x, is(1));

        Dog dog9 = my.dogList.get(9);
        assertThat(dog9.y, is(13));
        assertThat(dog9.x, is(5));
    }
}