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

    public Dog(int id, int y, int x) {
        this.id = id;
        this.did = Utility.getId(y, x);
        this.y = y;
        this.x = x;
    }
}

class DogComparator implements Comparator<Dog> {
    @Override
    public int compare(Dog dogA, Dog dogB) {
        return dogA.targetDist - dogB.targetDist;
    }
}
