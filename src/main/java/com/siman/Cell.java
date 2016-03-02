package main.java.com.siman;

/**
 * Created by siman on 3/2/16.
 */
public class Cell {
    /**
     * 現在のセルの状態
     */
    public int state;

    /**
     * 危険度合い
     */
    public int danger;

    public void clear() {
        this.state = 0;
        this.danger = 0;
    }
}
