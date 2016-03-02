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
        assertTrue(Field.isWall(1));
        assertTrue(Field.isWall(7));

        assertFalse(Field.isWall(2));
    }

    @Test
    public void testIsFloor() throws Exception {
        assertTrue(Field.isFloor(2));
        assertTrue(Field.isFloor(3));

        assertFalse(Field.isFloor(1));
    }

    @Test
    public void testExistStone() throws Exception {
        assertTrue(Field.existStone(4));
        assertTrue(Field.existStone(5));

        assertFalse(Field.existStone(1));
        assertFalse(Field.existStone(2));
    }

    @Test
    public void testExistSoul() throws Exception {
        assertTrue(Field.existSoul(8));
        assertTrue(Field.existSoul(12));

        assertFalse(Field.existSoul(1));
        assertFalse(Field.existSoul(2));
    }

    @Test
    public void testExistNinja() throws Exception {
        assertTrue(Field.existNinja(32));
        assertTrue(Field.existNinja(36));

        assertFalse(Field.existNinja(1));
        assertFalse(Field.existNinja(2));
    }

    @Test
    public void testIsMovableObject() throws Exception {
        assertTrue(Field.isMovableObject(Field.FLOOR));
        assertTrue(Field.isMovableObject(Field.SOUL));

        assertFalse(Field.isMovableObject(Field.WALL));
        assertFalse(Field.isMovableObject(Field.DOG));
    }
}