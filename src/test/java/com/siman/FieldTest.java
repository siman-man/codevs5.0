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
    public void testIsStone() throws Exception {
        assertTrue(Field.isStone(4));
        assertTrue(Field.isStone(5));

        assertFalse(Field.isStone(1));
        assertFalse(Field.isStone(2));
    }

    @Test
    public void testIsSoul() throws Exception {
        assertTrue(Field.isSoul(8));
        assertTrue(Field.isSoul(12));

        assertFalse(Field.isSoul(1));
        assertFalse(Field.isSoul(2));
    }

    @Test
    public void testIsMovableObject() throws Exception {
        assertTrue(Field.isMovableObject(Field.FLOOR));
        assertTrue(Field.isMovableObject(Field.SOUL));

        assertFalse(Field.isMovableObject(Field.WALL));
        assertFalse(Field.isMovableObject(Field.DOG));
    }
}