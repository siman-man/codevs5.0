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
        assertFalse(Field.isWall(Field.STONE));
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
        assertTrue(Field.existStone(Field.FLOOR | Field.STONE | Field.SOUL));

        assertFalse(Field.existStone(Field.WALL));
        assertFalse(Field.existStone(Field.FLOOR));
        assertFalse(Field.existStone(Field.FLOOR | Field.DOG));
    }

    @Test
    public void testExistSoul() throws Exception {
        assertTrue(Field.existSoul(Field.FLOOR | Field.SOUL));
        assertTrue(Field.existSoul(Field.FLOOR | Field.STONE | Field.SOUL));

        assertFalse(Field.existSoul(Field.WALL));
        assertFalse(Field.existSoul(Field.FLOOR));
    }

    @Test
    public void testExistNinja() throws Exception {
        assertTrue(Field.existNinja(Field.FLOOR | Field.NINJA_A));
        assertTrue(Field.existNinja(Field.FLOOR | Field.NINJA_B));
        assertTrue(Field.existNinja(Field.FLOOR | Field.NINJA_A | Field.NINJA_B));

        assertFalse(Field.existNinja(Field.WALL));
        assertFalse(Field.existNinja(Field.FLOOR | Field.STONE));
    }

    @Test
    public void testIsMovableObject() throws Exception {
        assertTrue(Field.isMovableObject(Field.FLOOR));
        assertTrue(Field.isMovableObject(Field.SOUL));

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
}