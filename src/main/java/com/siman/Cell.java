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
     * y座標
     */
    public int y;

    /**
     * x座標
     */
    public int x;

    /**
     * 現在のセルの状態
     */
    public int state;

    public int saveDangerValue;

    /**
     * 危険度合い
     */
    public int dangerValue;

    /**
     *
     */
    public int tempDangerValue;

    /**
     * 忍犬の密集度合い
     */
    public int dogValue;

    /**
     *
     */
    public int saveDogValue;

    /**
     * このセルから一番近い忍犬の距離
     */
    public int dogDist;

    public int soulValue;

    public Cell(int id) {
        this.id = id;
        this.y = id / Field.WIDTH;
        this.x = id % Field.WIDTH;
    }

    /**
     * セルの状態を初期化する
     */
    public void clear() {
        this.state = 0;
        this.dangerValue = 0;
        this.dogDist = PlayerInfo.INF;
        this.dogValue = 0;
        this.soulValue = 0;
        this.saveDangerValue = 0;
    }

    public void rollback() {
        this.dangerValue = this.saveDangerValue;
    }

    public void save() {
        this.saveDangerValue = this.dangerValue;
    }

    public void tempSave() {
        this.tempDangerValue = this.dangerValue;
    }

    public void tempRollback() {
        this.dangerValue = this.tempDangerValue;
    }

    /**
     * セルの座標を取得する
     *
     * @return
     */
    public Coord getCoord() {
        return new Coord(this.y, this.x);
    }
}
