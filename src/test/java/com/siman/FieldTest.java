package test.java.com.siman;

import main.java.com.siman.Field;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by siman on 3/2/16.
 */
public class FieldTest {

    @Test
    public void testToInteger() throws Exception {
        assertThat(Field.toInteger('W'), is(Field.WALL));
        assertThat(Field.toInteger('_'), is(Field.FLOOR));
        assertThat(Field.toInteger('O'), is(Field.STONE));
    }

    @Test
    public void testIsWall() throws Exception {
        assertTrue(Field.isWall(Field.WALL));
        assertTrue(Field.isWall(Field.FLOOR | Field.WALL));

        assertFalse(Field.isWall(Field.FLOOR));
        assertFalse(Field.isWall(Field.FLOOR | Field.STONE));
        assertFalse(Field.isWall(Field.FLOOR | Field.DOG));
    }

    @Test
    public void testIsFloor() throws Exception {
        assertTrue(Field.isFloor(Field.FLOOR));
        assertTrue(Field.isFloor(Field.FLOOR | Field.SOUL));

        assertFalse(Field.isFloor(Field.FLOOR | Field.DOG));
        assertFalse(Field.isFloor(Field.WALL));
        assertFalse(Field.isFloor(Field.FLOOR | Field.STONE));
        assertFalse(Field.isFloor(Field.FLOOR | Field.NINJA_A));
        assertFalse(Field.isFloor(Field.FLOOR | Field.NINJA_B));
    }

    @Test
    public void testExistStone() throws Exception {
        assertTrue(Field.existStone(Field.FLOOR | Field.STONE));
        assertTrue(Field.existStone(Field.FLOOR | Field.FIX_STONE));
        assertTrue(Field.existStone(Field.FLOOR | Field.STONE | Field.SOUL));

        assertFalse(Field.existStone(Field.WALL));
        assertFalse(Field.existStone(Field.FLOOR));
        assertFalse(Field.existStone(Field.FLOOR | Field.DOG));
    }

    @Test
    public void testExistFixStone() throws Exception {
        assertTrue(Field.existFixStone(Field.FLOOR | Field.FIX_STONE));
        assertTrue(Field.existFixStone(Field.FLOOR | Field.FIX_STONE | Field.SOUL));

        assertFalse(Field.existFixStone(Field.WALL));
        assertFalse(Field.existFixStone(Field.FLOOR | Field.STONE));
        assertFalse(Field.existFixStone(Field.FLOOR));
        assertFalse(Field.existFixStone(Field.FLOOR | Field.DOG));
    }

    @Test
    public void testExistSolidObject() throws Exception {
        assertTrue(Field.existSolidObject(Field.FLOOR | Field.FIX_STONE));
        assertTrue(Field.existSolidObject(Field.FLOOR | Field.STONE));
        assertTrue(Field.existSolidObject(Field.FLOOR | Field.FIX_STONE | Field.SOUL));
        assertTrue(Field.existSolidObject(Field.FLOOR | Field.STONE | Field.SOUL));
        assertTrue(Field.existSolidObject(Field.WALL));

        assertFalse(Field.existSolidObject(Field.FLOOR));
        assertFalse(Field.existSolidObject(Field.NINJA_A));
        assertFalse(Field.existSolidObject(Field.NINJA_B));
        assertFalse(Field.existSolidObject(Field.FLOOR | Field.DOG));
    }

    @Test
    public void testExistSoul() throws Exception {
        assertTrue(Field.existSoul(Field.FLOOR | Field.SOUL));
        assertTrue(Field.existSoul(Field.FLOOR | Field.STONE | Field.SOUL));
        assertTrue(Field.existSoul(Field.FLOOR | Field.FIX_STONE | Field.SOUL));

        assertFalse(Field.existSoul(Field.WALL));
        assertFalse(Field.existSoul(Field.FLOOR));
        assertFalse(Field.existSoul(Field.NINJA_A));
        assertFalse(Field.existSoul(Field.NINJA_B));
    }

    @Test
    public void testExistNinja() throws Exception {
        assertTrue(Field.existNinja(Field.FLOOR | Field.NINJA_A));
        assertTrue(Field.existNinja(Field.FLOOR | Field.NINJA_B));
        assertTrue(Field.existNinja(Field.FLOOR | Field.NINJA_A | Field.NINJA_B));

        assertFalse(Field.existNinja(Field.WALL));
        assertFalse(Field.existNinja(Field.SOUL));
        assertFalse(Field.existNinja(Field.FLOOR | Field.DOG));
        assertFalse(Field.existNinja(Field.FLOOR | Field.STONE));
    }

    @Test
    public void testIsMovableObject() throws Exception {
        assertTrue(Field.isMovableObject(Field.FLOOR));
        assertTrue(Field.isMovableObject(Field.SOUL));
        assertTrue(Field.isMovableObject(Field.FLOOR | Field.SOUL));

        assertFalse(Field.isMovableObject(Field.SOUL | Field.STONE));
        assertFalse(Field.isMovableObject(Field.NINJA_A));
        assertFalse(Field.isMovableObject(Field.NINJA_B));
        assertFalse(Field.isMovableObject(Field.WALL));
        assertFalse(Field.isMovableObject(Field.DOG));
        assertFalse(Field.isMovableObject(Field.DOG | Field.FLOOR));
        assertFalse(Field.isMovableObject(Field.NINJA_A | Field.FLOOR));
        assertFalse(Field.isMovableObject(Field.NINJA_B | Field.FLOOR));
        assertFalse(Field.isMovableObject(Field.DOG | Field.SOUL));
        assertFalse(Field.isMovableObject(Field.NINJA_A | Field.SOUL));
        assertFalse(Field.isMovableObject(Field.NINJA_B | Field.SOUL));
    }

    @Test
    public void testIsDogMovableObject() throws Exception {
        assertTrue(Field.isDogMovableObject(Field.FLOOR));
        assertTrue(Field.isDogMovableObject(Field.SOUL));
        assertTrue(Field.isDogMovableObject(Field.FLOOR | Field.SOUL));
        assertTrue(Field.isDogMovableObject(Field.FLOOR | Field.NINJA_A));
        assertTrue(Field.isDogMovableObject(Field.FLOOR | Field.NINJA_B));

        assertFalse(Field.isDogMovableObject(Field.SOUL | Field.STONE));
        assertFalse(Field.isDogMovableObject(Field.WALL));
        assertFalse(Field.isDogMovableObject(Field.DOG));
        assertFalse(Field.isDogMovableObject(Field.DOG | Field.FLOOR));
        assertFalse(Field.isDogMovableObject(Field.DOG | Field.SOUL));
    }

    @Test
    public void testIsStonePuttable() throws Exception {
        assertTrue(Field.isStonePuttable(Field.FLOOR));

        assertFalse(Field.isStonePuttable(Field.FLOOR | Field.SOUL));
        assertFalse(Field.isStonePuttable(Field.WALL));
        assertFalse(Field.isStonePuttable(Field.FLOOR | Field.DOG));
        assertFalse(Field.isStonePuttable(Field.FLOOR | Field.NINJA_A));
        assertFalse(Field.isStonePuttable(Field.FLOOR | Field.NINJA_B));
        assertFalse(Field.isStonePuttable(Field.FLOOR | Field.STONE));
    }
}