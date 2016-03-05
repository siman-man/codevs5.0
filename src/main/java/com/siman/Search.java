package main.java.com.siman;

/**
 * Created by siman on 3/5/16.
 */
public class Search {
    // 通常時
    public static char[][] LIST = {
            // 0マス (1パターン)
            {'N'},

            // 1マス移動 (4パターン)
            {'U'},
            {'R'},
            {'D'},
            {'L'},

            // 2マス移動  (12パターン)
            {'U', 'U'},
            {'U', 'R'},
            {'U', 'L'},

            {'R', 'U'},
            {'R', 'R'},
            {'R', 'D'},

            {'D', 'R'},
            {'D', 'D'},
            {'D', 'L'},

            {'L', 'U'},
            {'L', 'D'},
            {'L', 'L'},

            // 3マス移動
            {'U', 'U', 'U'},
            {'U', 'U', 'L'},
            {'U', 'U', 'R'},
    };
}
