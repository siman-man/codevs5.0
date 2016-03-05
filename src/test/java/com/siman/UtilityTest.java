package test.java.com.siman;

import main.java.com.siman.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by siman on 3/5/16.
 */
public class UtilityTest {
    Codevs codevs;

    @Before
    public void setup() throws Exception {
        this.codevs = new Codevs();
        this.codevs.init();
    }

    @Test
    public void testReadPlayerInfo() throws Exception {
        PlayerInfo enemy = this.codevs.playerInfoList[Codevs.ENEMY_ID];
        Utility.readPlayerInfo(enemy, "src/test/resources/playerInfo/player_sample.in");

        assertThat(enemy.soulPower, is(6));
        assertThat(enemy.soulCount, is(2));
        assertThat(enemy.dogCount, is(2));

        Dog dog = enemy.dogList.get(0);
        assertThat(dog.y, is(11));
        assertThat(dog.x, is(7));
        assertTrue(Field.existDog(enemy.field[11][7].state));

        NinjaSoul soul = enemy.soulList.get(0);
        assertThat(soul.y, is(3));
        assertThat(soul.x, is(10));
        assertTrue(Field.existSoul(enemy.field[3][10].state));

        Ninja ninjaA = enemy.ninjaList[0];
        assertThat(ninjaA.y, is(3));
        assertThat(ninjaA.x, is(9));

        Ninja ninjaB = enemy.ninjaList[1];
        assertThat(ninjaB.y, is(1));
        assertThat(ninjaB.x, is(1));
    }
}