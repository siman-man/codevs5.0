package main.java.com.siman;

/**
 * Created by siman on 3/2/16.
 */
public class Cell {
    /**
     * セルのID
     */
    public int id;

    /**
     * 現在のセルの状態
     */
    public int state;

    /**
     * 危険度合い
     */
    public int danger;

    public Cell(int id) {
        this.id = id;
    }

    public void clear() {
        this.state = 0;
        this.danger = 0;
    }

    public Coord getCoord() {
        int y = this.id / Field.WIDTH;
        int x = this.id % Field.WIDTH;

        return new Coord(y,x);
    }
}
