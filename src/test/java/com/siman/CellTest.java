package test.java.com.siman;

import main.java.com.siman.Cell;
import main.java.com.siman.Coord;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by siman on 3/3/16.
 */
public class CellTest {

    @Test
    public void testClear() throws Exception {
        Cell cell = new Cell(0);

        cell.state = 3;
        assertThat(3, is(cell.state));

        cell.clear();

        assertThat(0, is(cell.state));
    }

    @Test
    public void testGetCoord() throws Exception {
        Cell cell1 = new Cell(0);
        Coord coord1 = cell1.getCoord();
        assertThat(coord1.y, is(0));
        assertThat(coord1.x, is(0));

        Cell cell2 = new Cell(30);
        Coord coord2 = cell2.getCoord();
        assertThat(coord2.y, is(2));
        assertThat(coord2.x, is(2));

        Cell cell3 = new Cell(100);
        Coord coord3 = cell3.getCoord();
        assertThat(coord3.y, is(7));
        assertThat(coord3.x, is(2));
    }
}