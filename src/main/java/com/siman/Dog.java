package main.java.com.siman;

import test.java.com.siman.Utility;

/**
 * Created by siman on 3/2/16.
 */
public class Dog {
    public int id;
    public int did;
    public int y;
    public int x;
    public int update_at;

    public Dog(int id, int y, int x) {
        this.id = id;
        this.did = Utility.getId(y, x);
        this.y = y;
        this.x = x;
    }
}
