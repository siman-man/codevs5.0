package main.java.com.siman;

import test.java.com.siman.Utility;

/**
 * Created by siman on 3/2/16.
 */
public class NinjaSoul {
    public int id;
    public int sid;
    public int y;
    public int x;

    public NinjaSoul(int id, int y, int x) {
        this.id = id;
        this.sid = Utility.getId(y, x);
        this.y = y;
        this.x = x;
    }
}
