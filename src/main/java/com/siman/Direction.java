package main.java.com.siman;

/**
 * Created by siman on 3/2/16.
 */
public class Direction {
    public static int toInteger(char direct) {
        switch (direct) {
            case 'U':
                return 0;
            case 'R':
                return 1;
            case 'D':
                return 2;
            case 'L':
                return 3;
            case 'N':
                return 4;
            default:
                throw new RuntimeException("予期せぬ入力");
        }
    }

    public static char toChar(int direct) {
        switch (direct) {
            case 0:
                return 'U';
            case 1:
                return 'R';
            case 2:
                return 'D';
            case 3:
                return 'L';
            case 4:
                return 'N';
            default:
                throw new RuntimeException("予期せぬ入力");
        }
    }
}
