package main.java.com.siman;

/**
 * Created by siman on 3/1/16.
 */
public class Ninja {
    /**
     * 現在のy座標
     */
    public int y;

    /**
     * 現在のx座標
     */
    public int x;

    /**
     * 保存したy座標
     */
    public int saveY;

    /**
     * 保存したx座標
     */
    public int saveX;

    /**
     * 現在の状態を保存
     */
    public void saveStatus() {
        this.saveY = this.y;
        this.saveX = this.x;
    }

    /**
     * 保存した状態に戻す
     */
    public void rollback() {
        this.y = this.saveY;
        this.x = this.saveX;
    }
}
