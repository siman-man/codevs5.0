package main.java.com.siman;

import test.java.com.siman.Utility;

import java.util.Comparator;

/**
 * Created by siman on 3/2/16.
 */
public class Dog {
    public int id;
    public int did;
    public int y;
    public int x;
    public int targetId;
    public int targetDist;
    public int saveY;
    public int saveX;
    public int firstY;
    public int firstX;
    public int tempSaveY;
    public int tempSaveX;

    public Dog(int id, int y, int x) {
        this.id = id;
        this.did = Utility.getId(y, x);
        this.y = y;
        this.x = x;
    }

    public void saveStatus() {
        this.saveY = this.y;
        this.saveX = this.x;
    }

    public void rollback() {
        this.y = this.saveY;
        this.x = this.saveX;
    }

    public void saveFirst() {
        this.firstY = this.y;
        this.firstX = this.x;
    }

    public void rollbackFirst() {
        this.y = this.firstY;
        this.x = this.firstX;
    }

    public void tempSave() {
        this.tempSaveY = this.y;
        this.tempSaveX = this.x;
    }

    public void tempRollback() {
        this.y = this.tempSaveY;
        this.x = this.tempSaveX;
    }

    public String toString() {
        int ty = this.targetId / Field.WIDTH;
        int tx = this.targetId % Field.WIDTH;
        return String.format("target = (%d, %d), dogY = %d, dogX = %d", ty, tx, this.y, this.x);
    }
}

class DogComparator implements Comparator<Dog> {
    @Override
    public int compare(Dog dogA, Dog dogB) {
        return (100 * dogA.targetDist + dogA.id) - (100 * dogB.targetDist + dogB.id);
    }
}
