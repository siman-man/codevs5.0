package test.java.com.siman;

import main.java.com.siman.Ninja;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by siman on 3/2/16.
 */
public class NinjaTest {

    @Test
    public void testSaveStatus() throws Exception {
        Ninja ninja = new Ninja();
        ninja.y = 10;
        ninja.x = 5;

        ninja.saveStatus();

        assertThat(ninja.saveY, is(10));
        assertThat(ninja.saveX, is(5));
    }

    @Test
    public void testRollback() throws Exception {
        Ninja ninja = new Ninja();
        ninja.y = 10;
        ninja.x = 5;

        ninja.saveStatus();

        ninja.y = 2;
        ninja.x = 9;

        ninja.rollback();
        assertThat(ninja.y, is(10));
        assertThat(ninja.x, is(5));
    }
}