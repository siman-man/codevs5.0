package main.java.com.siman;

/**
 * Created by siman on 3/2/16.
 */
public class Direction {
    public int toInteger(char direct) {
        switch(direct){
            case 'U':
                return 0;
            case 'R':
                return 1;
            case 'D':
                return 2;
            case 'L':
                return 3;
            default:
                throw new RuntimeException();
        }
    }
}
